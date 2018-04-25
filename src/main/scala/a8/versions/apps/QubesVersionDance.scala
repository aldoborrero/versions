package a8.versions.apps

import java.util.Date

import a8.versions.BuildTimestamp

object QubesVersionDance {

  import a8.versions.Build._

  def main(args: Array[String]) = {

    implicit val buildType = BuildType.ArtifactoryBuild
    implicit val buildTimestamp = Some(BuildTimestamp.now())

    println(s"using buildTimestamp = ${buildTimestamp}")

    val start = new Date()

    publish(codeHome \\ "model3")
    upgradeAndPublish(codeHome \\ "manna")
    upgrade(codeHome \\ "qubes")

    println(s"started at ${start}")
    println(s"started at ${new Date}")
    println(s"${(System.currentTimeMillis - start.getTime)/1000} seconds processing time")

  }

}
