package a8.versions

import a8.versions.ast.{StringIdentifier, VariableIdentifier}
import a8.versions.model.{CompositeBuild, ResolvedModule}
import m3.fs.FileSystem
import m3.predef._
import net.model3.chrono.DateTime
import net.model3.util.Versioning

import java.net.InetAddress
import scala.collection.JavaConverters._

class GradleGenerator(codeRootDir: m3.fs.Directory) {

  val versioning: Versioning = Versioning.getVersioning(getClass)

  object files {
    val settingsDotGradleFile: FileSystem#TFile = codeRootDir \ "settings.gradle"
  }

  lazy val compositeBuild = CompositeBuild(codeRootDir)

  lazy val firstRepo: model.ResolvedRepo = compositeBuild.resolvedRepos.head

  lazy val scalaVersion: String = firstRepo.versionDotPropsMap("scalaLibVersion")
  lazy val scalaMajorVersion: String = scalaVersion.splitList("\\.").take(2).mkString(".")

//  lazy val scalaJsCrossProjectVersion: String =
//    getVersionFromDotProperties("scalaJsCrossProjectVersion", "1.0.0")
//  lazy val scalaJsVersion: String =
//    getVersionFromDotProperties("scalaJsVersion", "1.2.0")
//  lazy val coursierJsVersion: String =
//    getVersionFromDotProperties("coursierVersion", "2.0.0-RC6")
//  lazy val sbtDependencyGraphVersion: String =
//    getVersionFromDotProperties("sbtDependencyGraphVersion", "0.9.0")
//  lazy val slf4jNopVersion: String =
//    getVersionFromDotProperties("slf4jNopVersion", "1.7.21")
//  lazy val sbtGitVersion: String =
//    getVersionFromDotProperties("sbtGitVersion", "0.9.3")
//  lazy val sbtA8Version: String =
//    getVersionFromDotProperties("sbtA8Version", "1.1.0-20191220_1208")
//  lazy val sbtBloopVersion: String =
//    getVersionFromDotProperties("sbtBloopVersion", "1.4.4")
//  lazy val sbtVersion: String =
//    getVersionFromDotProperties("sbtVersion", "1.3.10")
//  lazy val partialUnificationVersion: String =
//    getVersionFromDotProperties("partialUnificationVersion", "1.1.2")
//
//
//  lazy val singleRepoOpt: Option[model.ResolvedRepo] =
//    if ( compositeBuild.resolvedRepos.size == 1 ) compositeBuild.resolvedRepos.headOption
//    else None
//
//  lazy val singleRepo: Boolean = singleRepoOpt.isDefined
//

  /*
    generate the following files

    build.sbt
    project/build.properties
    project/plugins.sbt
    project/Common.scala

  */
  def run(): Unit = {
    generateSettingsDotGradle()
//    setupProjectDirectory()
//    generateBuildProperties()
//    generatePlugins()
//    generateCommon()
  }

  def generateSettingsDotGradle() = {

    files.settingsDotGradleFile.write(generateSettingsDotGradleStr)

    compositeBuild.resolvedModules.foreach { module =>
      generateBuildDotGradle(module)
    }

  }

  def generateSettingsDotGradleStr = {
s"""

rootProject.name = 'root'
include ${
    compositeBuild
      .resolvedModules
      .map { module =>
        "'" + module.sbtName + "'"
      }
      .mkString(" ")
}
"""
  }


  def generateBuildDotGradle(module: ResolvedModule) = {
    codeRootDir
      .subdir(module.resolveDirectory)
      .file("build.gradle")
      .write(generateBuildDotGradleStr(module))
  }


  def generateBuildDotGradleStr(module: ResolvedModule) = {
s"""

plugins {
    id 'scala'
}

repositories {
    mavenCentral()
}

configurations {
    provided
}

sourceSets {
    main { compileClasspath += configurations.provided }
}

dependencies {
${
  module.dependencies.map { dependency =>

    val artifactSuffix =
      dependency.scalaArtifactSeparator match {
        case "%" =>
          ""
        case "%%" | "%%%" =>
          "_2.12"
      }

    val depType =
      dependency.configuration match {
        case None | Some("compile") =>
          "implementation"
        case Some("test") =>
          "testImplementation"
        case Some("provided") =>
          "provided"
      }

    val version =
      dependency.version match {
        case StringIdentifier(v) =>
          v
        case VariableIdentifier(v) =>
          "$" + v
      }

    s"  ${depType} '${dependency.organization}:${dependency.artifactName}${artifactSuffix}:${version}'"

  }.mkString("\n")
}
}

"""
  }


  def getVersionFromDotProperties(versionName: String, defaultValue: String): String =
    firstRepo.versionDotPropsMap.getOrElse(versionName, defaultValue)


}
