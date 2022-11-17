package io.accur8.neodeploy


import a8.shared.{CascadingHocon, CompanionGen, ConfigMojo, ConfigMojoOps, Exec, StringValue, ZString}
import a8.shared.FileSystem.{Directory, File, dir}
import model._
import a8.shared.SharedImports._
import a8.shared.ZString.ZStringer
import a8.shared.app.LoggingF
import a8.shared.json.JsonCodec
import a8.shared.json.ast.{JsDoc, JsObj, JsVal}
import io.accur8.neodeploy.Mxresolvedmodel.MxStoredSyncState
import io.accur8.neodeploy.Sync.{ContainerSteps, Phase, ResolvedSteps, Step, SyncName}
import zio.{Chunk, Task, UIO, ZIO}
import PredefAssist._

object resolvedmodel extends LoggingF {

  case class ResolvedUser(
    descriptor: UserDescriptor,
    home: Directory,
    server: ResolvedServer,
  ) {

    lazy val qualifiedUserName = QualifiedUserName(qname)

    def qname = z"${login}@${server.name}"

    def login = descriptor.login

    lazy val repoDir =
      server
        .gitServerDirectory
        .resolvedDirectory
        .subdir(descriptor.login.value)
        .resolve

    def repoFile(subPath: String): File =
      repoDir
        .file(subPath)

    def tempSshPrivateKeyFileInRepo =
      repoFile("id_ed25519")

    def sshPrivateKeyFileInRepo =
      repoFile(z"id_ed25519.priv")

    def sshPublicKeyFileInRepo =
      repoFile(z"id_ed25519.pub")

    def sshPrivateKeyFileInHome =
      home.subdir(".ssh").file("id_ed25519")

    def sshPublicKeyFileInHome =
      home.subdir(".ssh").file("id_ed25519.pub")

    def publicKey: Option[AuthorizedKey] =
      sshPublicKeyFileInRepo
        .readAsStringOpt()
        .map(AuthorizedKey.apply)

    def authorizedKeys =
      descriptor
        .authorizedKeys
        .flatMap(n => server.repository.authorizedKeys(n))

  }


  case class ResolvedServer(
    descriptor: ServerDescriptor,
    gitServerDirectory: GitServerDirectory,
    repository: ResolvedRepository,
  ) {

    def fetchUser(login: UserLogin): ResolvedUser =
      resolvedUsers
        .find(_.login == login)
        .getOrError(s"cannot find ${login}")

    def fetchUserOpt(login: UserLogin): Option[ResolvedUser] =
      resolvedUsers
        .find(_.login == login)

    lazy val resolvedUsers =
      descriptor
        .users
        .map( userDescriptor =>
          ResolvedUser(
            descriptor = userDescriptor,
            home = dir(userDescriptor.home.getOrElse(z"/home/${userDescriptor.login}")),
            server = this,
          )
        )

    def supervisorCommand(action: String, applicationName: ApplicationName): Command =
      Command(Seq(
        descriptor.supervisorctlExec.getOrElse("supervisorctl"),
        action,
        applicationName.value
      ))

    def appCommandStep(phase: Phase, supervisorAction: String, currentApplicationOpt: Option[ApplicationDescriptor]): Seq[Step] = {
      currentApplicationOpt match {
        case None =>
          Seq.empty
        case Some(currentApplication) =>
          val command =
            currentApplication
              .stopServerCommand
              .getOrElse(supervisorCommand(supervisorAction, currentApplication.name))
          Seq(Step(
            phase = phase,
            description = s"run command -- ${command.args.mkString(" ")}",
            action = execCommand(command),
          ))
      }
    }


    def execCommand(command: Command): Task[Unit] = {
      val logLinesEffect: Chunk[String] => UIO[Unit] = { lines: Chunk[String] =>
        loggerF.debug(s"command output chunk -- ${lines.mkString("\n    ", "\n    ", "\n    ")}")
      }
      command
        .exec(logLinesEffect = logLinesEffect)
        .as(())
    }

    def name = descriptor.name

    lazy val resolvedApps =
      gitServerDirectory
        .unresolvedDirectory
        .subdirs()
        .flatMap { userDir =>
          fetchUserOpt(UserLogin(userDir.name)) match {
            case None =>
              logger.warn(z"no user found for ${userDir}")
              None
            case Some(user) =>
              userDir
                .subdirs()
                .flatMap { appDir =>
                  loadResolvedAppFromDisk(appDir, user)
                }
          }
        }

    def appsRootDirectory: AppsRootDirectory = descriptor.appInstallDirectory
    def supervisorDirectory: SupervisorDirectory = descriptor.supervisorDirectory
    def caddyDirectory: CaddyDirectory = descriptor.caddyDirectory

    def loadResolvedAppFromDisk(appConfigDir: Directory, resolvedUser: ResolvedUser): Option[ResolvedApp] = {
      val appDescriptorFile = appConfigDir.file("application.json")
      appDescriptorFile
        .readAsStringOpt()
        .flatMap { appDescriptorJsonStr =>
          json.read[ApplicationDescriptor](appDescriptorJsonStr) match {
            case Left(e) =>
              logger.error(s"Failed to load application descriptor file: $appDescriptorFile -- ${e.prettyMessage}")
              None
            case Right(v) =>
              Some(ResolvedApp(v, this, appConfigDir, resolvedUser))
          }
        }
    }
  }

  object ResolvedApp {
  }

  case class ResolvedApp(
    descriptor: ApplicationDescriptor,
    server: ResolvedServer,
    gitDirectory: Directory,
    user: ResolvedUser,
  ) {
    def name = descriptor.name
  }


  object ResolvedRepository {

    def loadFromDisk(gitRootDirectory: GitRootDirectory): ResolvedRepository = {
      val cascadingHocon =
        CascadingHocon
          .loadConfigsInDirectory(gitRootDirectory.unresolvedDirectory.asNioPath, recurse = false)
          .resolve
      val configMojo =
        ConfigMojoOps.impl.ConfigMojoRoot(
          cascadingHocon.config.root(),
          cascadingHocon,
        )
      val repositoryDescriptor = configMojo.as[RepositoryDescriptor]
      ResolvedRepository(repositoryDescriptor, gitRootDirectory)
    }

  }

  case class ResolvedRepository(
    descriptor: RepositoryDescriptor,
    gitRootDirectory: GitRootDirectory,
  ) {

    def server(serverName: ServerName): ResolvedServer =
      servers
        .find(_.name == serverName)
        .getOrError(z"server ${serverName} not found")

    def authorizedKeys(id: QualifiedUserName): Vector[AuthorizedKey] = {
      descriptor.publicKeys.find(_.id === id) match {
        case None =>
          val contentsOpt =
            gitRootDirectory
              .resolvedDirectory
              .subdir("public-keys")
              .file(id.value)
              .readAsStringOpt()
          contentsOpt match {
            case None =>
              logger.warn(s"no public key found for ${id}")
              Vector.empty
            case Some(contents) =>
              Vector(AuthorizedKey(s"# from ${id}"), AuthorizedKey(contents))
          }
        case Some(personnel) =>
          personnel.resolvedKeys
      }
    }

    lazy val servers =
      descriptor
        .servers
        .map { serverDescriptor =>
          ResolvedServer(
            serverDescriptor,
            GitServerDirectory(gitRootDirectory.unresolvedDirectory.subdir(serverDescriptor.name.value).asNioPath.toString),
            this,
          )
        }

    lazy val allUsers =
      servers
        .flatMap(_.resolvedUsers)

    lazy val rsnapshotClients: Vector[ResolvedRSnapshotClient] =
      servers
        .flatMap( server =>
          server
            .descriptor
            .rsnapshotClient
            .map(d => ResolvedRSnapshotClient(d, server, server.fetchUser(UserLogin("rsnapshot"))))
        )

  }


  object StoredSyncState extends MxStoredSyncState {
    def fromResolvedSteps[A : JsonCodec](name: StringValue, descriptor: A, containerSteps: ContainerSteps): StoredSyncState = {
      val statesJsoValues =
        containerSteps
          .resolvedSteps
          .flatMap(rs => rs.newState.map(rs.syncName.value -> _))
          .toMap

      new StoredSyncState(
        name.value,
        descriptor.toJsDoc,
        JsObj(statesJsoValues).toJsDoc,
      )
    }
  }
  @CompanionGen
  case class StoredSyncState(
    name: String,
    descriptor: JsDoc,
    states: JsDoc,
  ) {
    def syncState(name: SyncName): Option[JsVal] =
      states.actualJsVal match {
        case jso: JsObj =>
          jso
            .values
            .get(name.value)
        case _ =>
          None
      }
  }

  case class ResolvedRSnapshotClient(
    descriptor: RSnapshotClientDescriptor,
    server: ResolvedServer,
    user: ResolvedUser,
  ) {
    // this makes sure there is a tab separate the include|exclude keyword and the path
    lazy val resolvedIncludeExcludeLines =
      descriptor
        .includeExcludeLines
        .map { line =>
          line
            .splitList("[ \t]", limit = 2)
            .mkString("\t")
        }
        .mkString("\n")

    lazy val sshUrl: String = z"${user.login}@${server.name}"

    // this makes sure there is a tab separate the include|exclude keyword and the path
    lazy val resolvedBackupLines = {

      descriptor
        .directories
        .map { directory =>
          val parts = Seq("backup", z"${sshUrl}${directory}", "/")
          parts
            .mkString("\t")
        }
        .mkString("\n")
    }

  }

  case class ResolvedRSnapshotServer(
    clients: Vector[ResolvedRSnapshotClient],
    descriptor: RSnapshotServerDescriptor,
    server: ResolvedServer,
    user: ResolvedUser,
  )

}
