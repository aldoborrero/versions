package io.accur8.neodeploy


import a8.shared.SharedImports._
import io.accur8.neodeploy.resolvedmodel.{ResolvedRSnapshotServer, ResolvedServer, ResolvedUser}
import zio.{Task, ZIO, ZLayer}
import PredefAssist._
import a8.shared.{CompanionGen, Synchronize}
import a8.shared.json.ast.JsDoc
import io.accur8.neodeploy.MxRSnapshotServerSync.MxState
import io.accur8.neodeploy.RSnapshotServerSync.{QualifiedClient, State}
import io.accur8.neodeploy.Sync.Phase
import io.accur8.neodeploy.Systemd.{TimerFile, UnitFile}
import io.accur8.neodeploy.dsl.Step
import io.accur8.neodeploy.dsl.Step.impl.ParallelSteps
import io.accur8.neodeploy.model.{QualifiedUserName, RSnapshotClientDescriptor, RSnapshotServerDescriptor}

object RSnapshotServerSync {

  object State extends MxState
  @CompanionGen
  case class State(
    clients: Vector[QualifiedClient],
    server: RSnapshotServerDescriptor,
  )

  type QualifiedClient = (QualifiedUserName,RSnapshotClientDescriptor)

}

case class RSnapshotServerSync(healthchecksDotIo: HealthchecksDotIo) extends Sync[State,ResolvedUser] {

  import Step._

  // for each rsnapshot client
      // create rsnapshot config
      // create healthchecks.io check
      // create systemd unit and timer

  // on each servers rsnapshot user create authorized_keys2 file entry
      // add proper scripts for ssh validations and invocation
      // add proper sudo implementation so we can sudo

  override val name: Sync.SyncName = Sync.SyncName("rsnapshotServer")

  override def state(input: ResolvedUser): Task[Option[State]] =
    zsucceed(
      input
        .plugins
        .resolvedRSnapshotServerOpt
        .map { rrss =>
          State(
            rrss.clients.map(c => c.user.qualifiedUserName -> c.descriptor),
            rrss.descriptor,
          )
        }
    )

  override def resolveStepsFromModification(modification: Sync.Modification[State, ResolvedUser]): Vector[Sync.Step] = {

    def delete(client: QualifiedClient): Vector[Sync.Step] =
      ???

    def insert(client: QualifiedClient, resolvedUser: ResolvedUser): Vector[Sync.Step] = {
      val resolvedRSnapshotClient =
        resolvedUser
          .server
          .repository
          .fetchUser(client._1)
          .plugins
          .resolvedRSnapshotClientOpt
          .get
      Vector(
        setupClientStep(resolvedUser.plugins.resolvedRSnapshotServerOpt.get, resolvedRSnapshotClient)
          .asSyncStep(s"setup rsnapshot client for ${client}")
      )
    }

    def sync(before: Vector[QualifiedClient], after: Vector[QualifiedClient], resolvedUser: ResolvedUser): Vector[Sync.Step] =
      Synchronize[QualifiedClient, QualifiedUserName](before, after, _._1)
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

  }



//  def action(resolvedServer: resolvedmodel.ResolvedRSnapshotServer): Task[Unit] = {
//
//    val step =
//      ParallelSteps(
//        resolvedServer
//          .clients
//          .map(setupClientStep)
//      )
//
//    val effect =
//      for {
//        dryRuns <- Step.dryRun(step, checkIfActionIsRequired = true, recurse = true, indent = "        ")
//        _ <- loggerF.debug(s"will run the following steps:\n${dryRuns.mkString("\n")}\n")
//        _ <- Step.performAction(step)
//      } yield ()
//
//    effect
//      .provideLayer(StepLogger.simpleLayer)
//
//  }

  def setupClientStep(resolvedServer: resolvedmodel.ResolvedRSnapshotServer, client: resolvedmodel.ResolvedRSnapshotClient): Step = {

    lazy val rsnapshotConfigFile =
      resolvedServer
        .descriptor
        .rsnapshotConfigDir
        .resolvedDirectory
        .file(z"rsnapshot-${client.server.name}.conf")

    def rsnapshotConfigFileStep  =
      Step.fileStep(
        rsnapshotConfigFile,
        RSnapshotConfig.serverConfigForClient(resolvedServer, client),
      )

    def setupHealtcheckRecordsStep =
      healthchecksDotIo.step.upsert(
        HealthchecksDotIo.CheckUpsertRequest(
          name = z"rsnapshot-${client.server.name}",
          tags = z"rsnapshot managed ${client.server.name}".some,
          timeout = 1.day.toSeconds.some,
          grace = 1.hours.toSeconds.some,
          unique = Iterable("name")
        )
      )

    val systemdSetupStep =
      Systemd.setupStep(
        z"rsnapshot-${client.server.name}",
        z"run snapshot from ${client.server.name} to this machine",
        resolvedServer.user,
        UnitFile(
          Type = "oneshot",
          workingDirectory = rsnapshotConfigFile.parent,
          execStart = z"/bootstrap/bin/run-rsnapshot ${rsnapshotConfigFile} ${client.server.name}",
        ),
        TimerFile(
          onCalendar = "hourly",
          persistent = true.some,
        ).some
      )


    rsnapshotConfigFileStep >> setupHealtcheckRecordsStep >> systemdSetupStep

  }


}
