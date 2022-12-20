package io.accur8.neodeploy


import a8.shared.{CascadingHocon, CompanionGen, ConfigMojo, ConfigMojoOps, Exec, HoconOps, StringValue, ZString}
import a8.shared.ZFileSystem.{Directory, File, dir, userHome}
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
import com.typesafe.config.{Config, ConfigFactory, ConfigValue}
import io.accur8.neodeploy.resolvedmodel.ResolvedPgbackrestServer
import io.accur8.neodeploy.systemstate.SystemStateModel.M

object resolvedmodel extends LoggingF {

  case class ResolvedUser(
    descriptor: UserDescriptor,
    home: Directory,
    server: ResolvedServer,
  ) {

    lazy val gitAppsDirectory =
      server.gitServerDirectory.subdir(descriptor.login.value)

    lazy val resolvedAppsM: M[Vector[ResolvedApp]] =
      gitAppsDirectory
        .subdirs
        .flatMap(
          _.map(appDir => server.loadResolvedAppFromDisk(appDir, this))
            .sequence
            .map(_.flatten.toVector)
        )

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
        .subdir(descriptor.login.value)

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

    def publicKeys: Task[Vector[AuthorizedKey]] =
      sshPublicKeyFileInRepo
        .readAsStringOpt
        .map(
          _.map(line => Vector(AuthorizedKey(line)))
        )
        .flatMap {
          case Some(v) =>
            zsucceed(v)
          case None =>
            server.repository.authorizedKeys(qualifiedUserName)
        }

    def resolvedAuthorizedKeys = {
      for {
        pak <- plugins.authorizedKeys
        dak <- descriptorAuthorizedKeys
      } yield pak ++ dak
    }

    def descriptorAuthorizedKeys: Task[Vector[AuthorizedKey]] =
      descriptor
        .authorizedKeys
        .map(n => server.repository.authorizedKeys(n))
        .sequence
        .map(_.flatten)

  }


  case class ResolvedServer(
    descriptor: ServerDescriptor,
    gitServerDirectory: GitServerDirectory,
    repository: ResolvedRepository,
  ) {

    def fetchUserZ(login: UserLogin): ZIO[Any, Throwable, ResolvedUser] =
      fetchUserOpt(login)
        .map(u => zsucceed(u))
        .getOrElse(zfail(new RuntimeException(z"user ${login} not found")))

    def fetchUserOpt(login: UserLogin): Option[ResolvedUser] =
      resolvedUsers
        .find(_.login == login)

    lazy val resolvedUsers =
      descriptor
        .users
        .map( userDescriptor =>
          ResolvedUser(
            descriptor = userDescriptor,
            home = userDescriptor.home.getOrElse(dir(z"/home/${userDescriptor.login}")),
            server = this,
          )
        )

    def supervisorCommand(action: String, applicationName: ApplicationName): Command =
      Command(Seq(
        descriptor.supervisorctlExec.getOrElse("supervisorctl"),
        action,
        applicationName.value
      ))

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

    def loadResolvedAppFromDisk(appConfigDir: Directory, resolvedUser: ResolvedUser): Task[Option[ResolvedApp]] = {
      val appDescriptorFilesZ =
        Vector(
          appConfigDir.file("secret.props.priv"),
          appConfigDir.file("application.json"),
          appConfigDir.file("application.hocon"),
        )
          .map(f => f.exists.map(_ -> f))
          .sequence
          .map(
            _.collect {
              case (true, f) =>
                f
            }
          )

      val appDir = resolvedUser.appsRootDirectory.subdir(appConfigDir.name)


      val baseConfigMap =
        Map(
          "appDir" -> appDir.absolutePath,
          "dataDir" -> appDir.subdir("data").absolutePath,
        )

      val baseConfig = ConfigFactory.parseMap(baseConfigMap.asJava)

      appDescriptorFilesZ
        .map { appDescriptorFiles =>
          try {
            import HoconOps._

            val configs =
              appDescriptorFiles
                .map(f => HoconOps.impl.loadConfig(f.asNioPath))
            if (configs.isEmpty) {
              None
            } else {
              val resolvedConfig =
                (configs ++ Vector(baseConfig))
                  .reduceLeft(_.resolveWith(_))

              val descriptor =
                resolvedConfig
                  .read[ApplicationDescriptor]
              ResolvedApp(descriptor, appConfigDir, resolvedUser).some
            }
          } catch {
            case IsNonFatal(e) =>
              logger.error(s"Failed to load application descriptor file: $appDescriptorFiles", e)
              None
          }

        }
    }
  }

  object ResolvedApp {
  }

  case class ResolvedApp(
    descriptor: ApplicationDescriptor,
    gitDirectory: Directory,
    user: ResolvedUser,
  ) {
    def server = user.server
    def name = descriptor.name
    def appDirectory = user.appsRootDirectory.subdir(descriptor.name.value)
  }


  object ResolvedRepository {

    def loadFromDisk(gitRootDirectory: GitRootDirectory): ResolvedRepository = {
      val cascadingHocon =
        CascadingHocon
          .loadConfigsInDirectory(gitRootDirectory.asNioPath, recurse = false)
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

    def authorizedKeys(id: QualifiedUserName): Task[Vector[AuthorizedKey]] = {

      def personnelFinderZ =
        personnel
          .find(_.id === id)
          .map(_.resolvedKeysZ)
          .getOrElse(zsucceed(Vector.empty))

      def publicKeysFinderZ =
        gitRootDirectory
          .subdir("public-keys")
          .file(id.value)
          .readAsStringOpt
          .map {
            case Some(contents) =>
              val keys =
                contents
                  .linesIterator
                  .filterNot(_.isBlank)
                  .map(AuthorizedKey.apply)
                  .toVector
              Vector(AuthorizedKey(s"# start ${id}")) ++ keys ++ Vector(AuthorizedKey(s"# end ${id}"))
            case None =>
              Vector.empty
          }

      def usersFinderZ =
        users
          .find(_.qualifiedUserName === id)
          .map(_.publicKeys)
          .getOrElse(zsucceed(Vector.empty))

      for {
        personnelFinder <- personnelFinderZ
        publicKeys <- publicKeysFinderZ
        usersKeys <- usersFinderZ
      } yield {
        val result = personnelFinder ++ publicKeys ++ usersKeys

        result match {
          case v if v.nonEmpty =>
            v
          case _ =>
            logger.warn(s"unable to find keys for ${id}")
            Vector.empty
        }
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
            GitServerDirectory(gitRootDirectory.subdir(serverDescriptor.name.value).asNioPath.toString),
            this,
          )
        }

    lazy val allUsers: Seq[ResolvedUser] =
      servers
        .flatMap(_.resolvedUsers)

  }

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

    override def authorizedKeys: Task[Vector[AuthorizedKey]] =
      user
        .server
        .repository
        .userPlugins
        .map {
          case rss: ResolvedRSnapshotServer =>
            rss.user.publicKeys
          case _ =>
            zsucceed(Vector.empty)
        }
        .sequence
        .map(_.flatten)

  }

  object ResolvedRSnapshotServer extends UserPlugin.Factory.AbstractFactory[RSnapshotServerDescriptor]("rsnapshotServer")

  case class ResolvedRSnapshotServer(
    descriptor: RSnapshotServerDescriptor,
    user: ResolvedUser,
  ) extends UserPlugin {

    def descriptorJson = descriptor.toJsVal

    def name = "rsnapshotServer"

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

    override def authorizedKeys: Task[Vector[AuthorizedKey]] =
      user
        .server
        .repository
        .userPlugins
        .map {
          case rps: ResolvedPgbackrestServer =>
            rps.user.publicKeys
          case _ =>
            zsucceed(Vector.empty)
        }
        .sequence
        .map(_.flatten)

    lazy val server: ResolvedServer = user.server

  }


  object ResolvedPgbackrestServer extends UserPlugin.Factory.AbstractFactory[PgbackrestServerDescriptor]("pgbackrestServer")

  case class ResolvedPgbackrestServer(
    descriptor: PgbackrestServerDescriptor,
    user: ResolvedUser,
  ) extends UserPlugin {

    def descriptorJson = descriptor.toJsVal

    override def name: String = "pgbackrestServer"

    override def authorizedKeys: Task[Vector[AuthorizedKey]] =
      user
        .server
        .repository
        .userPlugins
        .map {
          case rpc: ResolvedPgbackrestClient =>
            rpc.user.publicKeys
          case _ =>
            zsucceed(Vector.empty)
        }
        .sequence
        .map(_.flatten)

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
