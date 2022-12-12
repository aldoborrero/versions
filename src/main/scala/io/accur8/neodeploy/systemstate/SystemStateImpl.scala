package io.accur8.neodeploy.systemstate

import a8.shared.FileSystem
import a8.shared.SharedImports._
import io.accur8.neodeploy.ApplicationInstallSync.Installer
import io.accur8.neodeploy.systemstate.Interpretter.RunArgs
import io.accur8.neodeploy.systemstate.SystemStateModel._
import io.accur8.neodeploy.{Command, HealthchecksDotIo}
import zio.ZIO

import java.nio.file.Files
import java.nio.file.attribute.PosixFileAttributeView

object SystemStateImpl {

  def dryRunCleanup(runArgs: RunArgs): Vector[String] = {

    // cleanup anything in previous not in current
    runArgs
      .statesToCleanup
      .flatMap { ss =>
        dryRun(ss, _ => Vector.empty, "cleanup ")
      }

  }


  def dryRun(state: SystemState, inner: SystemState => Vector[String], prefix: String): Vector[String] = {
    def ret(msg: String) = Vector(prefix + msg)
    state match {
      case SystemState.Empty =>
        Vector.empty
      case SystemState.HealthCheck(check) =>
        ret(s"healthcheck ${check.name}")
      case SystemState.Caddy(f) =>
        inner(f)
      case SystemState.Systemd(_, _, files) =>
        files
          .map(inner)
          .flatten
      case SystemState.SecretsTextFile(f, _, perms, _) =>
        val permsStr =
          if (perms.value.isEmpty) ""
          else s" with perms ${perms.value}"
        ret(s"secret file ${f}${permsStr}")
      case SystemState.TextFile(f, _, perms, _) =>
        val permsStr =
          if (perms.value.isEmpty) ""
          else s" with perms ${perms.value}"
        ret(s"file ${f}${permsStr}")
      case SystemState.Supervisor(f) =>
        inner(f)
      case SystemState.Directory(d, perms) =>
        val permsStr =
          if (perms.value.isEmpty) ""
          else s" with perms ${perms.value}"
        ret(s"directory ${d}${permsStr}")
      case javaAppInstall: SystemState.JavaAppInstall =>
        ret(s"app install into ${javaAppInstall.appInstallDir} -- ${javaAppInstall.fromRepo.compactJson}")
      case SystemState.Composite(desc, states) =>
        val statesStr =
          states
            .map(inner)
            .flatten
            .map("    " + _)
        ret(desc) ++ statesStr
    }
  }

  def runCleanupSingle(state: SystemState): M[Unit] =
    state match {
      case SystemState.Empty =>
        zunit
      case SystemState.Caddy(f) =>
        zunit
      case SystemState.HealthCheck(check) =>
        for {
          service <- zservice[HealthchecksDotIo]
          _ <- service.disable(check)
        } yield ()
      case SystemState.Composite(_, _) =>
        zunit
      case SystemState.Directory(d, _) =>
        ZIO.attemptBlocking(
          FileSystem.dir(d).delete()
        )
      case SystemState.Supervisor(_) =>
        // ??? TODO properly disable systemd unit
        zunit
      case SystemState.TextFile(f, _, _, mkdirs) =>
        ZIO.attemptBlocking {
          val file = FileSystem.file(f)
          if ( mkdirs && !file.parent.exists() )
            file.parent.makeDirectories()
          file.delete()
        }
      case SystemState.SecretsTextFile(f, _, _, mkdirs) =>
        ZIO.attemptBlocking {
          val file = FileSystem.file(f)
          if (mkdirs && !file.parent.exists())
            file.parent.makeDirectories()
          file.delete()
        }
      case SystemState.Systemd(_, _, _) =>
        // ??? TODO properly disable systemd unit
        zunit
      case SystemState.JavaAppInstall(d, _, _, _) =>
        // ??? TODO properly uninstall / move app
        zunit
    }

  /**
   * cleanup anything in previousState not in currentState
   */
  def runCleanup(runArgs: RunArgs): M[Unit] =
    runArgs
      .statesToCleanup
      .map(runCleanupSingle)
      .sequence
      .as(())


  def run(state: SystemState, inner: SystemState => M[Unit]): M[Unit] = {
    state match {
      case SystemState.Empty =>
        zunit
      case SystemState.HealthCheck(check) =>
        for {
          service <- zservice[HealthchecksDotIo]
          _ <- service.upsert(check)
        } yield ()
      case SystemState.Caddy(f) =>
        inner(f)
      case SystemState.Supervisor(f) =>
        inner(f)
      case SystemState.Systemd(_, _, files) =>
        files
          .map(inner)
          .sequence
          .as(())
      case SystemState.Composite(_, states) =>
        states
          .map(inner)
          .sequence
          .as(())
      case stf: SystemState.SecretsTextFile =>
        runImpl(stf.asTextFile)
      case tf: SystemState.TextFile =>
        runImpl(tf)
      case d: SystemState.Directory =>
        runImpl(d)
      case javaAppInstall: SystemState.JavaAppInstall =>
        Installer(javaAppInstall)
          .installAction
    }
  }

  def runImpl(tf: SystemState.TextFile): M[Unit] =
    ZIO
      .attemptBlocking {
        val file = FileSystem.file(tf.filename)
        if ( tf.makeParentDirectories && !file.parent.exists() )
          file.parent.makeDirectories()
        file.write(tf.contents)
        file
      }
      .flatMap(applyPermissions(_, tf.perms))

  def runImpl(d: SystemState.Directory): M[Unit] = {
    val fsDir = FileSystem.dir(d.path)
    ZIO.attemptBlocking(
      if (!fsDir.exists())
        fsDir.makeDirectories()
    ).flatMap(_ => applyPermissions(fsDir, d.perms))
  }

  def readContents(file: FileSystem.File): M[Option[String]] =
    ZIO.attemptBlocking(
      file.readAsStringOpt()
    )

  def permissionsActionNeeded(path: FileSystem.Path, perms: UnixPerms): M[Boolean] = {
    if (perms.expectedPerms.isEmpty) {
      zsucceed(false)
    } else {
      ZIO.attemptBlocking {
        if ( path.exists() ) {
          val actual =
            Files.getFileAttributeView(path.asNioPath, classOf[PosixFileAttributeView])
              .readAttributes()
              .permissions()
              .asScala
          val expected = perms.expectedPermsAsNioSet
          val result = expected != actual
          result
        } else {
          true
        }
      }
    }
  }

  def applyPermissions(path: FileSystem.Path, perms: UnixPerms): M[Unit] =
    permissionsActionNeeded(path, perms)
      .flatMap {
        case true =>
          Command("chmod", perms.value, path.absolutePath)
            .execCaptureOutput
            .as(())
        case false =>
          zunit
      }

  def singleStateKey(systemState: SystemState): Option[StateKey] =
    systemState match {
      case SystemState.Empty =>
        None
      case SystemState.SecretsTextFile(f, _, _, _) =>
        None
      case SystemState.TextFile(f, _, _, _) =>
        StateKey(f).some
      case SystemState.Caddy(c) =>
        StateKey("caddy-" + c.filename).some
      case sd: SystemState.Systemd =>
        StateKey("userunit-" + sd.unitName).some
      case jai: SystemState.JavaAppInstall =>
        StateKey(jai.appInstallDir).some
      case SystemState.Supervisor(f) =>
        StateKey("supervisor-" + f.filename).some
      case c: SystemState.Composite =>
        None
      case d: SystemState.Directory =>
        StateKey(d.path).some
      case SystemState.HealthCheck(check) =>
        StateKey(s"healthcheck-${check.name}").some
    }

}
