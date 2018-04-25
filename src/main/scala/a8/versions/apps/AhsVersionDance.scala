package a8.versions.apps

import java.util.Date

import a8.versions.{BuildTimestamp, UpgradeVersionsMain}
import m3.Exec
import m3.fs._

object AhsVersionDance {

  import a8.versions.Build._

  def main(args: Array[String]) = {

     implicit val buildType = BuildType.ArtifactoryBuild
//      implicit val buildType = BuildType.LocalBuild

    val start = new Date()

    implicit val buildTimestamp = Some(BuildTimestamp.now())

    publish(codeHome \\ "model3")
    upgradeAndPublish(codeHome \\ "manna")
    upgradeAndPublish(codeHome \\ "qubes")
    upgrade(codeHome \\ "ahs" \\ "scala")


    println(s"started at ${start}")
    println(s"started at ${new Date}")
    println(s"${(System.currentTimeMillis - start.getTime)/1000} seconds processing time")
  }

}
