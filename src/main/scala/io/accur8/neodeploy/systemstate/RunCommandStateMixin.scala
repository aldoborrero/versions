package io.accur8.neodeploy.systemstate


import a8.shared.SharedImports._
import io.accur8.neodeploy.systemstate.SystemStateModel._

trait RunCommandStateMixin extends SystemStateMixin { self: SystemState.RunCommandState =>

  def dryRun: Vector[String] =
    installCommand
      .toVector
      .map(ic =>
        s"run install command${ic.workingDirectory.map(wd => s" in working directory ${wd}")} -- ${ic.args.mkString(" ")}"
      )

  override def dryRunUninstall: Vector[String] = super.dryRunUninstall
    uninstallCommand
      .toVector
      .map(c =>
        s"run uninstall command${c.workingDirectory.map(wd => s" in working directory ${wd}")} -- ${c.args.mkString(" ")}"
      )

  def isActionNeeded: M[Boolean] = zsucceed(installCommand.nonEmpty)

  override def runApplyNewState =
    installCommand
      .map(
        _.asRunnableCommand
          .execCaptureOutput
          .as(())
      )
      .getOrElse(zunit)

  override def runUninstallObsolete =
    uninstallCommand
      .map(
        _.asRunnableCommand
          .execCaptureOutput
          .as(())
      )
      .getOrElse(zunit)

}
