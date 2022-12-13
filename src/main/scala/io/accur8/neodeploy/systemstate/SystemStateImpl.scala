package io.accur8.neodeploy.systemstate


import a8.shared.{FileSystem, ZFileSystem}
import a8.shared.SharedImports._
import io.accur8.neodeploy.ApplicationInstallSync.Installer
import io.accur8.neodeploy.systemstate.Interpretter.ActionNeededCache
import io.accur8.neodeploy.systemstate.SystemStateModel._
import io.accur8.neodeploy.{Command, HealthchecksDotIo}
import zio.ZIO

import java.nio.file.Files
import java.nio.file.attribute.PosixFileAttributeView

object SystemStateImpl {

  def dryRunCleanup(statesToCleanup: Vector[SystemState]): Vector[String] =
    statesToCleanup
      .flatMap { ss =>
        dryRun(ss, _ => Vector.empty, "cleanup ")
      }

  def dryRun(state: SystemState, inner: SystemState => Vector[String], prefix: String): Vector[String] = {
    val stateDryRun = state.dryRun
    val subStatesDryRun =
      state match {
        case hasSubStates: SystemState.HasSubStates =>
          hasSubStates
            .subStates
            .flatMap(inner)
        case _ =>
          Vector.empty
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

  def runApplyNewState(state: SystemState, inner: SystemState => M[Unit]): M[Unit] =
    for {
      _ <- state.runApplyNewState
      _ <-
        state match {
          case hasSubStates: SystemState.HasSubStates =>
            hasSubStates
              .subStates
              .map(inner)
              .sequence
          case _ =>
            zunit
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

  def dryRun(interpretter: Interpretter): Vector[String] = {
    def inner(s0: SystemState): Vector[String] = {
      interpretter.actionNeededCache.cache.get(s0) match {
        case Some(true) =>
          SystemStateImpl.dryRun(s0, inner, "")
        case _ =>
          Vector.empty
      }
    }
    inner(interpretter.newState.systemState) ++ SystemStateImpl.dryRunCleanup(interpretter.statesToCleanup)
  }

  def actionNeededCache(newState: NewState): M[ActionNeededCache] = {
    def impl(systemState: SystemState): M[Map[SystemState, Boolean]] = {
      systemState
        .isActionNeeded
        .flatMap { isActionNeeded =>
          val value = Map(systemState -> isActionNeeded)
          systemState match {
            case hss: SystemState.HasSubStates =>
              hss.subStates
                .map(impl)
                .sequence
                .map(_.reduce(_ ++ _) ++ value)
            case _ =>
              zsucceed(value)
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
      case _ =>
        false
    }

  def runApplyNewState(interpretter: Interpretter): M[Unit] = {
    def inner(s0: SystemState): M[Unit] =
      if (interpretter.actionNeededCache.cache(s0)) {
        SystemStateImpl.runApplyNewState(s0, inner)
      } else {
        zunit
      }
    runApplyNewState(interpretter.newState.systemState, inner)
  }


  def statesByKey(systemState: SystemState): Map[StateKey, SystemState] =
    states(systemState)
      .flatMap(s => s.stateKey.map(_ -> s))
      .toMap


  def states(systemState: SystemState): Vector[SystemState] =
    systemState match {
      case hss: SystemState.HasSubStates =>
        Vector(systemState) ++ hss.subStates.flatMap(states)
      case leaf =>
        Vector(leaf)
    }

}
