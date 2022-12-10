package io.accur8.neodeploy


import a8.shared.SharedImports._
import a8.shared.{CompanionGen, FileSystem}
import a8.shared.app.BootstrappedIOApp
import a8.shared.app.BootstrappedIOApp.BootstrapEnv
import io.accur8.neodeploy.MxLocalUserSyncSubCommand._
import io.accur8.neodeploy.LocalUserSyncSubCommand.Config
import io.accur8.neodeploy.PushRemoteSyncSubCommand.Filter
import io.accur8.neodeploy.Sync.SyncName
import io.accur8.neodeploy.model.{ApplicationName, AppsRootDirectory, CaddyDirectory, DomainName, GitRootDirectory, GitServerDirectory, ServerName, SupervisorDirectory, UserLogin}
import io.accur8.neodeploy.resolvedmodel.ResolvedRepository
import zio.{ZIO, ZLayer}
import systemstate.SystemStateModel._

import java.net.InetAddress

object LocalUserSyncSubCommand {

  object Config extends MxConfig {
    def default() =
      Config(
        GitRootDirectory(FileSystem.userHome.subdir("server-app-configs").asNioPath.toAbsolutePath.toString),
        ServerName.thisServer(),
        userLogin = UserLogin.thisUser(),
      )
  }

  @CompanionGen
  case class Config(
    gitRootDirectory: GitRootDirectory,
    serverName: ServerName,
    userLogin: UserLogin = UserLogin.thisUser(),
  )

}

case class LocalUserSyncSubCommand(appsFilter: Filter[ApplicationName], syncsFilter: Filter[SyncName]) extends BootstrappedIOApp {

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
      .find(s => s.name == config.serverName || s.descriptor.aliases.contains(config.serverName))
      .getOrError(s"server ${config.serverName} not found")

  override def runT: ZIO[BootstrapEnv, Throwable, Unit] =
    LocalUserSync(resolvedServer.fetchUser(config.userLogin), appsFilter, syncsFilter)
      .run
      .logVoid
      .provide(
        ZLayer.succeed(HealthchecksDotIo(resolvedRepository.descriptor.healthchecksApiToken)),
        SystemStateLogger.simpleLayer,
      )


}
