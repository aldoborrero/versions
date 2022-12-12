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
import a8.shared.ZString.ZStringer
import io.accur8.neodeploy.PushRemoteSyncSubCommand.Filter
import io.accur8.neodeploy.Sync.SyncName

object PushRemoteSyncSubCommand {

  object Filter {
    def allowAll[A : ZStringer] = Filter[A]("", Vector.empty)
  }

  case class Filter[A : ZStringer](argName: String, values: Iterable[A]) {

    def hasValues = values.nonEmpty

    def args =
      values
        .nonEmpty
        .toOption(
          Some("--" + argName) ++ values.map(v => implicitly[ZStringer[A]].toZString(v).toString())
        )
        .toVector
        .flatten

    def include(a: A): Boolean =
      matches(a)

    def matches(a: A): Boolean =
      values.isEmpty || values.find(_ == a).nonEmpty

  }

}

case class PushRemoteSyncSubCommand(
  serversFilter: Filter[ServerName],
  usersFilter: Filter[UserLogin],
  appsFilter: Filter[ApplicationName],
  syncsFilter: Filter[SyncName],
  remoteDebug: Boolean,
  remoteTrace: Boolean,
) extends BootstrappedIOApp {

  lazy val validateParameters =
    if ( serversFilter.hasValues || usersFilter.hasValues || appsFilter.hasValues ) {
      ZIO.unit
    } else {
      ZIO.fail(new RuntimeException("must supply servers, users or apps"))
    }

  lazy val resolvedRepository = LocalUserSyncSubCommand(Filter.allowAll, Filter.allowAll).resolvedRepository

  lazy val validateRepo = ValidateRepo(resolvedRepository)

  lazy val fitleredServers =
    resolvedRepository
      .servers
      .filter(s => serversFilter.include(s.name))

  override def runT: ZIO[BootstrapEnv, Throwable, Unit] =
    for {
      _ <- validateParameters
      _ <- validateRepo.run
      _ <-
        ZIO.collectAllPar(
          fitleredServers
            .map(pushRemoteServerSync)
        )
    } yield ()

  def pushRemoteServerSync(resolvedServer: ResolvedServer): UIO[Vector[Either[Throwable,Command.Result]]] = {
    val filteredUsers = resolvedServer.resolvedUsers.filter(u => usersFilter.include(u.login))
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
        publicKey <- user.publicKeys
        qualifiedUserName <- user.qualifiedUserNames
      } yield {
        publicKeysDir
          .file(qualifiedUserName.value)
          .write(publicKey.value)
      }
      ()
    }

  def pushRemoteUserSync(resolvedUser: ResolvedUser): UIO[Either[Throwable,Command.Result]] = {

    val remoteServer = resolvedUser.server.name

    val stagingDir = resolvedRepository.gitRootDirectory.resolvedDirectory.subdir(s".staging/${resolvedUser.qname}").resolve

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
      Command("ssh", z"${resolvedUser.sshName}", "--")
        .appendArgs(resolvedUser.a8VersionsExec)
        .appendArgsSeq(remoteDebug.toOption("--debug"))
        .appendArgsSeq(remoteTrace.toOption("--trace"))
        .appendArgs("local_user_sync")
        .appendArgsSeq(appsFilter.args)
        .appendArgsSeq(syncsFilter.args)
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
