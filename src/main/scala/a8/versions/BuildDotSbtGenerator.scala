package a8.versions

import a8.versions.apps.Main
import a8.versions.model.CompositeBuild


class BuildDotSbtGenerator(codeRootDir: m3.fs.Directory, config: Main.Config) {

  object files {
    val buildDotSbtFile = codeRootDir \ "build.sbt"
    val buildDotPropertiesFile = codeRootDir \\ "project" \ "build.properties"
    val plugins = codeRootDir \\ "project" \ "plugins.sbt"
    val common = codeRootDir \\ "project" \ "Common.scala"
  }

  def run() = {
    files.buildDotSbtFile.write(content)

    files.plugins.parentDir.makeDirectories()

    if ( !files.buildDotPropertiesFile.exists )
      files.buildDotPropertiesFile.write(s"sbt.version=${config.sbtVersion}\n")

    files.plugins.write(
      """

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.22")
addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.1")
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.9.0")

resolvers += "a8-sbt-plugins" at "https://accur8.artifactoryonline.com/accur8/sbt-plugins/"
credentials += Credentials(Path.userHome / ".sbt" / "credentials")

addSbtPlugin("com.lihaoyi" % "workbench" % "0.4.0")

libraryDependencies += "org.slf4j" % "slf4j-nop" % "1.7.21"
addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.9.3")

addSbtPlugin("a8" % "sbt-a8" % "1.1.0-20180412_1831")

addSbtPlugin("ch.epfl.scala" % "sbt-bloop" % "1.0.0-M10")

      """)

    files.common.write("""
import sbt._
import Keys._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.cross.{CrossProject, CrossType}

object Common extends a8.sbt_a8.SharedSettings with a8.sbt_a8.HaxeSettings {

  def crossProject(artifactName: String, dir: java.io.File, id: String) =
    CrossProject(id, dir, CrossType.Full)
      .settings(settings: _*)
      .settings(Keys.name := artifactName)
      .jsSettings(jsSettings: _*)
      .jvmSettings(jvmSettings: _*)


  def jsProject(artifactName: String, dir: java.io.File, id: String) =
    bareProject(artifactName, dir, id)
      .settings(jsSettings: _*)
      .enablePlugins(ScalaJSPlugin)

  override def jvmSettings: Seq[Def.Setting[_]] =
    super.jvmSettings ++
    Seq(
    )

}
    """.trim)
  }

  import a8.versions.model.impl.q
  import m3.Chord
  import m3.Chord._

/*
  generate the following files

  build.sbt
  project/build.properties
  project/plugins.sbt

*/


  lazy val compositeBuild = CompositeBuild(codeRootDir)

  lazy val singleRepoOpt =
    if ( compositeBuild.resolvedRepos.size == 1 ) compositeBuild.resolvedRepos.headOption
    else None

  lazy val singleRepo = singleRepoOpt.isDefined

  lazy val firstRepo = compositeBuild.resolvedRepos.head

  lazy val scalaVersion = firstRepo.versionDotPropsMap("scalaVersion")

  lazy val content =
    s"""

${singleRepoOpt.flatMap(_.astRepo.header).getOrElse("")}

scalacOptions in Global ++= Seq("-deprecation", "-unchecked", "-feature")

resolvers in Global += "a8-repo" at "https://accur8.artifactoryonline.com/accur8/all/"

publishTo in Global := Some("a8-repo-publish" at "https://accur8.artifactoryonline.com/accur8/libs-releases-local/")

credentials in Global += Credentials(Path.userHome / ".sbt" / "credentials")

scalaVersion in Global := "${scalaVersion}"

organization in Global := "${firstRepo.astRepo.organization}"

version in Global := ${if ( singleRepo ) s"a8.sbt_a8.versionStamp(file(${q(".")}))" else q("1.0-SNAPSHOT") }


${
  compositeBuild.resolvedModules.map { module =>
s"""
lazy val ${module.sbtName} =
  Common
    .${module.resolveProjectType}Project("${module.resolveArtifactName}", file("${module.resolveDirectory}"), ${q(module.sbtName)})
${
  module.settingsLines.map("    " + _).mkString("\n")
}"""}
  .mkString("\n\n")
}${
    compositeBuild.resolvedModules.flatMap(_.subModuleLines) match {
    case l if l.isEmpty => ""
    case l =>
      l.mkString("\n\n","\n","\n")
  }
}
${
      if ( compositeBuild.resolvedModules.size == 1 ) ""
      else
      s"""
lazy val root_bloop =
  Common.jvmProject("root_bloop", file("target/root_bloop"), id = "root_bloop")
    .settings( publish := {} )
    ${
//      val aggregateMethod = "aggregate"
      val aggregateMethod = "dependsOn"
      compositeBuild.resolvedModules.flatMap(_.aggregateModules).toList.sorted.map(m => s".${aggregateMethod}(${m})").mkString("\n    ")
    }

lazy val root =
  Common.jvmProject("root", file("target/root"), id = "root")
    .settings( publish := {} )
    ${
      val aggregateMethod = "aggregate"
//      val aggregateMethod = "dependsOn"
      compositeBuild.resolvedModules.flatMap(_.aggregateModules).toList.sorted.map(m => s".${aggregateMethod}(${m})").mkString("\n    ")
    }

"""}

   """

}


//lazy val ${module.sbtName} =
//  Common.jvmProject(${q(module.sbtName)}, file("."), id = ${q(module.sbtName)})
//    .settings( publish := {} )
//    ${
//      val aggregateMethod = "aggregate"
////      val aggregateMethod = "dependsOn"
//      compositeBuild.resolvedModules.flatMap(_.aggregateModules).toList.sorted.map(m => s".${aggregateMethod}(${m})").mkString("\n    ")
//    }
