package example


import a8.appinstaller.AppInstallerConfig.LibDirKind
import a8.appinstaller.{AppInstaller, AppInstallerConfig, InstallBuilder}
import a8.versions.predef._

object AppInstallerDemo {



  def main(args: Array[String]) = {

    val config =
      AppInstallerConfig(
        organization = "a8",
        artifact = "a8-qubes-dist_2.12",
//        version = "2.7.0-20180410_0910_master",
        version = "latest",
        branch = Some("master"),
        installDir = Some("/Users/glen/_a/qubes-install"),
        libDirKind = Some(LibDirKind.Repo),
      )

    val installer = new AppInstaller(config)
    installer.execute()

  }

}
