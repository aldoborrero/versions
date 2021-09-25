package a8.versions

import java.net.InetAddress

import a8.versions.model.CompositeBuild
import m3.fs.FileSystem
import net.model3.chrono.DateTime
import net.model3.util.Versioning

import scala.collection.JavaConverters._
import m3.predef._

class BuildDotSbtGenerator(codeRootDir: m3.fs.Directory) {

  val versioning: Versioning = Versioning.getVersioning(getClass)

  val scalaComment: String = "//"
  val sbtComment: String = "#"

  object files {
    val buildDotSbtFile: FileSystem#TFile = codeRootDir \ "build.sbt"
    val projectDirectory: FileSystem#TDirectory = codeRootDir \\ "project"
    val buildDotPropertiesFile: FileSystem#TFile = projectDirectory \ "build.properties"
    val plugins: FileSystem#TFile = projectDirectory \ "plugins.sbt"
    val common: FileSystem#TFile = projectDirectory \ "Common.scala"
  }

  lazy val scalaJsCrossProjectVersion: String =
    getVersionFromDotProperties("scalaJsCrossProjectVersion", "1.0.0")
  lazy val scalaJsVersion: String =
    getVersionFromDotProperties("scalaJsVersion", "1.6.0")
  lazy val coursierJsVersion: String =
    getVersionFromDotProperties("coursierVersion", "2.0.0-RC6")
  lazy val sbtDependencyGraphVersion: String =
    getVersionFromDotProperties("sbtDependencyGraphVersion", "0.9.0")
  lazy val slf4jNopVersion: String =
    getVersionFromDotProperties("slf4jNopVersion", "1.7.21")
  lazy val sbtGitVersion: String =
    getVersionFromDotProperties("sbtGitVersion", "0.9.3")
  lazy val sbtA8Version: String =
    getVersionFromDotProperties("sbtA8Version", "1.1.0-20210925_1516")
  lazy val sbtVersion: String =
    getVersionFromDotProperties("sbtVersion", "1.5.3")
  lazy val partialUnificationVersion: String =
    getVersionFromDotProperties("partialUnificationVersion", "1.1.2")

  lazy val compositeBuild = CompositeBuild(codeRootDir)

  lazy val singleRepoOpt: Option[model.ResolvedRepo] =
    if ( compositeBuild.resolvedRepos.size == 1 ) compositeBuild.resolvedRepos.headOption
    else None

  lazy val singleRepo: Boolean = singleRepoOpt.isDefined

  lazy val firstRepo: model.ResolvedRepo = compositeBuild.resolvedRepos.head

  lazy val scalaVersion: String = firstRepo.versionDotPropsMap("scalaLibVersion")

  /*
    generate the following files

    build.sbt
    project/build.properties
    project/plugins.sbt
    project/Common.scala

  */
  def run(): Unit = {
    generateBuildSbt()
    setupProjectDirectory()
    generateBuildProperties()
    generatePlugins()
    generateCommon()
  }

  def getVersionFromDotProperties(versionName: String, defaultValue: String): String =
    firstRepo.versionDotPropsMap.getOrElse(versionName, defaultValue)

  def writeIfChanged(newContents: String, outputFile: FileSystem#TFile, comment: String): Unit = {
    val doWrite =
      if ( outputFile.exists ) {
        val currentContentWithoutComments: List[String] = outputFile.lines.filterNot(_.startsWith(comment))
        val newContentWithoutComments: List[String] = newContents.split("\n").toList.filterNot(_.startsWith(comment))
        val hasContentChanged = !currentContentWithoutComments.equals(newContentWithoutComments)
        hasContentChanged
      } else {
        true
      }
    if ( doWrite )
      outputFile.write(newContents)
  }

  def header(comment: String, overwrite: Boolean): String = {
    s"""
       |${if (overwrite) "DO NOT EDIT THIS FILE IT IS MACHINE GENERATED" else "THIS FILE WAS ORIGINALLY MACHINE GENERATED"}
       |
       |This file is generated from modules.conf using `a8-versions build_dot_sbt`
       |
       |It was generated at ${new DateTime} by ${System.getProperty("user.name")} on ${InetAddress.getLocalHost.getHostName}
       |
       |a8-versions build/versioning info follows
       |
       |${versioning.getProperties.asScala.map(t => t._1 + " : " + t._2).mkString("\n").indent("       ")}
       |
     """.stripMargin.linesIterator.map(comment + " " + _).mkString("\n")
  }

  def setupProjectDirectory(): Unit =
    files.projectDirectory.makeDirectories()

  def generateCommon(): Unit = {
    val newCommonContent =
      s"""
${header(scalaComment, true)}

import sbt._
import Keys._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport.fastOptJS
import sbtcrossproject.JVMPlatform
import scalajscrossproject.JSPlatform
import sbtcrossproject.CrossType

object Common extends a8.sbt_a8.SharedSettings with a8.sbt_a8.HaxeSettings with a8.sbt_a8.SassSettings with a8.sbt_a8.dobby.DobbySettings {

  def crossProject(artifactName: String, dir: java.io.File, id: String) =
    sbtcrossproject.CrossProject(id, dir)(JSPlatform, JVMPlatform)
      .crossType(CrossType.Full)
      .settings(settings: _*)
      .settings(Keys.name := artifactName)
      .platformsSettings(JSPlatform)(jsSettings: _*)
      .platformsSettings(JVMPlatform)(jvmSettings: _*)


  def jsProject(artifactName: String, dir: java.io.File, id: String) =
    bareProject(artifactName, dir, id)
      .settings(jsSettings: _*)
      .enablePlugins(ScalaJSPlugin)

  override def jvmSettings: Seq[Def.Setting[_]] =
    super.jvmSettings ++
    Seq(
    )

  override def jsSettings: Seq[Def.Setting[_]] =
    super.jsSettings ++
    Seq(
      Compile / fastOptJS / artifactPath := crossTarget.value / "classes" / "webapp" / "scripts" / ((fastOptJS / moduleName).value + "-fastopt.js")
    )

${repoAssistSource(false)}

}

    """.trim

    writeIfChanged(newCommonContent, files.common, scalaComment)
  }

  def generateBuildProperties(): Unit = {
    if ( !files.buildDotPropertiesFile.exists ) {
      header(sbtComment, false)
      val content =
        s"""
${header(sbtComment, false)}

sbt.version=${sbtVersion}

"""
      writeIfChanged(content, files.buildDotPropertiesFile, sbtComment)
    }
  }

  def generatePlugins(): Unit = {
    val newPluginsContent =
      s"""
${header(scalaComment, true)}

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.9.9")
addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.1.2")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "${scalaJsVersion}")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "${scalaJsCrossProjectVersion}")
//addSbtPlugin("io.get-coursier" % "sbt-coursier" % "${coursierJsVersion}")
//addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "${sbtDependencyGraphVersion}")

resolvers += "a8-sbt-plugins" at readRepoUrl()
credentials += readRepoCredentials()

//libraryDependencies += "org.slf4j" % "slf4j-nop" % "${slf4jNopVersion}"
//addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "${sbtGitVersion}")

// use this one if you need dobby
//addSbtPlugin("a8" % "sbt-a8" % "1.1.0-20210702_1452")
addSbtPlugin("a8" % "sbt-a8" % "${sbtA8Version}")

// This plugin can be removed when using Scala 2.13.0 or above
addSbtPlugin("org.lyranthe.sbt" % "partial-unification" % "${partialUnificationVersion}")

${repoAssistSource(false)}

"""

    writeIfChanged(newPluginsContent, files.plugins, scalaComment)

  }

  def generateBuildSbt(): Unit = {

    import a8.versions.model.impl.q

    lazy val buildSbtContent =
      s"""
${
        header(scalaComment, true)
}

${
  firstRepo
    .variables
    .map { case (k,v) =>
      s"""val ${k} = "${v}""""
    }
    .mkString("\n")
}${
  buildSbtContentWithoutHeader
}
"""

    lazy val buildSbtContentWithoutHeader =
      s"""${
  singleRepoOpt.flatMap(_.astRepo.header).getOrElse("")
}

scalacOptions in Global ++= Seq("-deprecation", "-unchecked", "-feature")

resolvers in Global += "a8-repo" at Common.readRepoUrl()

publishTo in Global := Some("a8-repo-releases" at Common.readRepoUrl())
//publishTo in Global := sonatypePublishToBundle.value
//credentials in Global += Credentials(Path.userHome / ".sbt" / "sonatype.credentials")

scalaVersion in Global := scalaLibVersion

organization in Global := "${firstRepo.astRepo.organization}"

version in Global := a8.sbt_a8.versionStamp(file("."))

versionScheme in Global := Some("strict")

serverConnectionType in Global := ConnectionType.Local

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
    writeIfChanged(buildSbtContent, files.buildDotSbtFile, scalaComment)
  }

  /** this is a copy of the RepoAssist object */
  def repoAssistSource(includeObject: Boolean) =
  s"""
${if ( includeObject ) "object RepoAssist {" else ""}

  def readRepoUrl() = readRepoProperty("repo_url")

  lazy val repoConfigFile = new java.io.File(System.getProperty("user.home") + "/.a8/repo.properties")

  lazy val repoProperties = {
    import scala.jdk.CollectionConverters._
    val props = new java.util.Properties()
    if ( repoConfigFile.exists() ) {
      val input = new java.io.FileInputStream(repoConfigFile)
      try {
        props.load(input)
      } finally {
        input.close()
      }
      props.asScala
    } else {
      sys.error("config file " + repoConfigFile + " does not exist")
    }
  }

  def readRepoProperty(propertyName: String): String = {
    repoProperties.get(propertyName) match {
      case Some(s) =>
        s
      case None =>
        sys.error("could not find property " + propertyName + " in " + repoConfigFile)
    }
  }

  def readRepoCredentials(): Credentials = {
    val repoUrl = new java.net.URL(readRepoUrl())
    Credentials(
      readRepoProperty("repo_realm"),
      repoUrl.getHost,
      readRepoProperty("repo_user"),
      readRepoProperty("repo_password"),
    )
  }

${if ( includeObject ) "}" else ""}
  """



}
