package a8.versions.apps

import a8.common.HoconOps._
import a8.common.CommonOps._
import a8.versions.BuildDotSbtGenerator
import a8.versions.model.CompositeBuild
import m3.predef._

object GenerateBuildDotSbt extends App {

  lazy val homeDir = m3.fs.dir(System.getProperty("user.home"))

//  run("ahs_aggregate", homeDir \\ "code/ahs/aggregate")

//  run(homeDir \\ "code/odin")
  run(homeDir \\ "code/model3")
//  run(homeDir \\ "code/ahs-scala")
//  run(homeDir \\ "code/build-tools/a8-recipe")
//  run(homeDir \\ "code/build-tools/honeybadger")
//  run(homeDir \\ "code/s3-backup")

//  run(homeDir \\ "code/aggregates")


  def run(d: m3.fs.Directory) = {
    val g = new BuildDotSbtGenerator(d)
    g.run()
    println(s"run complete for ${d}")
  }

}
