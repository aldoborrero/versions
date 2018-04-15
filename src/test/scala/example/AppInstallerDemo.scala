package example

import a8.appinstaller.{AppInstaller, AppInstallerConfig, InstallBuilder}
import a8.versions.predef._

object AppInstallerDemo {



  def main(args: Array[String]) = {

    val config =
      AppInstallerConfig(
        groupId = "a8",
        artifactId = "a8-qubes-dist_2.12",
        version = "2.7.0-20180410_0910_master",
        appDir = Some("/Users/glen/_a/qubes-install"),
        symlinks = true,
      )

    val installer = new AppInstaller(config)
    installer.execute()

  }

}
