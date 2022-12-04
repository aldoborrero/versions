package io.accur8.neodeploy

import a8.shared.SharedImports._
import a8.shared.{CompanionGen, Synchronize}
import io.accur8.neodeploy.MxPgbackrestServerSync._
import io.accur8.neodeploy.PgbackrestServerSync.{QualifiedClient, State}
import io.accur8.neodeploy.Systemd.{TimerFile, UnitFile}
import io.accur8.neodeploy.dsl.Step
import io.accur8.neodeploy.model.{PgbackrestClientDescriptor, PgbackrestServerDescriptor, QualifiedUserName, RSnapshotClientDescriptor, RSnapshotServerDescriptor}
import io.accur8.neodeploy.resolvedmodel.ResolvedUser
import zio.Task

object PgbackrestServerSync {

  object State extends MxState
  @CompanionGen
  case class State(
    clients: Vector[QualifiedClient],
    server: PgbackrestServerDescriptor,
  )

  type QualifiedClient = (QualifiedUserName,PgbackrestClientDescriptor)

}

case class PgbackrestServerSync(healthchecksDotIo: HealthchecksDotIo) extends Sync[State,ResolvedUser] {

  // for each rsnapshot client
      // create rsnapshot config
      // create healthchecks.io check
      // create systemd unit and timer

  // on each servers rsnapshot user create authorized_keys2 file entry
      // add proper scripts for ssh validations and invocation
      // add proper sudo implementation so we can sudo

  override val name: Sync.SyncName = Sync.SyncName("pgbackrestServer")

  override def state(input: ResolvedUser): Task[Option[State]] =
    zsucceed(
      input
        .plugins
        .pgbackrestServerOpt
        .map { pbs =>
          State(
            pbs.clients.map(c => c.user.qualifiedUserName -> c.descriptor),
            pbs.descriptor,
          )
        }
    )

  override def resolveStepsFromModification(modification: Sync.Modification[State, ResolvedUser]): Vector[Sync.Step] = {

    def delete(client: QualifiedClient): Vector[Sync.Step] = {
      // ??? TODO implement me
      Vector.empty
    }

    def insert(client: QualifiedClient, resolvedUser: ResolvedUser): Vector[Sync.Step] = {
      val pgbackrestClient =
        resolvedUser
          .server
          .repository
          .fetchUser(client._1)
          .plugins
          .pgbackrestClientOpt
          .get
      Vector(
        setupClientStep(resolvedUser.plugins.pgbackrestServerOpt.get, pgbackrestClient)
          .asSyncStep(s"setup pgbackrest client for ${client._1}")
      )
    }

    def sync(before: Vector[QualifiedClient], after: Vector[QualifiedClient], resolvedUser: ResolvedUser): Vector[Sync.Step] =
      Synchronize[QualifiedClient, QualifiedUserName](source = after, target = before, _._1)
        .flatMap {
          case Synchronize.Action.Noop(_, _) =>
            Vector.empty
          case Synchronize.Action.Insert(v) =>
            insert(v, resolvedUser)
          case Synchronize.Action.Update(b, a) =>
            delete(b) ++ insert(a, resolvedUser)
          case Synchronize.Action.Delete(v) =>
            delete(v)
        }

    val result =
      modification match {
        case Sync.Delete(state) =>
          state
            .clients
            .flatMap(delete)
        case Sync.Update(b, a, ru) =>
          sync(b.clients, a.clients, ru)
        case Sync.Insert(a, ru) =>
          sync(Vector.empty, a.clients, ru)
      }

    result

  }


  def setupClientStep(resolvedServer: resolvedmodel.ResolvedPgbackrestServer, client: resolvedmodel.ResolvedPgbackrestClient): Step = {

    def setupHealtcheckRecordsStep =
      healthchecksDotIo.step.upsert(
        HealthchecksDotIo.CheckUpsertRequest(
          name = z"pgbackrest-${client.server.name}",
          tags = z"pgbackrest managed ${client.server.name}".some,
          timeout = 1.day.toSeconds.some,
          grace = 1.hours.toSeconds.some,
          unique = Iterable("name")
        )
      )

    val systemdSetupStep =
      Systemd.setupStep(
        z"pgbackrest-${client.server.name}",
        z"run daily pgbackrest from ${client.server.name} to this machine",
        resolvedServer.user,
        UnitFile(
          Type = "oneshot",
          workingDirectory = resolvedServer.user.home,
          execStart = z"/bootstrap/bin/run-pgbackrest ${client.server.name}",
        ),
        TimerFile(
          onCalendar = "daily",
          persistent = true.some,
        ).some
      )

    setupHealtcheckRecordsStep >> systemdSetupStep

  }


}
