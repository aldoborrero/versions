package a8.appinstaller


import java.nio.file.{Files, Path, Paths}
import java.util

import a8.versions.Build.BuildType
import a8.versions.RepositoryOps.DependencyTree
import a8.versions.{RepositoryOps, Version, ast}
import a8.versions.ast.Dependency
import m3.fs._

import collection.JavaConverters._
import predef._
import a8.common.logging.Logging

case class InstallBuilder(
  config: AppInstallerConfig
) extends Logging {

  lazy val unresolvedArtifact = config.unresolvedArtifact

  lazy val appDir = config.resolvedAppDir

  def build(): Unit = {
    buildLibDir()
    val jarFileName = config.artifactId + "-" + rootVersion.toString + ".jar"
    val jarFile =
      dependencyResult
        .localArtifacts
        .find(f => f.getName == jarFileName)
        .getOrElse(sys.error(s"unable to find ${jarFileName} in dependency tree"))
    JarMetadata.process(appDir, jarFile)
  }

  lazy val dependencyResult: DependencyTree =
    RepositoryOps.resolveDependencyTree(unresolvedArtifact.asCoursierModule, rootVersion)(BuildType.ArtifactoryBuild)

  private def buildLibDir() {
    val libDir: Directory = appDir \\ "lib"
    libDir.makeDirectories
    libDir.entries.foreach {
      case f: File => f.delete()
      case d: Directory => d.deleteTree()
    }

    for (fromFile <- dependencyResult.localArtifacts) {
      val toFile: File = libDir \ fromFile.getName
      if ( config.symlinks ) {
        logger.debug("symlinking artifact " + fromFile)
        val link: Path = Paths.get(toFile.getCanonicalPath)
        val target: Path = Paths.get(fromFile.getCanonicalPath)
        Files.createSymbolicLink(link, target)
      } else {
        logger.debug("copying artifact " + fromFile)
        Files.copy(Paths.get(fromFile.getCanonicalPath), Paths.get(toFile.getCanonicalPath))
      }
    }
  }

  lazy val rootVersion: Version = {
    if ( unresolvedArtifact.version.rawValue == "latest") {
      val versions = RepositoryOps.remoteVersions(unresolvedArtifact.asCoursierModule)
      versions.toList.sorted.head
    } else {
      Version.parse(unresolvedArtifact.version.rawValue).get
    }
  }

}
