package a8.versions


import a8.shared.FileSystem
import a8.shared.FileSystem.Directory
import a8.versions.ast.{StringIdentifier, VariableIdentifier}
import a8.versions.model.{CompositeBuild, ResolvedModule}

import java.net.InetAddress
import a8.shared.SharedImports._

class GradleGenerator(codeRootDir: Directory) {

  val versioning: Versioning = Versioning(getClass)

  object files {
    val settingsDotGradleFile: FileSystem.File = codeRootDir \ "settings.gradle"
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

    compositeBuild.resolvedModules.filter(_.includeInGradle).foreach { module =>
      generateBuildDotGradle(module)
    }

  }

  def generateSettingsDotGradleStr = {
s"""

rootProject.name = 'root'
${
    compositeBuild
      .resolvedModules
      .filter(_.includeInGradle)
      .map { module =>
        s"include '${module.gradleName}'"
      }
      .mkString("\n")
}


def repoProperty(String name) {
  def props = new Properties()
  file(System.getProperty("user.home") + "/.a8/repo.properties").withInputStream { props.load(it) }
  return props.getProperty(name)
}

dependencyResolutionManagement {
  repositories {
  //    mavenCentral()
      maven {
          url repoProperty("repo_url")
          credentials {
              username = repoProperty("repo_user")
              password = repoProperty("repo_password")
          }
      }
  }
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
    id 'java-library'
}

configurations {
    provided
}


sourceSets {
  main {
    compileClasspath += configurations.provided
    java {
      srcDirs = []
    }
    scala {
      srcDirs = ['src/main/scala','src/main/java','shared/src//main/scala','jvm/src/main/scala']
    }
  }
  test {
    java {
      srcDirs = []
    }
    scala {
      srcDirs = ['src/test/scala','src/test/java','shared/src/test/scala','jvm/src/test/scala']
    }
  }
}


dependencies {
${
  val depsArtifacts =
    (module.dependencies ++ module.resolveJvmDependencies).map { dependency =>

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
            "api"
          case Some("test") =>
            "testImplementation"
          case Some("provided") =>
            "provided"
          case conf =>
            sys.error(s"unsupported dependency classifier of ${conf}")
        }

      val version =
        dependency.version match {
          case StringIdentifier(v) =>
            v
          case VariableIdentifier(v) =>
            firstRepo.versionDotPropsMap(v)
        }

      s"${depType} '${dependency.organization}:${dependency.artifactName}${artifactSuffix}:${version}'"

    }

  val depsProjects =
    module.dependsOn.map { dependsOn =>
      val sbtName =
        if ( dependsOn.endsWith("JVM") ) {
          dependsOn.substring(0, dependsOn.length-3)
        } else if ( dependsOn.endsWith("JS") ) {
          sys.error(s"this should not happen as we should not depend on JS projects -- ${dependsOn}")
        } else {
          dependsOn
        }

      val projectName = firstRepo.findDependencyViaSbtName(sbtName).gradleName

      s"api project(':${projectName}')"
    }

  (depsArtifacts ++ depsProjects)
    .mkString("  ", "\n  ", "")

}
}

"""
  }


  def getVersionFromDotProperties(versionName: String, defaultValue: String): String =
    firstRepo.versionDotPropsMap.getOrElse(versionName, defaultValue)


}
