package a8.appinstaller

import a8.appinstaller.AppInstallerConfig.LibDirKind
import a8.appinstaller.MxInstallInventory.MxInstallInventory
import a8.common.CompanionGen

object InstallInventory extends MxInstallInventory

@CompanionGen
case class InstallInventory(
  appInstallerConfig: AppInstallerConfig,
  classpath: Iterable[String],
)
