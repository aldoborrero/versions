package io.accur8.neodeploy

import a8.shared.FileSystem
import a8.shared.SharedImports._
import io.accur8.neodeploy.Systemd.{TimerFile, UnitFile}
import io.accur8.neodeploy.dsl.Step
import io.accur8.neodeploy.dsl.Step.impl.ParallelSteps
import io.accur8.neodeploy.resolvedmodel.{ResolvedPgbackrestServer, ResolvedRSnapshotServer, ResolvedUser}
import zio.Task


case class PGBackrestServerSync(healthchecksApiAuthToken: HealthchecksDotIo.ApiAuthToken) extends ConfigFileSync[ResolvedUser] {

  import Step._

  override def configFile(input: ResolvedUser): FileSystem.File =
    FileSystem.file("/etc/pgbackrest/pgbackrest.conf")

  override def configFileContents(input: ResolvedUser): Task[Option[String]] = {
    zsucceed(
      input
        .plugins
        .pluginInstances
        .collect {
          case rps: ResolvedPgbackrestServer =>
            rps
        }
        .headOption
        .map { resolvedServer =>

          lazy val clientConfigs =
            resolvedServer
              .clients
              .map { pgc =>
                z"""
                   |[${pgc.server.name}]
                   |pg1-host=${pgc.server.descriptor.serverName}
                   |pg1-path=${pgc.descriptor.pgdata}
                """.stripMargin
              }
              .mkString("\n\n")

          s"${resolvedServer.descriptor.configHeader}\n\n${clientConfigs}"

        }
    )
  }

  override val name: Sync.SyncName = Sync.SyncName("pgbackrestserver")

}
