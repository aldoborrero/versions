package io.accur8.neodeploy


import a8.shared.SharedImports._
import a8.shared.{CompanionGen, FileSystem}
import a8.shared.app.BootstrappedIOApp
import a8.shared.app.BootstrappedIOApp.BootstrapEnv
import io.accur8.neodeploy.MxMain._
import io.accur8.neodeploy.model.{AppsRootDirectory, CaddyDirectory, DomainName, GitRootDirectory, GitServerDirectory, ResolvedRepository, ServerName, SupervisorDirectory}
import zio.ZIO

object Main extends BootstrappedIOApp {

  object Config extends MxConfig
  @CompanionGen
  case class Config(
    gitRootDirectory: GitRootDirectory,
    serverName: ServerName,
  )

  lazy val configFile =
    FileSystem
      .userHome
      .subdir(".a8")
      .file("server_app_sync.conf")

  lazy val config =
    configFile
      .readAsStringOpt()
      .map(json.unsafeRead[Config])
      .getOrError(s"config file ${configFile} not found")

  lazy val resolvedRepository =
    ResolvedRepository.loadFromDisk(config.gitRootDirectory)

  lazy val resolvedServer =
    resolvedRepository
      .servers
      .find(_.name == config.serverName)
      .getOrError(s"server ${config.serverName} not found")

  override def runT: ZIO[BootstrapEnv, Throwable, Unit] = {
    val syncServer = SyncServer(resolvedServer)
    syncServer.run
  }

}
