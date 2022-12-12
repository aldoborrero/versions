package io.accur8.neodeploy.systemstate

import a8.shared.FileSystem
import a8.shared.SharedImports._
import a8.shared.app.LoggerF
import io.accur8.neodeploy.HealthchecksDotIo
import io.accur8.neodeploy.model.ApplicationDescriptor
import io.accur8.neodeploy.systemstate.SystemStateModel._
import zio.ZIO

object Interpretter {


  case class RunArgs(newState: NewState, previousState: PreviousState, actionNeededCache: ActionNeededCache) {

    def dryRunLog(loggerF: LoggerF): zio.UIO[Unit] = {
      val newIsEmpty = Interpretter.isEmpty(newState.systemState)
      val previousIsEmpty = Interpretter.isEmpty(previousState.systemState)
      if ( newIsEmpty && previousIsEmpty ) {
        zunit
      } else {
        val dryRunLogs = dryRun(this)
        val context = z"${newState.resolvedName}-${newState.syncName}"
        if (dryRunLogs.isEmpty) {
          loggerF.info( s"dry run for ${context} is up to date")
        } else {
          loggerF.info( s"dry run for ${context}${"\n"}${dryRunLogs.mkString("\n").indent("    ")}")
        }
      }
    }


    lazy val statesToCleanup: Vector[SystemState] = {

      val newStatesByKey = newState.statesByKey
      val previousStatesByKey = previousState.statesByKey

      // cleanup anything in previous not in current
      previousStatesByKey
        .filterNot(e => newStatesByKey.contains(e._1))
        .map { case (key, ss) =>
          ss
        }
        .toVector

    }


  }


  def dryRun(runArgs: RunArgs): Vector[String] = {
    def inner(s0: SystemState): Vector[String] = {
      runArgs.actionNeededCache.cache.get(s0) match {
        case Some(true) =>
          SystemStateImpl.dryRun(s0, inner, "")
        case _ =>
          Vector.empty
      }
    }
    inner(runArgs.newState.systemState) ++ SystemStateImpl.dryRunCleanup(runArgs)
  }

  case class ActionNeededCache(cache: Map[SystemState,Boolean])

  def actionNeededCache(newState: NewState): M[ActionNeededCache] = {
    def impl(s0: SystemState): M[(Boolean,Map[SystemState,Boolean])] = {
      import SystemStateImpl._

      def retE(effect: M[(Boolean, Map[SystemState, Boolean])]) =
        effect
          .map(t => t._1 -> (t._2 + (s0 -> t._1)))

      def ret(b: Boolean) =
        b -> Map(s0 -> b)

      def reduce(items: Vector[(Boolean, Map[SystemState, Boolean])]): (Boolean, Map[SystemState, Boolean]) = {
        val b = items.foldLeft(false)(_ || _._1)
        b -> items.foldLeft(Map(s0 -> b))(_ ++ _._2)
      }

      s0 match {
        case SystemState.Empty =>
          zsucceed(ret(false))
        case SystemState.TextFile(f, expectedContents, perms, mkdirs) =>
          val file = FileSystem.file(f)
          for {
            permissionActionNeeded0 <- permissionsActionNeeded(file, perms)
            actualContents <- readContents(file)
          } yield ret(permissionActionNeeded0 || (actualContents !== some(expectedContents)))
        case SystemState.SecretsTextFile(f, SecretContent(expectedContents), perms, mkdirs) =>
          val file = FileSystem.file(f)
          for {
            permissionActionNeeded0 <- permissionsActionNeeded(file, perms)
            actualContents <- readContents(file)
          } yield ret(permissionActionNeeded0 || (actualContents !== some(expectedContents)))
        case SystemState.Composite(_, states) =>
          states
            .map(s => impl(s))
            .sequencePar
            .map(reduce)
        case SystemState.Directory(d, perms) =>
          val dir = FileSystem.dir(d)
          for {
            permissionActionNeeded0 <- permissionsActionNeeded(dir, perms)
          } yield ret(permissionActionNeeded0)
        case SystemState.Caddy(f) =>
          retE(impl(f))
        case SystemState.Supervisor(f) =>
          retE(impl(f))
        case SystemState.Systemd(_, _, files) =>
          files
            .map(s => impl(s))
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
    impl(newState.systemState)
      .map(t => ActionNeededCache(t._2))
  }

  def isEmpty(state: SystemState): Boolean =
    state match {
      case SystemState.Empty =>
        true
      case SystemState.Composite(_, values) =>
        values.forall(isEmpty)
      case _ =>
        false
    }

  def run(runArgs: RunArgs): M[Unit] = {
    def inner(s0: SystemState): M[Unit] =
      if ( runArgs.actionNeededCache.cache(s0) ) {
        SystemStateImpl.run(s0, inner)
      } else {
        zunit
      }

    for {
      _ <- SystemStateImpl.run(runArgs.newState.systemState, inner)
      _ <- SystemStateImpl.runCleanup(runArgs)
    } yield ()
  }


  def statesByKey(systemState: SystemState): Map[StateKey,SystemState] =
    states(systemState)
      .flatMap(s => SystemStateImpl.singleStateKey(s).map(_ -> s))
      .toMap


  def states(systemState: SystemState): Vector[SystemState] =
    systemState match {
      case hss: SystemState.HasSubStates =>
        Vector(systemState) ++ hss.subStates.flatMap(states)
      case leaf =>
        Vector(leaf)
    }


}
