package io.accur8.neodeploy


import a8.shared.FileSystem.Directory
import io.accur8.neodeploy.dsl.Step
import a8.shared.SharedImports._
import io.accur8.neodeploy.dsl.Step.impl.ParallelSteps
import io.accur8.neodeploy.resolvedmodel.ResolvedUser
import zio.ZIO
import PredefAssist._
import a8.shared.FileSystem

object Systemd {

  import Step._

  case class UnitFile(
    Type: String,
    workingDirectory: Directory,
    execStart: String,
  )

  case class TimerFile(
    onCalendar: String,
    persistent: Option[Boolean],
  ) {
    def timerLines = (
      Some(z"OnCalendar=${onCalendar}")
        ++ persistent.map(p => z"Persistent=${p}")
    )
  }

  def removeStep(
    unitName: String,
    user: ResolvedUser,
  ): Step = {
    val systemdUserDir = user.home.subdir(z".config/systemd/user")
    (
      Step.runCommand("systemctl", "stop", unitName)
        >> Step.runCommand("systemctl", "disable", unitName)
        >> Step.delete(systemdUserDir.file(s"${unitName}.service"))
        >> Step.delete(systemdUserDir.file(s"${unitName}.timer"))
    )
  }

    //  systemctl stop [servicename]
    //    systemctl disable [servicename]
    //    rm /etc/systemd/system/[servicename]
    //      rm /etc/systemd/system/[servicename] # and symlinks that might be related
    //    rm /usr/lib/systemd/system/[servicename]
    //      rm /usr/lib/systemd/system/[servicename] # and symlinks that might be related
    //    systemctl daemon-reload
    //  systemctl reset-failed

  /**
    *  from here https://wiki.archlinux.org/title/systemd/User#Automatic_start-up_of_systemd_user_instances
    */
  def setupStep(
    unitName: String,
    description: String,
    user: ResolvedUser,
    unitFile: UnitFile,
    timerFileOpt: Option[TimerFile] = None,
  ): Step = {

    val directory = user.home.subdir(z".config/systemd/user")

    val unitFileContents =
      z"""
         |[Unit]
         |Description=$description
         |
         |[Service]
         |WorkingDirectory=${unitFile.workingDirectory}
         |ExecStart=${unitFile.execStart}
         |
         |[Install]
         |WantedBy=multi-user.target
         |""".stripMargin

    val unitFileStep =
      Step.fileStep(
        file = directory.file(z"${unitName}.service"),
        content = unitFileContents,
      )

    val timerFileStepOpt =
      timerFileOpt.map {timerFile =>

        val timerFileContents =
          z"""
             |[Unit]
             |Description=${description}
             |
             |[Timer]
             |${timerFile.timerLines.mkString("\n")}
             |
             |[Install]
             |WantedBy=timers.target
          """.stripMargin

        Step.fileStep(
          file = directory.file(z"${unitName}.timer"),
          content = timerFileContents,
        )
      }

    def enableUnitEffect: M[Unit] = {
      Overrides
        .userSystemCtlCommand
        .appendArgs("--user", "enable", z"${unitName}.service")
        .exec()
        .as(())
    }

    val ensureUserLingerIsEnabled = {
      val doesLingerNeedToBeEnabledEffect =
        ZIO.attemptBlocking(
          !FileSystem.file(z"/var/lib/systemd/linger/${user.login}").exists()
        )

      Step.rawEffect(
        z"enable linger for ${user.login}",
        doesLingerNeedToBeEnabledEffect,
        Overrides.userLoginCtlCommand
          .appendArgs("enable-linger")
          .execDropOutput
      )
    }

    ParallelSteps((unitFileStep.some ++ timerFileStepOpt).toVector)
      .onActionRunAfter(s"enable systemd unit ${unitName}", enableUnitEffect)
      .andThen(ensureUserLingerIsEnabled)
      .span("systemd setup for " + unitName)

  }

}
