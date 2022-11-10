package io.accur8.neodeploy


import a8.shared.SharedImports._
import a8.shared.{CompanionGen, FileSystem}
import a8.shared.app.BootstrappedIOApp
import a8.shared.app.BootstrappedIOApp.BootstrapEnv
import a8.versions.Exec
import io.accur8.neodeploy.MxMain._
import io.accur8.neodeploy.model.{AppsRootDirectory, CaddyDirectory, DomainName, GitRootDirectory, GitServerDirectory, ServerName, SupervisorDirectory}
import io.accur8.neodeploy.resolvedmodel.ResolvedRepository
import zio.ZIO

import java.net.InetAddress

object Main extends BootstrappedIOApp {

  object Config extends MxConfig {
    def default() =
      Config(
        GitRootDirectory(FileSystem.userHome.subdir("server-app-configs").asNioPath.toAbsolutePath.toString),
        ServerName.thisServer(),
      )
  }

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
      .map { jsonStr =>
        try {
          json.unsafeRead[Config](jsonStr)
        } catch {
          case e: Exception =>
            val msg = s"error reading ${configFile}"
            logger.warn(msg, e)
            throw new RuntimeException(msg, e)
        }
      }
      .getOrElse {
        val d = Config.default()
        if (d.gitRootDirectory.unresolvedDirectory.exists()) {
          d
        } else {
          sys.error(s"tried using default config ${d} but ${d.gitRootDirectory} does not exist")
        }
      }

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
