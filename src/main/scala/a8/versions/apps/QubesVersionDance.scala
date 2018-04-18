package a8.versions.apps

import java.util.Date

object QubesVersionDance {

  import a8.versions.Build._

  def main(args: Array[String]) = {

     implicit val buildType = BuildType.ArtifactoryBuild
//      implicit val buildType = BuildType.LocalBuild

    val start = new Date()

    publish(codeHome \\ "model3")
    upgradeAndPublish(codeHome \\ "manna")
    upgrade(codeHome \\ "qubes")

    println(s"started at ${start}")
    println(s"started at ${new Date}")
    println(s"${(System.currentTimeMillis - start.getTime)/1000} seconds processing time")

  }

}
