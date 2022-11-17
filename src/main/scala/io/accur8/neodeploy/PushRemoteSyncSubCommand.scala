package io.accur8.neodeploy


import a8.shared.app.BootstrappedIOApp
import a8.shared.app.BootstrappedIOApp.BootstrapEnv
import io.accur8.neodeploy.model.{ApplicationName, ServerName, UserLogin}
import io.accur8.neodeploy.resolvedmodel.{ResolvedApp, ResolvedServer, ResolvedUser}
import zio.{Chunk, Task, UIO, ZIO}
import a8.shared.SharedImports._
import zio.process.CommandError

import scala.util.Try

case class PushRemoteSyncSubCommand(filterServers: Iterable[ServerName], filterUsers: Iterable[UserLogin], filterApps: Iterable[ApplicationName]) extends BootstrappedIOApp {

  lazy val resolvedRepository = LocalUserSyncSubCommand(Vector.empty).resolvedRepository

  lazy val validateRepo = ValidateRepo(resolvedRepository)

  lazy val fitleredServers =
    resolvedRepository
      .servers
      .filter(serverMatches)

  def serverMatches(resolvedServer: ResolvedServer): Boolean =
    filterServers.isEmpty || filterServers.exists(_ === resolvedServer.name)

  def userMatches(resolvedUser: ResolvedUser): Boolean =
    filterUsers.isEmpty || filterUsers.exists(_ === resolvedUser.login)

  override def runT: ZIO[BootstrapEnv, Throwable, Unit] =
    for {
      _ <- validateRepo.run
      _ <-
        ZIO.collectAllPar(
          fitleredServers
            .map(pushRemoteServerSync)
        )
    } yield ()

  def pushRemoteServerSync(resolvedServer: ResolvedServer): UIO[Vector[Either[Throwable,Command.Result]]] = {
    val filteredUsers = resolvedServer.resolvedUsers.filter(userMatches)
    ZIO.collectAllPar(
      filteredUsers
        .map(pushRemoteUserSync)
    )
  }

  def pushRemoteUserSync(resolvedUser: ResolvedUser): UIO[Either[Throwable,Command.Result]] = {

    val filteredAppArgs: Seq[String] =
      filterApps match {
        case l if l.isEmpty =>
          Nil
        case l =>
          List(z"--apps", l.map(a => z"${a}").mkString(","))
      }

    val remoteServer = resolvedUser.server.name

    val validateRepoEffect =
      ValidateRepo(resolvedRepository).run

    val rsyncEffect =
      Command(
        "rsync",
        "--delete",
        "--archive",
        "--partial",
//        "--progress",
        "--verbose",
        "--stats",
        "--exclude=\".*\"",
        z"--include=${remoteServer}/",
        z"--include=\"${remoteServer}/${resolvedUser.login}/**\"",
        "--include=config.hocon",
        "--exclude=\"*\"",
        ".",
        z"${resolvedUser.qname}:server-app-configs"
      )
        .workingDirectory(resolvedRepository.gitRootDirectory.unresolvedDirectory)
        .execLogOutput

    val sshEffect =
      Command("ssh", z"${resolvedUser.login}@${resolvedUser.server.name}", "--")
        .appendArgs("~/.nix-profile/bin/a8-versions", "blob")
        .appendArgsSeq(filteredAppArgs)
        .execLogOutput

    (validateRepoEffect *> rsyncEffect *> sshEffect)
      .either
      .tap {
        case Left(ce) =>
          loggerF.warn(s"pushRemoteUserSync(${resolvedUser.qname}) failed -- ${ce}", ce)
        case Right(r) =>
          zunit
      }

  }

}
