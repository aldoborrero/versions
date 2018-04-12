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

object model {

  object impl {
    /** quote string */
    def q(s: String): Chord = {
      '"' + s + '"'
    }
  }

  import Json._


  object Repo extends MxRepo

  @CompanionGen
  case class Repo(
    header: Option[String] = None,
    organization: String,
    modules: Iterable[Module]
  )


  object Module extends MxModule

  @CompanionGen
  case class Module(
    sbtName: String,
    projectType: Option[String],
    artifactName: Option[String],
    directory: Option[String],
    dependsOn: Iterable[String] = Nil,
    dependencies: Option[String],
    jvmDependencies: Option[String],
    jsDependencies: Option[String],
  ) {

    lazy val aggregateModules =
      subModuleNames.getOrElse(List(sbtName))

    lazy val subModuleNames =
      submodules.map(_.map(_._1))

    lazy val subModuleLines =
      submodules.toList.flatMap(_.map(sm => "lazy val " + sm._1 + " = " + sm._2))

    lazy val submodules =
      if ( projectType == Some("cross") ) Some(List("jvm", "js").map(s => (sbtName + s.toUpperCase, sbtName + "." + s.toLowerCase)))
      else None

    lazy val resolveProjectType = projectType.getOrElse("jvm")
    lazy val resolveArtifactName = artifactName.getOrElse(sbtName)
    lazy val resolveDirectory: String = directory.getOrElse(sbtName)
    lazy val resolveDependencies: Iterable[Dependency] = {
      dependencies
        .toList
        .flatMap(d => SbtDependencyParser.parse(d))
    }
    lazy val resolveJvmDependencies: Iterable[Dependency] = {
      jvmDependencies
        .toList
        .flatMap(d => SbtDependencyParser.parse(d))
    }
    lazy val resolveJsDependencies: Iterable[Dependency] = {
      jsDependencies
        .toList
        .flatMap(d => SbtDependencyParser.parse(d))
    }

    def allDependencyLines(versionDotPropsMap: Map[String,String]) = {
      (
        dependencyLines("settings", resolveDependencies, versionDotPropsMap) ++
          dependencyLines("jvmSettings", resolveJvmDependencies, versionDotPropsMap) ++
          dependencyLines("jsSettings", resolveJsDependencies, versionDotPropsMap)
      )
    }

    def dependencyLines(settingName: String, deps: Iterable[Dependency], versionDotPropsMap: Map[String,String]): Iterable[String] = {
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


  object Dependency extends MxDependency

  @CompanionGen
  case class Dependency(
    organization: String,
    scalaArtifactSeparator: String,
    artifactName: String,
    version: Identifier,
    configuration: Option[String],
    exclusions: Iterable[(String, String)],
  ) {
    import impl._

    def exclusionsAsSbt =
      exclusions
        .map { exclusion =>
          "exclude(" ~ q(exclusion._1) ~ "," * q(exclusion._2) ~ ")"
        }
        .mkChord(" ")

    val configurationAsSbt =
      configuration.map(c => "%" * q(c))

    def asSbt(versionDotPropsMap: Map[String,String]) = {
      q(organization) * scalaArtifactSeparator * q(artifactName) * "%" * version.resolve(versionDotPropsMap) * configurationAsSbt * exclusionsAsSbt
    }
  }


  sealed trait Identifier {
    def rawValue: String
    def resolve(versions: Map[String,String]): Chord
  }

  case class VariableIdentifier(name: String) extends Identifier {
    override def rawValue: String = name
    override def resolve(versions: Map[String,String]) = impl.q(versions(name))
  }

  case class StringIdentifier(value: String) extends Identifier {
    override def rawValue: String = value
    override def resolve(versions: Map[String,String]) = impl.q(value)
  }


}
