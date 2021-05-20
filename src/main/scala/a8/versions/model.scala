package a8.versions


import a8.common.CommonOps._
import play.api.libs.functional.syntax.unlift
import play.api.libs.json.{JsPath, Json, Reads, Writes}
import a8.common.Lenser.{Lens, LensImpl}
import play.api.libs.json.{JsPath, Reads, Writes}
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import a8.common.CommonOps._
import a8.common.CompanionGen
import m3.Chord
import Chord._
import a8.common.HoconOps._

object model {

  object impl {
    /** quote string */
    def q(s: String): Chord = {
      '"' + s + '"'
    }
  }

  import Json._

  object CompositeBuild {
    def apply(codeRoot: m3.fs.Directory): CompositeBuild = {

      val f = codeRoot \ "modules.conf"
      if ( f.exists ) {
        CompositeBuild(List(f.parentDir -> None))

      } else {

//        println(codeRoot)
//        println(codeRoot.entries.toList)

        val moduleConfs =
          codeRoot
            .subdirs
            .map(_ \ "modules.conf")
//            .map{ p => println(p) ; p }
            .filter(_.exists)

        if ( moduleConfs.isEmpty ) {
          sys.error("no modules.conf found")
        }

        val repos =
          moduleConfs
            .map { f =>
              f.parentDir -> Some(RepoPrefix(f.parentDir.name, f.parentDir.name))
            }
        CompositeBuild(repos)
      }
    }
  }

  case class CompositeBuild(
    repos: Iterable[(m3.fs.Directory, Option[RepoPrefix])]
  ) {

    lazy val resolvedRepos =
      repos.map(t => ResolvedRepo(this, t._1, t._2))

    lazy val resolvedModules =
      for (
        repo <- resolvedRepos ;
        module <- repo.astRepo.modules
      ) yield ResolvedModule(repo, module)
  }

  case class ResolvedRepo(
    compositeBuild: CompositeBuild,
    repoRootDir: m3.fs.Directory,
    prefix: Option[RepoPrefix],
  ) {

    def findDependencyViaSbtName(sbtName: String) = {
      compositeBuild
        .resolvedModules
        .find(_.sbtName == sbtName)
        .getOrError(s"unable to find dependsOn for ${sbtName}")
    }


    lazy val astRepo =
      parseHocon(repoRootDir.file("modules.conf").readText)
        .read[ast.Repo]

    lazy val versionDotPropsMap: Map[String, String] =
      repoRootDir
        .file("version.properties")
        .readText
        .linesIterator
        .map(_.trim)
        .filterNot(l => l.length == 0 || l.startsWith("#"))
        .flatMap {
          _.splitList("=", limit = 2, dropEmpty = false) match {
            case List(l,r) => Some(l -> r)
            case _ => None
          }
        }
        .toMap

    lazy val variables: Iterable[(String, String)] =
      versionDotPropsMap
        .filterNot { case (k,v) =>
          val kl = k.toLowerCase
          kl.endsWith(".upgrade") || kl =:= "this"
        }

  }

  case class RepoPrefix(
    dir: String,
    prefix: String
  )

  case class ResolvedModule(
    repo: ResolvedRepo,
    astModule: ast.Module
  ) {

    def includeInGradle: Boolean =
      astModule.projectType match {
        case None | Some("cross") =>
          true
        case Some("js") | Some("haxe") | Some("sass") =>
          false
      }

    lazy val organization = repo.astRepo.organization

    lazy val dependentModulesInComposite: Set[ResolvedModule] =
      repo
        .compositeBuild
        .resolvedModules
        .filter { m =>
          dependenciesByOrgArtifact.contains(m.organization -> m.resolveArtifactName)
        }
        .toSet

    lazy val dependenciesByOrgArtifact: Map[(String,String),ast.Dependency] =
      rawResolvedDependencies
        .map(d => (d.organization -> d.artifactName) -> d)
        .toMap

    lazy val prefix =
      repo.prefix.map(_.prefix + "_").getOrElse("")

    def prefixName(name: String): String =
      prefix + name

    lazy val gradleName = {
      val s = resolveDirectory.splitList("/")
      if ( s.length > 1 ) {
        s.dropRight(1).mkString("/") + ":" + s.last
      } else {
        resolveDirectory
      }
    }

    lazy val dependsOn =
      astModule
        .dependsOn
        .map(prefixName) ++ dependentModulesInComposite.map(_.sbtName)

    lazy val sbtName = prefixName(astModule.sbtName)

    lazy val aggregateModules =
      subModuleNames.getOrElse(List(sbtName))

    lazy val subModuleNames =
      submodules.map(_.map(_._1))

    lazy val subModuleLines =
      submodules.toList.flatMap(_.map(sm => "lazy val " + sm._1 + " = " + sm._2))

    lazy val submodules =
      if ( resolveProjectType == "cross" ) Some(List("jvm", "js").map(s => (sbtName + s.toUpperCase, sbtName + "." + s.toLowerCase)))
      else None

    lazy val resolveProjectType = astModule.projectType.getOrElse("jvm")
    lazy val resolveArtifactName = astModule.artifactName.getOrElse(astModule.sbtName)
    lazy val resolveDirectory: String = repo.prefix.map(_.dir + "/").getOrElse("") + astModule.directory.getOrElse(astModule.sbtName)
    lazy val dependencies: Iterable[ast.Dependency] =
      rawResolvedDependencies
        .filterNot { d =>
          dependentModulesInComposite.exists(m => m.organization == d.organization && m.resolveArtifactName == d.artifactName)
        }

    lazy val rawResolvedDependencies: Iterable[ast.Dependency] = {
      astModule
        .dependencies
        .toList
        .flatMap(d => SbtDependencyParser.parse(d))
    }
    lazy val resolveJvmDependencies: Iterable[ast.Dependency] = {
      astModule
        .jvmDependencies
        .toList
        .flatMap(d => SbtDependencyParser.parse(d))
    }
    lazy val resolveJsDependencies: Iterable[ast.Dependency] = {
      astModule
        .jsDependencies
        .toList
        .flatMap(d => SbtDependencyParser.parse(d))
    }


    lazy val allDependencyLines = {
      val versionDotPropsMap = repo.versionDotPropsMap
      (
        dependencyLines("settings", dependencies, versionDotPropsMap) ++
          dependencyLines("jvmSettings", resolveJvmDependencies, versionDotPropsMap) ++
          dependencyLines("jsSettings", resolveJsDependencies, versionDotPropsMap)
      )
    }

    lazy val extraSettingsLines = {
      astModule.extraSettings match {
        case None =>
          Nil
        case Some(es) =>
          val lines = es.linesIterator.filter(_.trim.length > 0).toList
          val indent = lines.head.indexOf(lines.head.trim)
          val unindentedLines =
            lines.map(_.zipWithIndex.dropWhile(t => t._1.isWhitespace && t._2 < indent).map(_._1).mkString)
          unindentedLines
      }
    }

    lazy val settingsLines = {
      val dependsOnLines = dependsOn.map(d => s".dependsOn(${d})")
      val dependenciesLines = allDependencyLines
      dependsOnLines ++ dependenciesLines ++ extraSettingsLines
    }


    def dependencyLines(settingName: String, deps: Iterable[ast.Dependency], versionDotPropsMap: Map[String,String]): Iterable[String] = {
      if ( deps.nonEmpty ) {
        val header = List(
          s".${settingName}(",
          "  libraryDependencies ++= Seq("
        )
        val dependenciesLines = deps.map(_.asSbt(versionDotPropsMap)).map("    " + _.trim + ",")

        val trailer = List("  )", ")")

        header ++ dependenciesLines ++ trailer

      } else {
        Nil
      }
    }

  }


}
