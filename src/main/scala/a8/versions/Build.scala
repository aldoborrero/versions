package a8.versions

import java.util.Date

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

  def upgrade(dir: Directory)(implicit buildType: BuildType) = {
    UpgradeVersionsMain.runUpgrade(dir.file("version.properties"))
  }

  def upgradeAndPublish(dir: Directory)(implicit buildType: BuildType) = {
    upgrade(dir)
    publish(dir)
  }

  def publish(dir: Directory)(implicit buildType: BuildType) = {
    Exec("sbt", buildType.sbtCommand)
      .inDirectory(dir)
      .execInline()
  }


  lazy val codeHome = dir(System.getProperty("user.home") + "/code")

}
