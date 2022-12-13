package io.accur8.neodeploy.systemstate
import io.accur8.neodeploy.systemstate.SystemStateModel.M

trait TriggeredStateMixin extends SystemStateMixin { self: SystemState.TriggeredState =>

  override def stateKey: Option[SystemStateModel.StateKey] = ???
  override def dryRun: Vector[String] = ???
  override def isActionNeeded: M[Boolean] = ???

  /**
   * applies the state for just this system state and no sub states
   */
  override def runApplyNewState: M[Unit] = ???

  /**
   * uninstalls the state for just this system state and no sub states
   */
  override def runUninstallObsolete: M[Unit] = ???

}
