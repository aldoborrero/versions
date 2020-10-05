package a8.versions.apps

import a8.common.CascadingHocon
import a8.common.HoconOps._
import a8.common.CommonOps._
import a8.versions.BuildDotSbtGenerator
import a8.versions.model.CompositeBuild
import m3.predef._

import scala.util.Try

object GenerateBuildDotSbt extends App {

  lazy val homeDir = m3.fs.dir(System.getProperty("user.home"))


  lazy val path =
    Try {
      CascadingHocon.config.readPath[String]("GenerateBuildDotSbt.path")
    }

  lazy val pathi =
    Try {
      CascadingHocon.config.readPath[Iterable[String]]("GenerateBuildDotSbt.paths")
    }

  lazy val paths =
    ( path.toOption ++ pathi.toOption.toIterable.flatten )
      .map ( homeDir.subdir )

//  run("ahs_aggregate", homeDir \\ "code/ahs/aggregate")

//  run(homeDir \\ "code/odin")
//  run(homeDir \\ "code/model3")
//  run(homeDir \\ "code/ahs-scala")
//    run(homeDir \\ "code/accur8/monadic-html")
//  run(homeDir \\ "code/accur8/odin")
//  run(homeDir \\ "code/accur8/hermes")
//  run(homeDir \\ "code/accur8/model3")
//  run(homeDir \\ "code/build-tools/a8-recipe")
//  run(homeDir \\ "code/build-tools/honeybadger")
//  run(homeDir \\ "code/s3-backup")

//  run(homeDir \\ "code/aggregates")

  paths.foreach { p =>
    run(p)
  }

  def run(d: m3.fs.Directory) = {
    val g = new BuildDotSbtGenerator(d)
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
