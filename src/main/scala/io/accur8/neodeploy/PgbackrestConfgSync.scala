package io.accur8.neodeploy

import a8.shared.ZFileSystem
import a8.shared.SharedImports._
import io.accur8.neodeploy.Systemd.{TimerFile, UnitFile}
import io.accur8.neodeploy.resolvedmodel.{ResolvedPgbackrestClient, ResolvedPgbackrestServer, ResolvedRSnapshotServer, ResolvedUser}
import io.accur8.neodeploy.systemstate.SystemState
import io.accur8.neodeploy.systemstate.SystemStateModel.M
import zio.Task


object PgbackrestConfgSync extends Sync[ResolvedUser] {

  override val name: Sync.SyncName = Sync.SyncName("pgbackrestConfig")

  def configFile(input: ResolvedUser): ZFileSystem.File =
    ZFileSystem.file(
      input
        .plugins
        .pgbackrestClientOpt
        .map(_.descriptor.configFile)
        .orElse(input.plugins.pgbackrestServerOpt.map(_.descriptor.configFile))
        .flatten
        .getOrElse("/etc/pgbackrest/pgbackrest.conf")
    )

  def fileContents(input: ResolvedUser): Option[String] =
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

  def clientConfig(resolvedClient: ResolvedPgbackrestClient): String = {
z"""
[global]
repo1-host=${resolvedClient.resolvedServer.user.server.descriptor.vpnDomainName}
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

[${resolvedClient.stanzaName}]
pg1-path=${resolvedClient.descriptor.pgdata}
""".ltrim
    }

  def serverConfig(resolvedServer: ResolvedPgbackrestServer): String = {
    lazy val clientConfigs =
      resolvedServer
        .clients
        .map { pgc =>
          z"""
             |[${pgc.stanzaName}]
             |pg1-host=${pgc.server.descriptor.vpnDomainName}
             |pg1-path=${pgc.descriptor.pgdata}
          """.stripMargin
        }
        .mkString("\n\n")

    s"${resolvedServer.descriptor.configHeader}\n\n${clientConfigs}"
  }


  override def systemState(input: ResolvedUser): M[SystemState] =
    fileContents(input) match {
      case Some(contents) =>
        zsucceed(
          SystemState.TextFile(
            configFile(input),
            contents,
          )
        )
      case None =>
        zsucceed(SystemState.Empty)
    }

}
