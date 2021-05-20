package a8.versions.apps

import a8.common.CascadingHocon
import a8.common.HoconOps._
import a8.versions.{BuildDotSbtGenerator, GradleGenerator}

import scala.util.Try

object GenerateGradle {

  lazy val homeDir = m3.fs.dir(System.getProperty("user.home"))


  import GenerateBuildDotSbt.paths

//  run("ahs_aggregate", homeDir \\ "code/ahs/aggregate")

//  run(homeDir \\ "code/odin")
//  run(homeDir \\ "code/model3")
//  run(homeDir \\ "code/ahs-scala")
//  run(homeDir \\ "code/accur8/monadic-html")
//  run(homeDir \\ "code/accur8/odin")
//  run(homeDir \\ "code/accur8/hermes")
//  run(homeDir \\ "code/accur8/model3")
//  run(homeDir \\ "code/build-tools/a8-recipe")
//  run(homeDir \\ "code/build-tools/honeybadger")
//  run(homeDir \\ "code/s3-backup")

//  run(homeDir \\ "code/aggregates")

  def main(args: Array[String]): Unit = {
    paths.foreach { p =>
      run(p)
    }
  }

  def run(d: m3.fs.Directory) = {
    val g = new GradleGenerator(d)
    println(s"running ${d.canonicalPath}")
    try {
      g.run()
      println(s"run complete for ${d}")
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }

}
