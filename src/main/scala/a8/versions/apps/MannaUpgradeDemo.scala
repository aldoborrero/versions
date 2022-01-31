package a8.versions.apps

import java.util.Date

object MannaUpgradeDemo {

  import a8.versions.Build._

  def main(args: Array[String]) = {

     implicit val buildType = BuildType.ArtifactoryBuild
//      implicit val buildType = BuildType.LocalBuild

    val start = new Date()

    upgrade(codeHome \\ "manna")

    println(s"started at ${new Date}")
    println(s"${(System.currentTimeMillis - start.getTime)/1000} seconds processing time")

  }

}
