package a8.appinstaller


import java.nio.file.{Files, Path, Paths}
import java.util

import a8.appinstaller.AppInstallerConfig.LibDirKind
import a8.versions.Build.BuildType
import a8.versions.RepositoryOps.DependencyTree
import a8.versions.{RepositoryOps, Version, ast}
import a8.versions.ast.Dependency
import m3.fs._

import collection.JavaConverters._
import predef._
import a8.common.logging.Logging

case class InstallBuilder(
  unresolvedConfig: AppInstallerConfig
) extends Logging {

  lazy val unresolvedArtifact = unresolvedConfig.unresolvedArtifact

  lazy val appDir = resolvedConfig.resolvedAppDir

  lazy val resolvedConfig =
    unresolvedConfig.copy(version = rootVersion.toString)

  lazy val libDir: Directory = appDir \\ "lib"

  def build(): Unit = {
    buildLibDir()
    val jarFileName = resolvedConfig.artifactId + "-" + resolvedConfig.version + ".jar"
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
    libDir.makeDirectories
    libDir.entries.foreach {
      case f: File => f.delete()
      case d: Directory => d.deleteTree()
    }

    for (fromFile <- dependencyResult.localArtifacts) {
      val toFile: File = libDir \ fromFile.getName
      resolvedConfig.resolvedLibDirKind match {
        case LibDirKind.Symlink =>
          logger.debug("symlinking artifact " + fromFile)
          val link: Path = Paths.get(toFile.getCanonicalPath)
          val target: Path = Paths.get(fromFile.getCanonicalPath)
          Files.createSymbolicLink(link, target)
        case LibDirKind.Copy =>
          logger.debug("copying artifact " + fromFile)
          Files.copy(Paths.get(fromFile.getCanonicalPath), Paths.get(toFile.getCanonicalPath))
        case LibDirKind.Repo =>
          // noop
      }
    }
  }

  lazy val inventory =
    InstallInventory(
      resolvedConfig,
      classpath = classpath
    )

  lazy val classpath =
    dependencyResult.localArtifacts.flatMap { file =>
      resolvedConfig.resolvedLibDirKind match {
        case LibDirKind.Symlink | LibDirKind.Copy =>
          None
        case LibDirKind.Repo =>
          Some(file.getCanonicalPath)
      }
    }

  lazy val rootVersion: Version = {
    if ( unresolvedArtifact.version.rawValue == "latest") {
      val versions =
        RepositoryOps
          .remoteVersions(unresolvedArtifact.asCoursierModule)
          .filter(v => resolvedConfig.branch.isEmpty || resolvedConfig.branch == v.buildInfo.map(_.branch))
      versions.toList.sorted.last
    } else {
      Version.parse(unresolvedArtifact.version.rawValue).get
    }
  }

}
