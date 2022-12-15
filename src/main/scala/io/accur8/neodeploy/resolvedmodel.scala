package io.accur8.neodeploy


import a8.shared.{CascadingHocon, CompanionGen, ConfigMojo, ConfigMojoOps, Exec, StringValue, ZString}
import a8.shared.FileSystem.{Directory, File, dir, userHome}
import model._
import a8.shared.SharedImports._
import a8.shared.ZString.ZStringer
import a8.shared.app.LoggingF
import a8.shared.json.JsonCodec
import a8.shared.json.ast.{JsDoc, JsObj, JsVal}
import io.accur8.neodeploy.Sync.SyncName
import zio.{Chunk, Task, UIO, ZIO}
import PredefAssist._
import a8.versions.model.ResolvedPersonnel
import io.accur8.neodeploy.resolvedmodel.ResolvedPgbackrestServer

object resolvedmodel extends LoggingF {

  case class ResolvedUser(
    descriptor: UserDescriptor,
    home: Directory,
    server: ResolvedServer,
  ) {

    lazy val gitAppsDirectory =
      server.gitServerDirectory.unresolvedDirectory.subdir(descriptor.login.value)

    lazy val resolvedApps: Vector[ResolvedApp] =
      gitAppsDirectory
        .subdirs()
        .flatMap { appDir =>
          server.loadResolvedAppFromDisk(appDir, this)
        }
        .toVector

    lazy val plugins = UserPlugin.UserPlugins(descriptor.plugins, this)

    lazy val a8VersionsExec =
      descriptor
        .a8VersionsExec
        .orElse(server.descriptor.a8VersionsExec)
        .getOrElse("/usr/bin/a8-versions")

    lazy val appsRootDirectory: AppsRootDirectory =
      descriptor
        .appInstallDirectory
        .getOrElse(AppsRootDirectory(home.subdir("apps").absolutePath))

    lazy val qualifiedUserNames: Seq[QualifiedUserName] =
      Vector(qualifiedUserName) ++ descriptor.aliases

    lazy val qualifiedUserName: QualifiedUserName =
      QualifiedUserName(qname)

    def qname = z"${login}@${server.name}"

    def sshName = z"${login}@${server.descriptor.vpnDomainName}"

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

    def publicKeys: Vector[AuthorizedKey] =
      sshPublicKeyFileInRepo
        .readAsStringOpt()
        .map(line => Vector(AuthorizedKey(line)))
        .getOrElse(server.repository.authorizedKeys(qualifiedUserName))

    def resolvedAuthorizedKeys =
      descriptorAuthorizedKeys ++ plugins.authorizedKeys

    def descriptorAuthorizedKeys: Vector[AuthorizedKey] =
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

//    def appCommandStep(phase: Phase, supervisorAction: String, commandGetter: ApplicationDescriptor=>Option[Command], currentApplicationOpt: Option[ApplicationDescriptor]): Seq[Step] = {
//      currentApplicationOpt match {
//        case None =>
//          Seq.empty
//        case Some(currentApplication) =>
//          val command =
//            commandGetter(currentApplication)
//              .getOrElse(supervisorCommand(supervisorAction, currentApplication.name))
//          Seq(Step.runCommand(phase, command, failOnNonZeroExitCode = false))
//      }
//    }


    def execCommand(command: Command): Task[Unit] = {
      val logLinesEffect: Chunk[String] => UIO[Unit] = { lines: Chunk[String] =>
        loggerF.debug(s"command output chunk -- ${lines.mkString("\n    ", "\n    ", "\n    ")}")
      }
      command
        .exec(logLinesEffect = logLinesEffect)
        .as(())
    }

    def name = descriptor.name

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

    def fetchUser(qname: QualifiedUserName): ResolvedUser =
      users
        .find(_.qualifiedUserName === qname)
        .getOrError(s"user ${qname} not found")


    lazy val userPlugins: Vector[UserPlugin] =
      for {
        server <- servers
        user <- server.resolvedUsers
        plugin <- user.plugins.pluginInstances
      } yield plugin

    def server(serverName: ServerName): ResolvedServer =
      servers
        .find(_.name == serverName)
        .getOrError(z"server ${serverName} not found")

    def authorizedKeys(id: QualifiedUserName): Vector[AuthorizedKey] = {

      def personnelFinder =
        personnel
          .find(_.id === id)
          .map(_.resolvedKeys)

      def publicKeysFinder =
        gitRootDirectory
          .resolvedDirectory
          .subdir("public-keys")
          .file(id.value)
          .readAsStringOpt()
          .map { contents =>
            Vector(AuthorizedKey(s"# from ${id}"), AuthorizedKey(contents))
          }

      def usersFinder =
        users
          .find(_.qualifiedUserName === id)
          .map(_.publicKeys)

      val result = personnelFinder orElse publicKeysFinder orElse usersFinder

      result match {
        case Some(v) =>
          v
        case None =>
          logger.warn(s"unable to find keys for ${id}")
          Vector.empty
      }

    }

    lazy val resolvedPgbackrestServerOpt =
      userPlugins
        .collect {
          case rps: ResolvedPgbackrestServer =>
            rps
        }
        .headOption

    lazy val personnel =
      descriptor
        .publicKeys
        .map { p =>
          ResolvedPersonnel(
            this,
            p,
          )
        }

    lazy val users =
      servers.flatMap(_.resolvedUsers)

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

    lazy val allUsers: Seq[ResolvedUser] =
      servers
        .flatMap(_.resolvedUsers)

  }


//  object StoredSyncState extends MxStoredSyncState {
//    def fromResolvedSteps[A : JsonCodec](name: StringValue, descriptor: A, containerSteps: ContainerSteps): StoredSyncState = {
//      val statesJsoValues =
//        containerSteps
//          .resolvedSteps
//          .flatMap(rs => rs.newState.map(rs.syncName.value -> _))
//          .toMap
//
//      new StoredSyncState(
//        name.value,
//        descriptor.toJsDoc,
//        JsObj(statesJsoValues).toJsDoc,
//      )
//    }
//  }
//  @CompanionGen
//  case class StoredSyncState(
//    name: String,
//    descriptor: JsDoc,
//    states: JsDoc,
//  ) {
//    def syncState(name: SyncName): Option[JsVal] =
//      states.actualJsVal match {
//        case jso: JsObj =>
//          jso
//            .values
//            .get(name.value)
//        case _ =>
//          None
//      }
//  }

  object ResolvedRSnapshotClient extends UserPlugin.Factory.AbstractFactory[RSnapshotClientDescriptor]("rsnapshotClient")

  case class ResolvedRSnapshotClient(
    descriptor: RSnapshotClientDescriptor,
    user: ResolvedUser,
  ) extends UserPlugin {

    def descriptorJson = descriptor.toJsVal

    def name = "rsnapshotClient"

    lazy val server: ResolvedServer = user.server

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
    lazy val resolvedBackupLines =
      descriptor
        .directories
        .map { directory =>
          val parts = Seq("backup", z"${sshUrl}:${directory}", z"${user.server.name}/")
          parts
            .mkString("\t")
        }
        .mkString("\n")

    override def authorizedKeys: Vector[AuthorizedKey] =
      user
        .server
        .repository
        .userPlugins
        .flatMap {
          case rss: ResolvedRSnapshotServer =>
            rss.user.publicKeys
          case _ =>
            Vector.empty
        }

  }

  object ResolvedRSnapshotServer extends UserPlugin.Factory.AbstractFactory[RSnapshotServerDescriptor]("rsnapshotServer")

  case class ResolvedRSnapshotServer(
    descriptor: RSnapshotServerDescriptor,
    user: ResolvedUser,
  ) extends UserPlugin {

    def descriptorJson = descriptor.toJsVal

    def name = "rsnapshotServer"

    override def authorizedKeys: Vector[AuthorizedKey] =
      Vector.empty

    lazy val server: ResolvedServer = user.server

    lazy val clients =
      user
        .server
        .repository
        .userPlugins
        .collect {
          case rc: ResolvedRSnapshotClient =>
            rc
        }
  }


  object ResolvedPgbackrestClient extends UserPlugin.Factory.AbstractFactory[PgbackrestClientDescriptor]("pgbackrestClient")

  case class ResolvedPgbackrestClient(
    descriptor: PgbackrestClientDescriptor,
    user: ResolvedUser,
  ) extends UserPlugin {

    def stanzaName = descriptor.stanzaNameOverride.getOrElse(user.server.name.value)

    def descriptorJson = descriptor.toJsVal

    override def name: String = "pgbackrestClient"

    def resolvedServer: ResolvedPgbackrestServer =
      user
        .server
        .repository
        .resolvedPgbackrestServerOpt
        .getOrError("must have a pgbackrest server configured")

    override def authorizedKeys: Vector[AuthorizedKey] =
      user
        .server
        .repository
        .userPlugins
        .flatMap {
          case rps: ResolvedPgbackrestServer =>
            rps.user.publicKeys
          case _ =>
            Vector.empty
        }

    lazy val server: ResolvedServer = user.server

  }


  object ResolvedPgbackrestServer extends UserPlugin.Factory.AbstractFactory[PgbackrestServerDescriptor]("pgbackrestServer")

  case class ResolvedPgbackrestServer(
    descriptor: PgbackrestServerDescriptor,
    user: ResolvedUser,
  ) extends UserPlugin {

    def descriptorJson = descriptor.toJsVal

    override def name: String = "pgbackrestServer"

    override def authorizedKeys: Vector[AuthorizedKey] =
      user
        .server
        .repository
        .userPlugins
        .flatMap {
          case rpc: ResolvedPgbackrestClient =>
            rpc.user.publicKeys
          case _ =>
            Vector.empty
        }

    lazy val server: ResolvedServer = user.server

    lazy val clients =
      user
        .server
        .repository
        .userPlugins
        .collect {
          case rc: ResolvedPgbackrestClient =>
            rc
        }

  }

}
