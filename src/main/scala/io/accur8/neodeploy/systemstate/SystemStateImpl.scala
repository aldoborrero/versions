package io.accur8.neodeploy.systemstate


import a8.shared.{FileSystem, StringValue, ZFileSystem}
import a8.shared.SharedImports._
import a8.sync.qubes.QubesApiClient
import io.accur8.neodeploy.ApplicationInstallSync.Installer
import io.accur8.neodeploy.systemstate.Interpreter.ActionNeededCache
import io.accur8.neodeploy.systemstate.SystemState.TriggeredState
import io.accur8.neodeploy.systemstate.SystemStateModel._
import io.accur8.neodeploy.{Command, HealthchecksDotIo}
import zio.ZIO

import java.nio.file.Files
import java.nio.file.attribute.PosixFileAttributeView
import scala.collection.immutable.Vector

object SystemStateImpl {

  def dryRunUninstall(statesToUninstall: Vector[SystemState]): Vector[String] = {
    val dryRunUninstallLogs =
      statesToUninstall
        .flatMap { ss =>
          rawDryRun(_.dryRunUninstall, ss, _ => Vector.empty)
        }
    if ( dryRunUninstallLogs.nonEmpty )
      Vector("uninstaller actions") ++ dryRunUninstallLogs.map("   " + _)
    else
      Vector.empty

  }

  def rawDryRun(getLogsFn: SystemState=>Vector[String], state: SystemState, inner: SystemState => Vector[String]): Vector[String] = {
    val stateDryRun = getLogsFn(state)
    val subStatesDryRun =
      state match {
        case hasSubStates: SystemState.HasSubStates =>
          hasSubStates
            .subStates
            .flatMap(inner)
        case _: SystemState.NoSubStates =>
          Vector.empty
        case triggeredState: TriggeredState =>
          inner(triggeredState.preTriggerState) ++ inner(triggeredState.triggerState) ++ inner(triggeredState.postTriggerState)
      }

    (stateDryRun.isEmpty, subStatesDryRun.isEmpty) match {
      case (_, true) =>
        stateDryRun
      case (true, _) =>
        subStatesDryRun
      case _ =>
        // indent the substates if we have top level dry run values
        stateDryRun ++ subStatesDryRun.map("    " + _)
    }
  }

  def runUninstallObsolete(obsoleteStates: Vector[SystemState]): M[Unit] = {
    // we reverse because we want file cleanup to happen before directory cleanup
    obsoleteStates
      .reverse
      .map(_.runUninstallObsolete)
      .sequence
      .as(())
  }

  def runApplyNewState(state: SystemState, interpreter: Interpreter, inner: SystemState => M[Unit]): M[Unit] =
    for {
      _ <- state.runApplyNewState
      _ <-
        state match {
          case hasSubStates: SystemState.HasSubStates =>
            hasSubStates
              .subStates
              .map(inner)
              .sequence
          case _: SystemState.NoSubStates =>
            zunit
          case triggeredState: SystemState.TriggeredState =>
            interpreter.actionNeededCache.cache(triggeredState.triggerState) match {
              case true =>
                for {
                  _ <- inner(triggeredState.preTriggerState)
                  _ <- inner(triggeredState.triggerState)
                  _ <- inner(triggeredState.postTriggerState)
                } yield ()
              case false =>
                zunit
            }
        }
    } yield ()

  def permissionsActionNeeded(path: ZFileSystem.Path, perms: UnixPerms): M[Boolean] = {
    if (perms.expectedPerms.isEmpty) {
      zsucceed(false)
    } else {
      path
        .exists
        .flatMap {
          case true =>
            ZIO.attemptBlocking(
              Files.getFileAttributeView(path.asNioPath, classOf[PosixFileAttributeView])
                .readAttributes()
                .permissions()
                .asScala
            ).map { actual =>
              val expected = perms.expectedPermsAsNioSet
              val result = expected != actual
              result
            }
          case false =>
            zsucceed(true)
        }
    }
  }


  def applyPermissions(path: ZFileSystem.Path, perms: UnixPerms): M[Unit] =
    permissionsActionNeeded(path, perms)
      .flatMap {
        case true =>
          Command("chmod", perms.value, path.absolutePath)
            .execCaptureOutput
            .as(())
        case false =>
          zunit
      }

  def dryRun(interpretter: Interpreter): Vector[String] = {
    def inner(s0: SystemState): Vector[String] = {
      interpretter.actionNeededCache.cache.get(s0) match {
        case Some(false) =>
          Vector.empty
        case _ =>
          SystemStateImpl.rawDryRun(_.dryRunInstall, s0, inner)
      }
    }
    inner(interpretter.newState.systemState) ++ SystemStateImpl.dryRunUninstall(interpretter.statesToUninstall)
  }

  def actionNeededCache(newState: NewState): M[ActionNeededCache] = {
    def impl(systemState: SystemState): M[Map[SystemState, Boolean]] = {
      systemState
        .isActionNeeded
        .flatMap { isActionNeeded =>
          def value(b: Boolean) = Map(systemState -> b)
          systemState match {
            case hss: SystemState.HasSubStates =>
              hss.subStates
                .map(impl)
                .sequence
                .map(_.reduceOption(_ ++ _).getOrElse(Map.empty))
                .map { cache =>
                  val b = cache.values.exists(identity)
                  cache ++ value(b || isActionNeeded)
                }
            case _: SystemState.NoSubStates =>
              zsucceed(value(isActionNeeded))
            case triggeredState: TriggeredState =>
              impl(triggeredState.triggerState)
                .map { actionNeededCache =>
                  val value = actionNeededCache(triggeredState.triggerState)
                  (actionNeededCache + (triggeredState -> value)) -> value
                }
                .flatMap {
                  case (anc, true) =>
                    Vector(triggeredState.preTriggerState, triggeredState.postTriggerState)
                      .map(impl)
                      .sequence
                      .map { preAndPostAnc =>
                        preAndPostAnc.reduce(_ ++ _) ++ anc
                      }
                  case (anc, false) =>
                    zsucceed(
                      Vector(triggeredState.preTriggerState, triggeredState.postTriggerState)
                        .map(_ -> false)
                        .toMap ++ anc
                    )
                }
          }
        }
    }

    impl(newState.systemState)
      .map(ActionNeededCache.apply)
  }

  def isEmpty(state: SystemState): Boolean =
    state match {
      case SystemState.Empty =>
        true
      case hss: SystemState.HasSubStates =>
        hss.subStates.forall(isEmpty)
      case _: SystemState.NoSubStates =>
        false
      case _: SystemState.TriggeredState =>
        false
    }

  def runApplyNewState(interpretter: Interpreter): M[Unit] = {
    def inner(s0: SystemState): M[Unit] =
      if (interpretter.actionNeededCache.cache(s0)) {
        SystemStateImpl.runApplyNewState(s0, interpretter, inner)
      } else {
        zunit
      }
    inner(interpretter.newState.systemState)
  }


  def statesByKey(systemState: SystemState): Map[StateKey, SystemState] = {
    val states0 =
      states(systemState)
        .flatMap(s => s.stateKey.map(_ -> s))
        .toMap
    states0
  }


  def states(systemState: SystemState): Vector[SystemState] = {
    val v = Vector(systemState)
    val subStates =
      systemState match {
        case hss: SystemState.HasSubStates =>
           hss.subStates.flatMap(states)
        case leaf: SystemState.NoSubStates =>
          Vector.empty
        case triggeredState: SystemState.TriggeredState =>
          Vector(triggeredState.preTriggerState, triggeredState.triggerState, triggeredState.postTriggerState)
            .flatMap(states)
      }
    v ++ subStates
  }

}
