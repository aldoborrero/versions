package io.accur8.neodeploy.systemstate


import a8.shared.SharedImports._
import io.accur8.neodeploy.systemstate.SystemStateModel._

trait RunCommandStateMixin extends SystemStateMixin { self: SystemState.RunCommandState =>

  override def dryRun: Vector[String] =
    dryRun("install", installCommands)

  def dryRun(actionName: String, commands: Vector[Command]): Vector[String] =
    commands
      .map { cmd =>
        s"run ${actionName} command -- ${cmd.args.mkString(" ")}${cmd.workingDirectory.map(wd => s" in working directory ${wd}")}"
      }

  override def dryRunUninstall: Vector[String] =
    dryRun("uninstall", installCommands)

  override def isActionNeeded: M[Boolean] = zsucceed(true)

  override def runApplyNewState =
    installCommands
      .map(_.asRunnableCommand.execCaptureOutput)
      .sequence
      .as(())

  override def runUninstallObsolete =
    uninstallCommands
      .map(_.asRunnableCommand.execCaptureOutput)
      .sequence
      .as(())

}
