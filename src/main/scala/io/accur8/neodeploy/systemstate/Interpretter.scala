package io.accur8.neodeploy.systemstate

import a8.shared.FileSystem
import a8.shared.SharedImports._
import a8.shared.app.LoggerF
import io.accur8.neodeploy.HealthchecksDotIo
import io.accur8.neodeploy.model.ApplicationDescriptor
import io.accur8.neodeploy.systemstate.Interpretter.ActionNeededCache
import io.accur8.neodeploy.systemstate.SystemStateModel._
import zio.{Ref, ZIO}

object Interpretter {

  case class ActionNeededCache(cache: Map[SystemState,Boolean])

  def apply(newState: NewState, previousState: PreviousState): M[Interpretter] =
    SystemStateImpl.actionNeededCache(newState)
      .map(anc => Interpretter(newState, previousState, anc))

}


case class Interpretter(newState: NewState, previousState: PreviousState, actionNeededCache: ActionNeededCache) {

  lazy val dryRunLog: Option[String] = {
    val newIsEmpty = SystemStateImpl.isEmpty(newState.systemState)
    val previousIsEmpty = SystemStateImpl.isEmpty(previousState.systemState)
    if (newIsEmpty && previousIsEmpty) {
      None
    } else Some {
      val dryRunLogs = SystemStateImpl.dryRun(this)
      val context = z"${newState.resolvedName}-${newState.syncName}"
      if (dryRunLogs.isEmpty) {
        s"dry run for ${context} is up to date"
      } else {
        s"dry run for ${context}${"\n"}${dryRunLogs.mkString("\n").indent("    ")}"
      }
    }
  }

  def runApplyNewState: M[Unit] =
    SystemStateImpl.runApplyNewState(this)

  def runUninstallObsolete: M[Unit] =
    SystemStateImpl.runUninstallObsolete(statesToCleanup)

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
