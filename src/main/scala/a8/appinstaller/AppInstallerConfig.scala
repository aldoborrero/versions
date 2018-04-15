package a8.appinstaller


import a8.versions.ast
import a8.versions.ast.Dependency
import m3.fs._


case class AppInstallerConfig(
  groupId: String,
  artifactId: String,
  version: String,
  appDir: Option[String] = None,
  symlinks: Boolean = true,
  stopServerCommand: Option[String] = None,
  startServerCommand: Option[String] = None,
  webappExplode: Boolean = true
) {

  lazy val artifactCoords = s"${groupId}:${artifactId}:${version}"

  lazy val resolvedAppDir: LocalFileSystem.Directory = dir(appDir.getOrElse("./target/app-installer"))

  lazy val unresolvedArtifact: Dependency =
    Dependency(groupId, "%", artifactId, ast.StringIdentifier(version))

}
