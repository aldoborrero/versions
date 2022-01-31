package a8.appinstaller

import a8.appinstaller.MxInstallInventory.MxInstallInventory
import a8.shared.CompanionGen

object InstallInventory extends MxInstallInventory

@CompanionGen
case class InstallInventory(
  appInstallerConfig: AppInstallerConfig,
  classpath: Iterable[String],
)
