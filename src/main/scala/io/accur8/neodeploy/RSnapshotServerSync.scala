package io.accur8.neodeploy


import a8.shared.SharedImports._
import io.accur8.neodeploy.resolvedmodel.{ResolvedRSnapshotServer, ResolvedServer, ResolvedUser}
import zio.{Task, ZIO, ZLayer}
import PredefAssist._
import io.accur8.neodeploy.Systemd.{TimerFile, UnitFile}
import io.accur8.neodeploy.dsl.Step
import io.accur8.neodeploy.dsl.Step.impl.ParallelSteps



class RSnapshotServerSync(resolvedServer: ResolvedRSnapshotServer, healthchecksApiAuthToken: HealthchecksDotIo.ApiAuthToken) {

  import Step._

  // for each rsnapshot client
      // create rsnapshot config
      // create healthchecks.io check
      // create systemd unit and timer

  // on each servers rsnapshot user create authorized_keys2 file entry
      // add proper scripts for ssh validations and invocation
      // add proper sudo implementation so we can sudo

  def run: Task[Unit] = {
    val effect =
    for {
      dryRuns <- Step.dryRun(step, checkIfActionIsRequired = true, recurse = true, indent = "        ")
      _ <- loggerF.debug(s"will run the following steps:\n${dryRuns.mkString("\n")}\n")
      _ <- Step.performAction(step)
    } yield ()
    effect
      .provideLayer(StepLogger.simpleLayer)
  }

  def step: Step =
    ParallelSteps(
      resolvedServer
        .clients
        .map(setupClientStep)
    )

  def setupClientStep(client: resolvedmodel.ResolvedRSnapshotClient): Step = {

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
      HealthchecksDotIo.step(
        HealthchecksDotIo.CheckUpsertRequest(
          api_key = healthchecksApiAuthToken,
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
