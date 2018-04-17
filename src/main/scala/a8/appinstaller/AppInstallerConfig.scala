package a8.appinstaller


import a8.appinstaller.AppInstallerConfig.LibDirKind
import a8.common.CompanionGen
import a8.versions.ast
import a8.versions.ast.Dependency
import enumeratum.{PlayInsensitiveJsonEnum, PlayJsonEnum}
import m3.fs._

object AppInstallerConfig extends MxAppInstallerConfig {
  sealed trait LibDirKind extends enumeratum.EnumEntry
  object LibDirKind extends enumeratum.Enum[LibDirKind] with PlayInsensitiveJsonEnum[LibDirKind] {
    val values = findValues
    case object Copy extends LibDirKind
    case object Symlink extends LibDirKind
    case object Repo extends LibDirKind
  }
}

@CompanionGen
case class AppInstallerConfig(
  groupId: String,
  artifactId: String,
  version: String,
  branch: Option[String],
  installDir: Option[String] = None,
  libDirKind: Option[LibDirKind] = None,
  webappExplode: Option[Boolean] = None
) {

  lazy val resolveWebappExplode = webappExplode.getOrElse(true)

  lazy val resolvedLibDirKind = libDirKind.getOrElse(LibDirKind.Repo)

  lazy val artifactCoords = s"${groupId}:${artifactId}:${version}"

  lazy val resolvedInstallDir: LocalFileSystem.Directory = dir(installDir.getOrElse("./target/app-installer"))

  lazy val unresolvedArtifact: Dependency =
    Dependency(groupId, "%", artifactId, ast.StringIdentifier(version))

}
