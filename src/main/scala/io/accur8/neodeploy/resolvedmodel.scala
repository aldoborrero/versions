package io.accur8.neodeploy


import a8.shared.{CascadingHocon, CompanionGen}
import a8.shared.FileSystem.Directory
import model._
import a8.shared.SharedImports._
import a8.shared.json.ast.{JsDoc, JsObj, JsVal}
import io.accur8.neodeploy.Mxresolvedmodel.MxStoredSyncState
import io.accur8.neodeploy.Sync.SyncName


object resolvedmodel {

  case class ResolvedUser(
    descriptor: UserDescriptor,
    home: Directory,
    appsDirectory: Directory,
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
    def name = descriptor.name
    lazy val resolvedApps = {
      descriptor
        .applications
        .map(a => ResolvedApp(a, this, gitServerDirectory.unresolvedDirectory.subdir(a.name.value)))
    }
    def appsRootDirectory: AppsRootDirectory = descriptor.appsDirectory
    def supervisorDirectory: SupervisorDirectory = descriptor.supervisorDirectory
    def caddyDirectory: CaddyDirectory = descriptor.caddyDirectory

  }

  object ResolvedApp {

    def supervisorCommand(action: String, applicationName: ApplicationName): Command =
      Command(Seq(
        "supervisorctl",
        action,
        applicationName.value
      ))

  }

  case class ResolvedApp(
    application: ApplicationDescriptor,
    server: ResolvedServer,
    gitDirectory: Directory,
  ) {
  }


  object ResolvedRepository {

    def loadFromDisk(gitRootDirectory: GitRootDirectory): ResolvedRepository = {
      val ch = CascadingHocon.loadConfigsInDirectory(gitRootDirectory.unresolvedDirectory.asNioPath, recurse = false)
      ch.resolve
      val file = gitRootDirectory.unresolvedDirectory.file("repository.hocon")
      val jsonStr = file.readAsString()
      val desc = json.unsafeRead[RepositoryDescriptor](jsonStr)
      ResolvedRepository(
        desc,
        gitRootDirectory,
      )
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
    def apply(appName: ApplicationName, applicationDescriptor: ApplicationDescriptor, states: Seq[(SyncName,Option[JsVal])]): StoredSyncState = {
      val statesJsoValues =
        states
          .flatMap {
            case (syncName, Some(state)) =>
              Some(syncName.value -> state)
            case _ =>
              None
          }
          .toMap

      new StoredSyncState(
        appName.value,
        applicationDescriptor.toJsDoc,
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
    def syncState(name: String): Option[JsVal] =
      states.actualJsVal match {
        case jso: JsObj =>
          jso.values.get(name)
        case _ =>
          None
      }
  }

}
