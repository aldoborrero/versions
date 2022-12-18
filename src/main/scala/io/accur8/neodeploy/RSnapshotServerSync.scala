package io.accur8.neodeploy


import a8.shared.SharedImports._
import io.accur8.neodeploy.resolvedmodel.{ResolvedRSnapshotServer, ResolvedServer, ResolvedUser}
import zio.{Task, ZIO, ZLayer}
import PredefAssist._
import a8.shared.{CompanionGen, Synchronize}
import a8.shared.json.ast.JsDoc
import io.accur8.neodeploy.Systemd.{TimerFile, UnitFile}
import io.accur8.neodeploy.model.{OnCalendarValue, QualifiedUserName, RSnapshotClientDescriptor, RSnapshotServerDescriptor}
import io.accur8.neodeploy.systemstate.SystemState
import io.accur8.neodeploy.systemstate.SystemStateModel.M

object RSnapshotServerSync extends Sync[ResolvedUser] {

  // for each rsnapshot client
      // create rsnapshot config
      // create healthchecks.io check
      // create systemd unit and timer

  // on each servers rsnapshot user create authorized_keys2 file entry
      // add proper scripts for ssh validations and invocation
      // add proper sudo implementation so we can sudo

  override val name: Sync.SyncName = Sync.SyncName("rsnapshotServer")


  override def systemState(user: ResolvedUser): M[SystemState] =
    zsucceed(rawSystemState(user))

  def rawSystemState(user: ResolvedUser): SystemState =
    user
      .plugins
      .resolvedRSnapshotServerOpt
      .map(systemState)
      .getOrElse(SystemState.Empty)


  def systemState(resolvedRSnapshotServer: ResolvedRSnapshotServer): SystemState =
    SystemState.Composite(
      "setup rsnapshot server",
      resolvedRSnapshotServer
        .clients
        .map { client =>
          setupClientSystemState(resolvedRSnapshotServer, client)
        }
    )


  def setupClientSystemState(resolvedServer: resolvedmodel.ResolvedRSnapshotServer, client: resolvedmodel.ResolvedRSnapshotClient): SystemState = {

    lazy val rsnapshotConfigFile =
      resolvedServer
        .descriptor
        .configDir
        .file(z"rsnapshot-${client.server.name}.conf")

    lazy val configFileState =
      SystemState.TextFile(
        rsnapshotConfigFile,
        RSnapshotConfig.serverConfigForClient(resolvedServer, client),
      )

    val healthCheckState =
      SystemState.HealthCheck(
        HealthchecksDotIo.CheckUpsertRequest(
          name = z"rsnapshot-${client.server.name}",
          tags = z"rsnapshot managed ${client.server.name} active".some,
          timeout = 1.day.toSeconds.some,
          grace = 1.hours.toSeconds.some,
          unique = Iterable("name")
        )
      )

    val systemdState =
      Systemd.systemState(
        z"rsnapshot-${client.server.name}",
        z"run snapshot from ${client.server.name} to this machine",
        resolvedServer.user,
        UnitFile(
          Type = "oneshot",
          workingDirectory = rsnapshotConfigFile.parent.absolutePath,
          execStart = z"/bootstrap/bin/run-rsnapshot ${rsnapshotConfigFile} ${client.server.name}",
        ),
        TimerFile(
          onCalendar = OnCalendarValue.hourly,
          persistent = true.some,
        ).some
      )

    SystemState.Composite(
      z"setup rsnapshot for client ${client.server.name}",
      Vector(
        configFileState,
        healthCheckState,
        systemdState,
      )
    )

  }

}
