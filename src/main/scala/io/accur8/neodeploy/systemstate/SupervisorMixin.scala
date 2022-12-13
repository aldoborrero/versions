package io.accur8.neodeploy.systemstate


import a8.shared.SharedImports._
import io.accur8.neodeploy.systemstate.SystemStateModel._

trait SupervisorMixin extends SystemStateMixin { self: SystemState.Supervisor =>
  override def dryRun: Vector[String] = Vector.empty
  override def isActionNeeded: M[Boolean] = zsucceed(false)
  override def stateKey: Option[StateKey] = None
  override def runApplyNewState = zunit
  override def runUninstallObsolete = zunit
}
