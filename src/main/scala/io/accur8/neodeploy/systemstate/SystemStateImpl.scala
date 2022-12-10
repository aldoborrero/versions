package io.accur8.neodeploy.systemstate

import a8.shared.FileSystem
import a8.shared.SharedImports._
import io.accur8.neodeploy.ApplicationInstallSync.Installer
import io.accur8.neodeploy.dsl.Step.M
import io.accur8.neodeploy.systemstate.SystemStateModel.{StateKey, UnixPerms}
import io.accur8.neodeploy.{Command, HealthchecksDotIo}
import zio.ZIO

object SystemStateImpl {

  def dryRun(state: SystemState, inner: SystemState => Vector[String]): Vector[String] = {
    state match {
      case SystemState.Empty =>
        Vector.empty
      case SystemState.HealthCheck(check) =>
        Vector(s"healthcheck ${check.name}")
      case SystemState.Caddy(f) =>
        inner(f)
      case SystemState.Systemd(_, _, files) =>
        files
          .map(inner)
          .flatten
      case SystemState.TextFile(f, _, perms) =>
        val permsStr =
          if (perms.value.isEmpty) ""
          else s" with perms ${perms.value}"
        Vector(s"file ${f}${permsStr}")
      case SystemState.Supervisor(f) =>
        inner(f)
      case SystemState.Directory(d, perms) =>
        val permsStr =
          if (perms.value.isEmpty) ""
          else s" with perms ${perms.value}"
        Vector(s"directory ${d}${permsStr}")
      case javaAppInstall: SystemState.JavaAppInstall =>
        Vector(s"app install into ${javaAppInstall.appInstallDir} -- ${javaAppInstall.fromRepo.compactJson}")
      case SystemState.Composite(desc, states) =>
        val statesStr =
          states
            .map(inner)
            .flatten
            .map("    " + _)
        Vector(desc) ++ statesStr
    }
  }


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
      case tf: SystemState.TextFile =>
        runImpl(tf)
      case d: SystemState.Directory =>
        runImpl(d)
      case javaAppInstall: SystemState.JavaAppInstall =>
        Installer(javaAppInstall)
          .installAction
    }
  }

  def runImpl(tf: SystemState.TextFile): M[Unit] = {
    val file = FileSystem.file(tf.filename)
    ZIO.attemptBlocking(
      file.write(tf.contents)
    ).flatMap(_ => applyPermissions(file, tf.perms))
  }

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
    // ??? TODO make more robust
    zsucceed(perms.value.nonEmpty)
  }

  def applyPermissions(path: FileSystem.Path, perms: UnixPerms): M[Unit] =
    permissionsActionNeeded(path, perms)
      .map {
        case true =>
          Command("chmod", perms.value, path.absolutePath)
            .execCaptureOutput
            .as(())
        case false =>
          ()
      }

  def singleStateKey(systemState: SystemState): Option[StateKey] =
    systemState match {
      case SystemState.Empty =>
        None
      case SystemState.TextFile(f, _, _) =>
        StateKey(f).some
      case SystemState.Caddy(c) =>
        StateKey("caddy-" + c.filename).some
      case sd: SystemState.Systemd =>
        StateKey("unit-" + sd.user + "-" + sd.unitName).some
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
