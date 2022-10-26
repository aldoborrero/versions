package io.accur8.neodeploy


import a8.appinstaller.AppInstallerConfig.LibDirKind
import a8.shared.{CompanionGen, Exec}
import a8.shared.FileSystem.{Directory, File, Path}
import a8.shared.SharedImports._
import a8.shared.app.{Logging, LoggingF}
import coursier.core.{ModuleName, Organization}
import io.accur8.neodeploy.ApplicationInstallSync.{Installer}
import io.accur8.neodeploy.model.Install.FromRepo
import io.accur8.neodeploy.model.{ApplicationDescriptor, AppsRootDirectory, Install, Version}
import zio.{Task, ZIO}

object ApplicationInstallSync extends Logging with LoggingF {

  case class Installer(deployState: DeployState, appDir: Directory) {

    def applicationDescriptor: ApplicationDescriptor =
      deployState
        .newApplicationDescriptor
        .getOrError("no new applicationDescriptor this should not happen")

    def appRootBinDir = appDir.parentOpt.get.subdir("bin")

    def deleteAppDir: Task[Unit] =
      ZIO.attemptBlocking(
        appDir.delete()
      )

    def createAppDir: Task[Unit] =
      ZIO.attemptBlocking(
        appDir.makeDirectories()
      )

    def runInstall(installMethod: Install): Task[Unit] =
      installMethod match {
        case r: FromRepo =>
          runInstallFromRepo(r)

        case Install.Manual =>
          zunit

      }

    def runInstallFromRepo(repo: FromRepo): Task[Unit] = {
      ZIO.attemptBlocking {
        val result =
          Exec(
            "sudo",
            "-u",
            applicationDescriptor.user,
//            "/nix/store/b2n71idin4yhsncpvj1w0q7pr3ff5q4d-devshell-dir/bin/a8-versions",
            "a8-versions",
            "install",
            "--organization",
            repo.organization.value,
            "--artifact",
            repo.artifact.value,
            "--version",
            repo.version.value,
            "--install-dir",
            appDir.canonicalPath,
            "--lib-dir-kind",
            "copy",
            "--webapp-explode",
            repo.webappExplode.toString,
            "--backup",
            "false"
          )
            .inDirectory(appDir)
            .execCaptureOutput()
        result
      }.flatMap {
        case result if result.exitCode === 0 =>
          loggerF.debug(s"install of ${applicationDescriptor.name} completed successfully")
        case result =>
          loggerF.warn(s"install of ${applicationDescriptor.name} non-zero exit code ${result.exitCode}\nstdout=${result.stdout}\nstderr=\n${result.stderr}")
      }
    }

    def symlinkConfig: Task[Unit] =
      updateSymLink(deployState.gitAppDirectory, appDir.file("config"))

    def symlinkJavaExecutable: Task[Unit] =
      updateSymLink(
        target = appRootBinDir.file(z"java${deployState.javaVersion}"),
        link = appRootBinDir.file(z"${deployState.applicationName}"),
      )

    def updateSymLink(
      target: Path,
      link: File,
    ): Task[Unit] = {
      for {
        _ <- ZIO.attemptBlocking {
          if (link.exists()) {
            link.delete()
          }
        }
        _ <- PathAssist.symlink(target, link)
      } yield ()
    }

    def install: Task[Unit] =
      for {
        _ <- createAppDir
        _ <- runInstall(applicationDescriptor.install)
        _ <- symlinkConfig
        _ <- symlinkJavaExecutable
      } yield ()

  }

}

case class ApplicationInstallSync(appsRootDirectory: AppsRootDirectory) extends Sync[FromRepo] with LoggingF {

  // Install
  //    create app directory
  //    symlink git directory to the app/config
  //    create java symlink
  //    create lib directory

  // Update
  //     update lib directory

  // Uninstall
  //     remove app directory

  override def state(applicationDescriptor: model.ApplicationDescriptor): Task[Option[FromRepo]] =
    applicationDescriptor.install match {
      case fr: FromRepo =>
        zsucceed(Some(fr))
      case Install.Manual =>
        zsucceed(None)
    }

  def appDir(deployState: DeployState): Directory = {
    appsRootDirectory.resolvedDirectory.subdir(deployState.applicationName.value)
  }

  override def applyAction(action: Sync.Action[FromRepo]): Task[Unit] = {

    def installer = Installer(action.deployState, appDir(action.deployState))

    val actionEffect =
      action match {
        case Sync.Noop(deployState) =>
          zunit
        case Sync.Update(deployState, _, newState) =>
          installer.install
        case Sync.Delete(deployState, _) =>
          installer.deleteAppDir
        case Sync.Insert(deployState, state) =>
          installer.install
      }

    for {
      _ <- actionEffect
    } yield ()

  }


}
