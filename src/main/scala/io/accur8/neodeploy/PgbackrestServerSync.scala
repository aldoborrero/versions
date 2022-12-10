package io.accur8.neodeploy

import a8.shared.SharedImports._
import a8.shared.{CompanionGen, Synchronize}
import io.accur8.neodeploy.Systemd.{TimerFile, UnitFile}
import io.accur8.neodeploy.model.{OnCalendarValue, PgbackrestClientDescriptor, PgbackrestServerDescriptor, QualifiedUserName, RSnapshotClientDescriptor, RSnapshotServerDescriptor}
import io.accur8.neodeploy.resolvedmodel.ResolvedUser
import io.accur8.neodeploy.systemstate.SystemState
import zio.Task

object PgbackrestServerSync extends Sync[ResolvedUser] {

  // for each rsnapshot client
      // create rsnapshot config
      // create healthchecks.io check
      // create systemd unit and timer

  // on each servers rsnapshot user create authorized_keys2 file entry
      // add proper scripts for ssh validations and invocation
      // add proper sudo implementation so we can sudo

  override val name: Sync.SyncName = Sync.SyncName("pgbackrestServer")


  override def rawSystemState(input: ResolvedUser): SystemState =
    input
      .plugins
      .pgbackrestServerOpt
      .map { pbs =>
        SystemState.Composite(
          "setup pgbackrest server",
          pbs.clients.map(c => clientState(pbs, c)),
        )
      }
      .getOrElse(SystemState.Empty)


  def clientState(resolvedServer: resolvedmodel.ResolvedPgbackrestServer, client: resolvedmodel.ResolvedPgbackrestClient): SystemState = {

    val healthcheckState =
      SystemState.HealthCheck(
        HealthchecksDotIo.CheckUpsertRequest(
          name = z"pgbackrest-${client.server.name}",
          tags = z"pgbackrest managed ${client.server.name} active".some,
          timeout = 1.day.toSeconds.some,
          grace = 1.hours.toSeconds.some,
          unique = Iterable("name"),
        )
      )

    val systemdState =
      Systemd.systemState(
        z"pgbackrest-${client.server.name}",
        z"run daily pgbackrest from ${client.server.name} to this machine",
        resolvedServer.user,
        UnitFile(
          Type = "oneshot",
          workingDirectory = resolvedServer.user.home,
          execStart = z"/bootstrap/bin/run-pgbackrest ${client.stanzaName} ${client.server.name}",
        ),
        TimerFile(
          onCalendar = client.descriptor.onCalendar.getOrElse(OnCalendarValue.daily),
          persistent = true.some,
        ).some
      )

    SystemState.Composite(
      s"setup pgbackrest for ${client.server.name}",
      Vector(
        healthcheckState,
        systemdState,
      )
    )

  }


}
