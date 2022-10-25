package a8.appinstaller


import a8.appinstaller.MxAppInstallerConfig.MxAppInstallerConfig
import a8.shared.json.{JsonCodec, JsonTypedCodec}
import a8.shared.{CompanionGen, FileSystem}
import a8.versions.ast
import a8.versions.ast.Dependency

object AppInstallerConfig extends MxAppInstallerConfig {

  sealed trait LibDirKind extends enumeratum.EnumEntry
  object LibDirKind extends enumeratum.Enum[LibDirKind] {
    val values = findValues
    case object Copy extends LibDirKind
    case object Symlink extends LibDirKind
    case object Repo extends LibDirKind

    implicit lazy val codec: JsonCodec[LibDirKind] =
      JsonTypedCodec
        .string
        .dimap[LibDirKind](
          s => values.find(_.entryName == s).get,
          _.entryName,
        )
        .asJsonCodec

  }
}

@CompanionGen
case class AppInstallerConfig(
  organization: String,
  artifact: String,
  version: String,
  branch: Option[String],
  installDir: Option[String] = None,
  libDirKind: Option[AppInstallerConfig.LibDirKind] = None,
  webappExplode: Option[Boolean] = None,
  backup: Boolean = true,
) {

  lazy val resolveWebappExplode = webappExplode.getOrElse(true)

  lazy val resolvedLibDirKind = libDirKind.getOrElse(AppInstallerConfig.LibDirKind.Repo)

  lazy val artifactCoords = s"${organization}:${artifact}:${version}"

  lazy val resolvedInstallDir: FileSystem.Directory = FileSystem.dir(installDir.getOrElse(sys.error("installDir is required")))

  lazy val unresolvedArtifact: Dependency =
    Dependency(organization, "%", artifact, ast.StringIdentifier(version))

}
