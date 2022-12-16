package io.accur8.neodeploy


import a8.appinstaller.AppInstallerConfig.LibDirKind
import a8.shared.{CompanionGen, Exec}
import a8.shared.FileSystem.{Directory, File, Path, dir}
import a8.shared.SharedImports._
import a8.shared.app.{Logging, LoggingF}
import a8.versions.RepositoryOps.RepoConfigPrefix
import coursier.core.{ModuleName, Organization}
import io.accur8.neodeploy.ApplicationInstallSync.Installer
import io.accur8.neodeploy.model.Install.JavaApp
import io.accur8.neodeploy.model.{ApplicationDescriptor, AppsRootDirectory, Install, Version}
import io.accur8.neodeploy.resolvedmodel.{ResolvedApp, ResolvedServer}
import zio.{Task, ZIO}
import a8.versions.RepositoryOps
import io.accur8.neodeploy.systemstate.SystemState
import io.accur8.neodeploy.systemstate.SystemState.JavaAppInstall

import java.nio.file.Paths

object ApplicationInstallSync extends Logging with LoggingF {

  case class Installer(installState: SystemState.JavaAppInstall) {

    lazy val gitAppDirectory = dir(installState.gitAppDirectory)

    lazy val appDir = dir(installState.appInstallDir)

    def applicationDescriptor: ApplicationDescriptor =
      installState.descriptor

    def appRootBinDir = appDir.parentOpt.get.subdir("bin").resolve

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

        case r: JavaApp =>
          runInstallFromRepo(r)

        case _: Install.Manual =>
          zunit

      }

    def runInstallFromRepo(repo: JavaApp): Task[Unit] = (
      loggerF.debug(s"runInstallFromRepo(${repo})") *>
      ZIO.attemptBlocking {
        import a8.appinstaller._
        val repositoryOps = RepositoryOps(repo.repository.getOrElse(RepoConfigPrefix.default))
        val config = AppInstallerConfig(
          organization = repo.organization.value,
          artifact = repo.artifact.value,
          branch = None,
          version = repo.version.value,
          installDir = appDir.asNioPath.toFile().getAbsolutePath().some,
          libDirKind = LibDirKind.Copy.some,
          webappExplode = repo.webappExplode.some,
          backup = true,
        )
        AppInstaller(config, repositoryOps)
          .execute()
      }
    )


//    def runInstallFromRepoExternalProc(repo: FromRepo): Task[Unit] = {
//      ZIO.attemptBlocking {
//        val a8VersionsExec = server.descriptor.a8VersionsExec.getOrElse("a8-versions")
//        val repoConfig = applicationDescriptor.repository.getOrElse(RepoConfigPrefix.default)
//        val args =
//          Seq(
//            a8VersionsExec,
//            "install",
//            "--organization",
//            repo.organization.value,
//            "--artifact",
//            repo.artifact.value,
//            "--version",
//            repo.version.value,
//            "--install-dir",
//            appDir.canonicalPath,
//            "--lib-dir-kind",
//            "copy",
//            "--webapp-explode",
//            repo.webappExplode.toString,
//            "--backup",
//            "false",
//            "--repo",
//            repoConfig.value,
//          )
//        val result =
//          Exec(args:_*)
//            .inDirectory(appDir)
//            .execCaptureOutput()
//        result
//      }.flatMap {
//        case result if result.exitCode === 0 =>
//          loggerF.debug(s"install of ${applicationDescriptor.name} completed successfully")
//        case result =>
//          loggerF.warn(s"install of ${applicationDescriptor.name} non-zero exit code ${result.exitCode}\nstdout=${result.stdout}\nstderr=\n${result.stderr}")
//      }
//    }

//    def asSteps: Vector[Step] = {
//      ???
//      Vector(
//        Step(
//          phase = Phase.Apply,
//          description = z"install ${state.applicationDescriptor.name} ${applicationDescriptor.install.description} into ${appDir.toString}",
//          action = installAction,
//        ),
//      )
//    }

    def symlinkConfig: Task[Unit] =
      updateSymLink(dir(installState.gitAppDirectory), appDir.file("config"))

    def symlinkJavaExecutable: Task[Unit] =
      updateSymLink(
        target = appRootBinDir.file(z"java${installState.fromRepo.javaVersion}"),
        link = appRootBinDir.file(z"${applicationDescriptor.name}"),
      )

    def updateSymLink(
      target: Path,
      link: File,
    ): Task[Unit] = {
      for {
        _ <- PathAssist.symlink(target, link, deleteIfExists = false)
      } yield ()
    }

    def installAction: Task[Unit] =
      for {
        _ <- createAppDir
        _ <- runInstall(applicationDescriptor.install)
        _ <- symlinkConfig
        _ <- symlinkJavaExecutable
      } yield ()

  }

}

case class ApplicationInstallSync(appsRootDirectory: AppsRootDirectory) extends ApplicationSync with LoggingF {

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

//  override def state(resolvedApp: ResolvedApp): Task[Option[State]] =
//    resolvedApp.descriptor.install match {
//      case fr: FromRepo =>
//        zsucceed(Some(
//          State(
//            appInstallDir = appsRootDirectory.unresolvedDirectory.subdir(resolvedApp.descriptor.name.value).toString(),
//            fromRepo = fr,
//            gitAppDirectory = resolvedApp.gitDirectory.toString(),
//            applicationDescriptor = resolvedApp.descriptor,
//          )
//        ))
//      case Install.Manual =>
//        zsucceed(None)
//    }


//  override def resolveStepsFromModification(modification: Sync.Modification[State, ResolvedApp]): Vector[Sync.Step] = {
//    modification match {
//      case Sync.Update(_, newState, newInput) =>
//        val javaAppInstall =
//          JavaAppInstall(
//            appInstallDir = ???,
//            fromRepo = ???,
//            descriptor = ???,
//            gitAppDirectory = ???
//          )
//        Installer(javaAppInstall).asSteps
//      case Sync.Delete(currentState) =>
//        Vector(Step(
//          Phase.Apply,
//          z"uninstall ${currentState.applicationDescriptor.name} by deleting it's ${currentState.appInstallDir} installed directory",
//          ZIO.attemptBlocking(
//            dir(currentState.appInstallDir).delete()
//          )
//        ))
//      case Sync.Insert(newState, newInput) =>
//        val javaAppInstall =
//          JavaAppInstall(
//            gitAppDirectory = newInput.gitDirectory.absolutePath,
//            descriptor = newInput.descriptor,
//            fromRepo = newState.fromRepo,
//            appInstallDir = newState.appInstallDir,
//          )
//        Installer(javaAppInstall)
//          .asSteps
//    }
//  }

  override def rawSystemState(resolvedApp: ResolvedApp): SystemState =
    resolvedApp.descriptor.install match {
      case fr: JavaApp =>
        SystemState.JavaAppInstall(
          gitAppDirectory = resolvedApp.gitDirectory.absolutePath,
          descriptor = resolvedApp.descriptor,
          appInstallDir = appsRootDirectory.unresolvedDirectory.subdir(resolvedApp.descriptor.name.value).toString(),
          fromRepo = fr,
        )
      case _: Install.Manual =>
        SystemState.Empty
    }

}
