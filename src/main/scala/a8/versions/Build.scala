package a8.versions

import a8.shared.FileSystem
import a8.shared.FileSystem.Directory

import java.util.Date
import a8.versions.apps.{GenerateBuildDotSbt, Main}

object Build {

  sealed case class BuildType(
    sbtCommand: String,
    useLocalRepo: Boolean
  )

  object BuildType {
    val LocalBuild = BuildType("publishLocal", true)
    val ArtifactoryBuild = BuildType("publish", false)
  }

  def upgrade(dir: Directory, repositoryOps: RepositoryOps)(implicit buildType: BuildType) = {
    UpgradeVersions.runUpgrade(dir.file("version.properties"), repositoryOps)
    val g = new BuildDotSbtGenerator(dir)
    g.run()
  }

  def upgradeAndPublish(dir: Directory, repositoryOps: RepositoryOps)(implicit buildType: BuildType, buildTimestamp: Option[BuildTimestamp]) = {
    upgrade(dir, repositoryOps)
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


  lazy val codeHome = FileSystem.dir(System.getProperty("user.home") + "/code")

}
