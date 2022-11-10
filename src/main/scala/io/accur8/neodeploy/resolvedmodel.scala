package io.accur8.neodeploy


import a8.shared.{CascadingHocon, CompanionGen, ConfigMojo, ConfigMojoOps, Exec, StringValue}
import a8.shared.FileSystem.{Directory, dir}
import model._
import a8.shared.SharedImports._
import a8.shared.app.LoggingF
import a8.shared.json.JsonCodec
import a8.shared.json.ast.{JsDoc, JsObj, JsVal}
import io.accur8.neodeploy.Mxresolvedmodel.MxStoredSyncState
import io.accur8.neodeploy.Sync.{ContainerSteps, Phase, ResolvedSteps, Step, SyncName}
import zio.{Task, ZIO}


object resolvedmodel extends LoggingF {

  case class ResolvedUser(
    descriptor: UserDescriptor,
    home: Directory,
    server: ResolvedServer,
  ) {

    def personnel =
      descriptor
        .authorizedPersonnel
        .flatMap( personnelId =>
          server
            .repository
            .personnel(personnelId)
        )

    def authorizedKeys =
      descriptor
        .authorizedKeys
  }


  case class ResolvedServer(
    descriptor: ServerDescriptor,
    gitServerDirectory: GitServerDirectory,
    repository: ResolvedRepository,
  ) {

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
      ZIO
        .attemptBlocking(
          Exec(command.args).execCaptureOutput(false)
        )
        .logVoid
    }

    def name = descriptor.name

    lazy val resolvedApps =
      gitServerDirectory
        .unresolvedDirectory
        .subdirs()
        .flatMap(loadResolvedAppFromDisk)

    def appsRootDirectory: AppsRootDirectory = descriptor.appInstallDirectory
    def supervisorDirectory: SupervisorDirectory = descriptor.supervisorDirectory
    def caddyDirectory: CaddyDirectory = descriptor.caddyDirectory

    def loadResolvedAppFromDisk(appConfigDir: Directory): Option[ResolvedApp] = {
      val appDescriptorFile = appConfigDir.file("application.json")
      appDescriptorFile
        .readAsStringOpt()
        .flatMap { appDescriptorJsonStr =>
          json.read[ApplicationDescriptor](appDescriptorJsonStr) match {
            case Left(e) =>
              logger.error(s"Failed to load application descriptor file: $appDescriptorFile -- ${e.prettyMessage}")
              None
            case Right(v) =>
              Some(ResolvedApp(v, this, appConfigDir))
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
  ) {
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

    def personnel(id: PersonnelId): Option[Personnel] = {
      val result =
        descriptor
          .personnel
          .find(_.id === id)
      if ( result.isEmpty ) {
        logger.warn(s"Personnel not found: $id")
      }
      result
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

}
