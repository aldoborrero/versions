package io.accur8.neodeploy


import a8.shared.SharedImports.{zservice, _}
import a8.shared.{CompanionGen, FileSystem, ZFileSystem}
import a8.shared.app.BootstrappedIOApp
import a8.shared.app.BootstrappedIOApp.BootstrapEnv
import io.accur8.neodeploy.MxLocalUserSyncSubCommand._
import io.accur8.neodeploy.LocalUserSyncSubCommand.Config
import io.accur8.neodeploy.PushRemoteSyncSubCommand.Filter
import io.accur8.neodeploy.Sync.SyncName
import io.accur8.neodeploy.model.{ApplicationName, AppsRootDirectory, CaddyDirectory, DomainName, GitRootDirectory, GitServerDirectory, ServerName, SupervisorDirectory, UserLogin}
import io.accur8.neodeploy.resolvedmodel.{ResolvedRepository, ResolvedServer, ResolvedUser}
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

  override def runT: ZIO[BootstrapEnv, Throwable, Unit] = {
    import Layers._
    runM
      .provide(
        configL,
        healthchecksDotIoL,
        resolvedRepositoryL,
        resolvedUserL,
        resolvedServerL,
        SystemStateLogger.simpleLayer,
      )
  }


  def runM: M[Unit] =
    for {
      user <- zservice[ResolvedUser]
      _ <-
        LocalUserSync(user, appsFilter, syncsFilter)
          .run
          .logVoid
    } yield ()


}
