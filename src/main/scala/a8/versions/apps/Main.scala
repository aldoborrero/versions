package a8.versions.apps

import a8.appinstaller.AppInstallerConfig.LibDirKind
import a8.appinstaller.{AppInstaller, AppInstallerConfig, InstallBuilder}
import a8.versions.Build.BuildType
import a8.versions.{RepositoryOps, Version}
import a8.versions.Upgrade.LatestArtifact
import a8.versions.apps.Main.Conf
import org.rogach.scallop.{ScallopConf, Subcommand}
import a8.versions.predef._

object Main {

  case class Conf(args0: Seq[String]) extends ScallopConf(args0) {

    val resolve = new Subcommand("resolve") {
      val organization = opt[String](required = true, descr = "organization of the artifact to resolve")
      val artifact = opt[String](required = true, descr = "artifact name")
      val branch = opt[String](descr = "branch name")
      val version = opt[String](descr = "specific version")
    }

    val install = new Subcommand("install") {
      val organization = opt[String](required = true, descr = "organization of the artifact to resolve")
      val artifact = opt[String](required = true, descr = "artifact name")
      val branch = opt[String](required = true, descr = "branch name")
      val version = opt[String](descr = "specific version")
      var installDir = opt[String](descr = "the install directory", required = true)
    }

    addSubcommand(resolve)
    addSubcommand(install)

    verify()

  }


  def main(args: Array[String]): Unit = {
    val main = new Main(args)
//    new Main(Seq("resolve", "-o", "org", "-a", "art")).run()
//    new Main(Seq("resolve", "--organization", "a8", "--artifact", "a8-qubes-dist_2.12", "--branch", "master")).run()
//    new Main(Seq("--help")).run()
    main.run()
  }



}


class Main(args: Seq[String]) {


  implicit def buildType = BuildType.ArtifactoryBuild

  lazy val userHome = m3.fs.dir(System.getProperty("user.home"))
  lazy val a8Home = userHome \\ ".a8"
  lazy val a8VersionsCache = userHome \\ ".a8" \\ "versions" \\ "cache"

  lazy val conf = Conf(args)

  def run(): Unit = {
    if ( conf.subcommand.exists(_ == conf.resolve) ) {
      val r = conf.resolve
      runResolve(coursier.Module(r.organization.apply(), r.artifact.apply()), r.branch.toOption, r.version.toOption)
    } else {
      sys.error(s"don't know how to handle -- ${args}")
    }
  }





  def runResolve(module: coursier.Module, branch: Option[String], version: Option[String]): Unit = {

    val (resolvedVersion, latest) =
      (branch, version) match {
        case (None, None) =>
          sys.error("must supply a branch or version")
        case (Some(_), Some(_)) =>
          sys.error("must supply a branch or version not both")
        case (Some(b), None) =>
          LatestArtifact(module, b).resolveVersion(Map()) -> Some(s"latest_${b}.json")
        case (None, Some(v)) =>
          Version.parse(v).get -> None
      }

    println(s"using version ${resolvedVersion}")

    val tree = RepositoryOps.resolveDependencyTree(module, resolvedVersion)

    val aic =
      AppInstallerConfig(
        organization = module.organization,
        artifact = module.name,
        version = resolvedVersion.toString,
        branch = None,
      )

    val installBuilder = InstallBuilder(aic)

    val inventoryDir = a8VersionsCache \\ module.organization \\ module.name

    inventoryDir.makeDirectories()

    val inventoryFiles =
      Some(inventoryDir \ s"${resolvedVersion.toString}.json") ++ latest.map(inventoryDir \ _)

    val inventoryJson = toJsonPrettyStr(installBuilder.inventory)

    inventoryFiles.foreach(_.write(inventoryJson))

    println(s"resolved ${inventoryFiles}")

  }



  def runInstall(module: coursier.Module, branch: Option[String], version: Option[String], installDir: String): Unit = {

    val (resolvedVersion, latest) =
      (branch, version) match {
        case (None, None) =>
          sys.error("must supply a branch or version")
        case (Some(_), Some(_)) =>
          sys.error("must supply a branch or version not both")
        case (Some(b), None) =>
          LatestArtifact(module, b).resolveVersion(Map()) -> Some(s"latest_${b}.json")
        case (None, Some(v)) =>
          Version.parse(v).get -> None
      }

    val config =
      AppInstallerConfig(
        organization = module.organization,
        artifact = module.name,
        branch = None,
        version = resolvedVersion.toString,
        installDir = Some(installDir),
        libDirKind = Some(LibDirKind.Repo),
        webappExplode = Some(true),
      )

    AppInstaller(config).execute()

  }


}