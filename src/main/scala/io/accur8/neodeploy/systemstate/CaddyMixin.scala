package io.accur8.neodeploy.systemstate


import a8.shared.SharedImports._
import io.accur8.neodeploy.systemstate.SystemStateModel._

trait CaddyMixin extends SystemStateMixin { self: SystemState.Caddy =>
  def dryRun: Vector[String] = Vector.empty
  def isActionNeeded: M[Boolean] = zsucceed(false)
  def stateKey: Option[StateKey] = None
  override def runApplyNewState = zunit
  override def runUninstallObsolete = zunit
}
