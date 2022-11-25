package io.accur8.neodeploy


import a8.shared.app.BootstrappedIOApp
import a8.shared.app.BootstrappedIOApp.BootstrapEnv
import io.accur8.neodeploy.model.{ApplicationName, ServerName, UserLogin}
import io.accur8.neodeploy.resolvedmodel.{ResolvedApp, ResolvedServer, ResolvedUser}
import zio.{Chunk, Task, UIO, ZIO}
import a8.shared.SharedImports._
import zio.process.CommandError

import scala.util.Try
import a8.shared.FileSystem

case class PushRemoteSyncSubCommand(filterServers: Iterable[ServerName], filterUsers: Iterable[UserLogin], filterApps: Iterable[ApplicationName], remoteVerbose: Boolean) extends BootstrappedIOApp {

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

  def copyManagedPublicKeysToStagingEffect(stagingDir: FileSystem.Directory): Task[Unit] =
    ZIO.attemptBlocking {
      val publicKeysDir =
        stagingDir
          .subdir("public-keys")
          .resolve
      for {
        user <- resolvedRepository.allUsers
        publicKey <- user.publicKey
      } yield {
        publicKeysDir
          .file(user.qualifiedUserName.value)
          .write(publicKey.value)
      }
      ()
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

    val stagingDir = resolvedRepository.gitRootDirectory.resolvedDirectory.subdir(".staging").resolve

    stagingDir.deleteChildren()

    val setupStagingDataEffect =  
      FileSystemAssist.FileSet(resolvedRepository.gitRootDirectory.resolvedDirectory)
        .addPath("config.hocon")
        .addPath("public-keys")
        .addPath(z"${remoteServer}/${resolvedUser.login}")
        .copyTo(stagingDir)

    val rsyncEffect =
      Command("rsync", "--delete", "--archive", "--verbose", "--recursive", ".", z"${resolvedUser.sshName}:server-app-configs/")
        .workingDirectory(stagingDir)
        .execLogOutput

    val sshEffect =
      Command("ssh", z"${resolvedUser.login}@${resolvedUser.server.name}", "--")
        .appendArgs("~/.nix-profile/bin/a8-versions")
        .appendArgsSeq(remoteVerbose.toOption("--verbose"))
        .appendArgs("local_user_sync")
        .appendArgsSeq(filteredAppArgs)
        .execLogOutput

    (copyManagedPublicKeysToStagingEffect(stagingDir) *> setupStagingDataEffect *> rsyncEffect *> sshEffect)
      .either
      .tap {
        case Left(ce) =>
          loggerF.warn(s"pushRemoteUserSync(${resolvedUser.qname}) failed -- ${ce}", ce)
        case Right(r) =>
          zunit
      }

  }

}
