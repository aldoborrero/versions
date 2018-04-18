package a8.versions.apps

import a8.common.HoconOps._
import a8.common.CommonOps._
import a8.versions.BuildDotSbtGenerator
import a8.versions.model.CompositeBuild

object GenerateBuildDotSbt extends App {

  lazy val homeDir = m3.fs.dir(System.getProperty("user.home"))

//  run("ahs_aggregate", homeDir \\ "code/ahs/aggregate")

  run("manna", homeDir \\ "code/manna")
  run("model3", homeDir \\ "code/model3")
  run("qubes", homeDir \\ "code/qubes")


  def run(name: String, d: m3.fs.Directory) = {
    val g = new BuildDotSbtGenerator(name, d)
    g.run()
    println(s"run complete for ${d}")
  }

}
