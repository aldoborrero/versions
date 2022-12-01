package io.accur8.neodeploy

import a8.shared.FileSystem
import a8.shared.SharedImports._
import io.accur8.neodeploy.Systemd.{TimerFile, UnitFile}
import io.accur8.neodeploy.dsl.Step
import io.accur8.neodeploy.dsl.Step.impl.ParallelSteps
import io.accur8.neodeploy.resolvedmodel.{ResolvedPgbackrestClient, ResolvedPgbackrestServer, ResolvedRSnapshotServer, ResolvedUser}
import zio.Task


case class PGBackrestSync(healthchecksApiAuthToken: HealthchecksDotIo.ApiAuthToken) extends ConfigFileSync[ResolvedUser] {

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
            serverConfig(rps)
          case rpc: ResolvedPgbackrestClient =>
            clientConfig(rpc)
        }
        .headOption
    )
  }

  def clientConfig(resolvedClient: ResolvedPgbackrestClient): String = {
z"""
[global]
repo1-host=${resolvedClient.resolvedServer.server.name}
repo1-host-user=${resolvedClient.resolvedServer.user.login}
log-level-console=detail
log-level-file=debug

process-max=4
start-fast=y
#archive-async=y

[global:archive-get]
process-max=4

[global:archive-push]
process-max=4
compress-level=3

[${resolvedClient.server.name}]
pg1-path=${resolvedClient.descriptor.pgdata}
""".ltrim
    }

  def serverConfig(resolvedServer: ResolvedPgbackrestServer): String = {
    lazy val clientConfigs =
      resolvedServer
        .clients
        .map { pgc =>
          z"""
             |[${pgc.server.name}]
             |pg1-host=${pgc.server.descriptor.vpnDomainName}
             |pg1-path=${pgc.descriptor.pgdata}
          """.stripMargin
        }
        .mkString("\n\n")

    s"${resolvedServer.descriptor.configHeader}\n\n${clientConfigs}"
  }

  override val name: Sync.SyncName = Sync.SyncName("pgbackrestserver")

}
