package io.accur8.neodeploy


import a8.appinstaller.AppInstallerConfig.LibDirKind
import a8.shared.{CompanionGen, Exec}
import a8.shared.FileSystem.{Directory, File, Path, dir}
import a8.shared.SharedImports._
import a8.shared.app.{Logging, LoggingF}
import a8.versions.RepositoryOps.RepoConfigPrefix
import coursier.core.{ModuleName, Organization}
import io.accur8.neodeploy.ApplicationInstallSync.{Installer, State}
import io.accur8.neodeploy.MxApplicationInstallSync._
import io.accur8.neodeploy.model.Install.FromRepo
import io.accur8.neodeploy.model.{ApplicationDescriptor, AppsRootDirectory, Install, Version}
import io.accur8.neodeploy.resolvedmodel.ResolvedApp
import zio.{Task, ZIO}

import java.nio.file.Paths

object ApplicationInstallSync extends Logging with LoggingF {

  case class Installer(state: State) {

    lazy val appDir = dir(state.appInstallDir)

    def applicationDescriptor: ApplicationDescriptor =
      state
        .applicationDescriptor

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
        val repoConfig = applicationDescriptor.repository.getOrElse(RepoConfigPrefix.default)
        val args =
          Seq(
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
            "false",
            "--repo",
            repoConfig.value,
          )
        logger.debug("we would run this command == " + args.mkString(" "))
//        val result =
//          Exec(args:_*)
//            .inDirectory(appDir)
//            .execCaptureOutput()
        Exec.Result(0, "", "")
      }.flatMap {
        case result if result.exitCode === 0 =>
          loggerF.debug(s"install of ${applicationDescriptor.name} completed successfully")
        case result =>
          loggerF.warn(s"install of ${applicationDescriptor.name} non-zero exit code ${result.exitCode}\nstdout=${result.stdout}\nstderr=\n${result.stderr}")
      }
    }

    def symlinkConfig: Task[Unit] =
      updateSymLink(dir(state.gitAppDirectory), appDir.file("config"))

    def symlinkJavaExecutable: Task[Unit] =
      updateSymLink(
        target = appRootBinDir.file(z"java${state.applicationDescriptor.javaVersion}"),
        link = appRootBinDir.file(z"${state.applicationDescriptor.name}"),
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

  object State extends MxState
  @CompanionGen
  case class State(
    appInstallDir: String,
    fromRepo: FromRepo,
    gitAppDirectory: String,
    applicationDescriptor: ApplicationDescriptor,
  )

}

case class ApplicationInstallSync(appsRootDirectory: AppsRootDirectory) extends ApplicationSync[State] with LoggingF {

  // Install
  //    create app directory
  //    symlink git directory to the app/config
  //    create java symlink
  //    create lib directory

  // Update
  //     update lib directory

  // Uninstall
  //     remove app directory

  override val name: Sync.SyncName = Sync.SyncName("installer")

  override def state(resolvedApp: ResolvedApp): Task[Option[State]] =
    resolvedApp.application.install match {
      case fr: FromRepo =>
        zsucceed(Some(
          State(
            appInstallDir = appsRootDirectory.unresolvedDirectory.subdir(resolvedApp.application.name.value).toString(),
            fromRepo = fr,
            gitAppDirectory = resolvedApp.gitDirectory.toString(),
            applicationDescriptor = resolvedApp.application,
          )
        ))
      case Install.Manual =>
        zsucceed(None)
    }


  override def applyAction(input: Option[ResolvedApp], action: Sync.Action[State]): Task[Unit] = {

    val actionEffect =
      action match {
        case Sync.Noop(_) =>
          zunit
        case Sync.Update(_, newState) =>
          Installer(newState).install
        case Sync.Delete(currentState) =>
          ZIO.attemptBlocking(
            dir(currentState.appInstallDir).delete()
          )
        case Sync.Insert(newState) =>
          Installer(newState).install
      }

    for {
      _ <- actionEffect
    } yield ()

  }


}
