package a8.versions

import java.util.Date

import a8.versions.apps.GenerateBuildDotSbt
import m3.Exec
import m3.fs._

object Build {

  sealed case class BuildType(
    sbtCommand: String,
    useLocalRepo: Boolean
  )

  object BuildType {
    val LocalBuild = BuildType("publishLocal", true)
    val ArtifactoryBuild = BuildType("publish", false)
  }

  def upgrade(dir: Directory, name: Option[String] = None)(implicit buildType: BuildType) = {
    val resolvedName = name.getOrElse(dir.name)
    UpgradeVersionsMain.runUpgrade(dir.file("version.properties"))
    val g = new BuildDotSbtGenerator(resolvedName, dir)
    g.run()
  }

  def upgradeAndPublish(dir: Directory, name: Option[String] = None)(implicit buildType: BuildType) = {
    upgrade(dir, name)
    publish(dir)
  }

  def publish(dir: Directory)(implicit buildType: BuildType) = {
    Exec("sbt", buildType.sbtCommand)
      .inDirectory(dir)
      .execInline()
  }


  lazy val codeHome = dir(System.getProperty("user.home") + "/code")

}
