package io.accur8.neodeploy.systemstate

import a8.shared.FileSystem
import a8.shared.SharedImports._
import io.accur8.neodeploy.HealthchecksDotIo
import io.accur8.neodeploy.model.ApplicationDescriptor
import io.accur8.neodeploy.systemstate.SystemStateModel._
import zio.ZIO

object Interpretter {




  val impl = SystemStateImpl


  def dryRun(state: SystemState, previousState: PreviousState, actionNeededCache: Map[SystemState,Boolean]): Vector[String] = {
    def inner(s0: SystemState): Vector[String] = {
      actionNeededCache.get(s0) match {
        case Some(true) =>
          impl.dryRun(s0, inner)
        case _ =>
          Vector.empty
      }
    }
    inner(state)
  }


  def isActionNeeded(state: SystemState, cache: Map[SystemState,Boolean]): M[(Boolean,Map[SystemState,Boolean])] = {

    import impl._

    def retE(effect: M[(Boolean,Map[SystemState,Boolean])]) =
      effect
        .map(t => t._1 -> (t._2 + (state -> t._1)))

    def ret(b: Boolean) =
      b -> Map(state -> b)

    def reduce(items: Vector[(Boolean,Map[SystemState,Boolean])]): (Boolean,Map[SystemState,Boolean]) = {
      val b = items.foldLeft(false)(_ || _._1)
      b -> items.foldLeft(Map(state -> b))(_ ++ _._2)
    }

    state match {
      case SystemState.Empty =>
        zsucceed(ret(false))
      case SystemState.TextFile(f, expectedContents, perms) =>
        val file = FileSystem.file(f)
        for {
          permissionActionNeeded0 <- permissionsActionNeeded(file, perms)
          actualContents <- readContents(file)
        } yield ret(permissionActionNeeded0 || (actualContents !== some(expectedContents)))
      case SystemState.SecretsTextFile(f, SecretContent(expectedContents), perms) =>
        val file = FileSystem.file(f)
        for {
          permissionActionNeeded0 <- permissionsActionNeeded(file, perms)
          actualContents <- readContents(file)
        } yield ret(permissionActionNeeded0 || (actualContents !== some(expectedContents)))
      case SystemState.Composite(_, states) =>
        states
          .map(s => isActionNeeded(s, cache))
          .sequencePar
          .map(reduce)
      case SystemState.Directory(d, perms) =>
        val dir = FileSystem.dir(d)
        for {
          permissionActionNeeded0 <- permissionsActionNeeded(dir, perms)
        } yield ret(permissionActionNeeded0)
      case SystemState.Caddy(f) =>
        retE(isActionNeeded(f, cache))
      case SystemState.Supervisor(f) =>
        retE(isActionNeeded(f, cache))
      case SystemState.Systemd(_, _, files) =>
        files
          .map(s => isActionNeeded(s, cache))
          .sequencePar
          .map(reduce)
      case SystemState.HealthCheck(check) =>
        for {
          hcdio <- zservice[HealthchecksDotIo]
          updateNeeded <- hcdio.isUpdateNeeded(check)
        } yield ret(updateNeeded)
      case javaAppInstall: SystemState.JavaAppInstall =>
        zsucceed(ret(
          FileSystem
            .dir(javaAppInstall.appInstallDir)
            .subdir("config")
            .file("application.json")
            .readAsStringOpt()
            .map(json.unsafeRead[ApplicationDescriptor])
            .map(_ !== javaAppInstall.descriptor)
            .getOrElse(true)
        ))
    }

  }

  def runUndoSingle(state: SystemState): M[Unit] =
    state match {
      case SystemState.Empty =>
        zunit
      case SystemState.Caddy(f) =>
        runUndoSingle(f)
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
      case SystemState.TextFile(f, _, _) =>
        ZIO.attemptBlocking(
          FileSystem.file(f).delete()
        )
      case SystemState.SecretsTextFile(f, _, _) =>
        ZIO.attemptBlocking(
          FileSystem.file(f).delete()
        )
      case SystemState.Systemd(_, _, _) =>
        // ??? TODO properly disable systemd unit
        zunit
      case SystemState.JavaAppInstall(d, _, _, _) =>
        // ??? TODO properly uninstall / move app
        zunit
    }

  def run(state: SystemState): M[Unit] =
    isActionNeeded(state, Map.empty)
      .flatMap { case (_, actionNeededCache) =>
        def inner(s0: SystemState): M[Unit] =
          if ( actionNeededCache(s0) ) {
            impl.run(s0, inner)
          } else {
            zunit
          }
        impl.run(state, inner)
      }


  def statesByKey(systemState: SystemState): Map[StateKey,SystemState] =
    states(systemState)
      .flatMap(s => impl.singleStateKey(s).map(_ -> s))
      .toMap


  def states(systemState: SystemState): Vector[SystemState] =
    systemState match {
      case hss: SystemState.HasSubStates =>
        Vector(systemState) ++ hss.subStates.flatMap(states)
      case leaf =>
        Vector(leaf)
    }


}
