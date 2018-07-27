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

  def upgrade(dir: Directory)(implicit buildType: BuildType) = {
    UpgradeVersionsMain.runUpgrade(dir.file("version.properties"))
    val g = new BuildDotSbtGenerator( dir)
    g.run()
  }

  def upgradeAndPublish(dir: Directory)(implicit buildType: BuildType, buildTimestamp: Option[BuildTimestamp]) = {
    upgrade(dir)
    publish(dir)
  }

  def publish(dir: Directory)(implicit buildType: BuildType, buildTimestamp: Option[BuildTimestamp]) = {

    val buildNumberProperty =
      buildTimestamp
        .map(ts => s"-DbuildNumber=${ts}_${GitOps.branchName(dir)}")

    Exec(List("sbt") ++ buildNumberProperty ++ List(buildType.sbtCommand))
      .inDirectory(dir)
      .execInline()
  }


  lazy val codeHome = dir(System.getProperty("user.home") + "/code")

}
