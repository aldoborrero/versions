package io.accur8.neodeploy


import a8.shared.FileSystem.Directory
import a8.shared.SharedImports._
import io.accur8.neodeploy.resolvedmodel.ResolvedUser
import zio.ZIO
import PredefAssist._
import a8.shared.FileSystem
import io.accur8.neodeploy.model.OnCalendarValue
import io.accur8.neodeploy.systemstate.SystemState
import io.accur8.neodeploy.systemstate.SystemStateModel._

/**
 * user level systemd service and timers
 *        https://gist.github.com/oprypin/0f0c3479ab53e00988b52919e5d7c144
 *        https://opensource.com/article/20/7/systemd-timers
 */
object Systemd {

  case class UnitFile(
    Type: String,
    workingDirectory: Directory,
    execStart: String,
  )

  case class TimerFile(
    onCalendar: OnCalendarValue,
    persistent: Option[Boolean],
  ) {
    def timerLines = (
      Some(z"OnCalendar=${onCalendar}")
        ++ persistent.map(p => z"Persistent=${p}")
    )
  }

//  def removeStep(
//    unitName: String,
//    user: ResolvedUser,
//  ): Step = {
//    val systemdUserDir = user.home.subdir(z".config/systemd/user")
//    (
//      Step.runCommand("systemctl", "stop", unitName)
//        >> Step.runCommand("systemctl", "disable", unitName)
//        >> Step.delete(systemdUserDir.file(s"${unitName}.service"))
//        >> Step.delete(systemdUserDir.file(s"${unitName}.timer"))
//    )
//  }

    //  systemctl stop [servicename]
    //    systemctl disable [servicename]
    //    rm /etc/systemd/system/[servicename]
    //      rm /etc/systemd/system/[servicename] # and symlinks that might be related
    //    rm /usr/lib/systemd/system/[servicename]
    //      rm /usr/lib/systemd/system/[servicename] # and symlinks that might be related
    //    systemctl daemon-reload
    //  systemctl reset-failed

  /**
   * from here https://wiki.archlinux.org/title/systemd/User#Automatic_start-up_of_systemd_user_instances
   */
  def systemState(
    unitName: String,
    description: String,
    user: ResolvedUser,
    unitFile: UnitFile,
    timerFileOpt: Option[TimerFile] = None,
  ): SystemState = {

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

    val unitFileState =
      SystemState.TextFile(
        filename = directory.file(z"${unitName}.service").absolutePath,
        contents = unitFileContents,
      )

    val timerFileStateOpt =
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

        SystemState.TextFile(
          filename = directory.file(z"${unitName}.timer").absolutePath,
          contents = timerFileContents,
        )
      }

//    def enableTimerEffect: M[Unit] = {
//      Overrides
//        .userSystemCtlCommand
//        .appendArgs("--user", "enable", "--now", z"${unitName}.timer")
//        .exec()
//        .as(())
//    }
//
//    val ensureUserLingerIsEnabled = {
//      val doesLingerNeedToBeEnabledEffect =
//        ZIO.attemptBlocking(
//          !FileSystem.file(z"/var/lib/systemd/linger/${user.login}").exists()
//        )
//
//      Step.rawEffect(
//        z"enable linger for ${user.login}",
//        doesLingerNeedToBeEnabledEffect,
//        Overrides.userLoginCtlCommand
//          .appendArgs("enable-linger")
//          .execDropOutput
//      )
//    }

    SystemState.Systemd(
      unitName = unitName,
      unitFiles = Vector(unitFileState) ++ timerFileStateOpt,
    )

  }

}
