package io.accur8.neodeploy


import a8.appinstaller.AppInstallerConfig.LibDirKind
import a8.shared.{CompanionGen, Exec}
import a8.shared.FileSystem.{Directory, File, Path}
import a8.shared.SharedImports._
import a8.shared.app.{Logging, LoggingF}
import coursier.core.{ModuleName, Organization}
import io.accur8.neodeploy.ApplicationInstallSync.{Installer, State}
import io.accur8.neodeploy.MxApplicationInstallSync._
import io.accur8.neodeploy.model.{ApplicationDescriptor, AppsRootDirectory, Version}
import zio.{Task, ZIO}

object ApplicationInstallSync extends Logging {

  object State extends MxState
  @CompanionGen
  case class State(
    version: Version,
  )

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

    def runInstall: Task[Unit] = {
      ZIO.attemptBlocking {
        a8.versions.apps.Main.runInstall(
          module = coursier.Module(Organization(applicationDescriptor.organization.value), ModuleName(applicationDescriptor.artifact.value)),
          branch = None,
          version = applicationDescriptor.version.value.some,
          installDir = appDir.canonicalPath,
          libDirKind = LibDirKind.Copy.entryName.some,
          webappExplode = applicationDescriptor.webappExplode.some,
          backup = false,
        )
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
        _ <- runInstall
        _ <- symlinkConfig
        _ <- symlinkJavaExecutable
      } yield ()

  }

}

case class ApplicationInstallSync(appsRootDirectory: AppsRootDirectory) extends Sync[State] with LoggingF {

  // Install
  //    create app directory
  //    symlink git directory to the app/config
  //    create java symlink
  //    create lib directory

  // Update
  //     update lib directory

  // Uninstall
  //     remove app directory

  override def state(applicationDescriptor: model.ApplicationDescriptor): Task[Option[State]] =
    zsucceed(
      State(applicationDescriptor.version).some
    )

  def appDir(deployState: DeployState): Directory = {
    appsRootDirectory.resolvedDirectory.subdir(deployState.applicationName.value)
  }

  override def applyAction(action: Sync.Action[State]): Task[Unit] = {

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
