package io.accur8.neodeploy


import a8.shared.CompanionGen
import a8.shared.FileSystem.Directory
import io.accur8.neodeploy.model.{ApplicationDescriptor, ApplicationName, AppsRootDirectory, CaddyDirectory, Command, DomainName, GitServerDirectory, Install, SupervisorDirectory, UserDescriptor, UserLogin}
import zio.{Task, ZIO}
import a8.shared.SharedImports._
import a8.shared.app.{Logging, LoggingF}
import a8.shared.json.ast
import a8.shared.json.ast.{JsDoc, JsObj, JsVal}
import a8.versions.Exec
import io.accur8.neodeploy.SyncServer.loadState
import io.accur8.neodeploy.resolvedmodel.{ResolvedApp, ResolvedServer, ResolvedUser, StoredSyncState}

object SyncServer extends Logging {

//  object Config extends MxConfig
//  @CompanionGen
//  case class Config(
//    supervisorDirectory: SupervisorDirectory,
//    caddyDirectory: CaddyDirectory,
//    appsRootDirectory: AppsRootDirectory,
//    gitServerDirectory: GitServerDirectory,
//    serverName: DomainName,
//  )

  def loadState(directory: Directory): Vector[StoredSyncState] = {
    directory
      .files()
      .toVector
      .flatMap { f =>
        try {
          val jsonStr = f.readAsString()
          json.unsafeRead[StoredSyncState](jsonStr).some
        } catch {
          case e: Throwable =>
            logger.error(s"error reading file ${f.canonicalPath}", e)
            None
        }
      }
  }

}


case class SyncServer(resolvedServer: ResolvedServer) extends LoggingF {

  lazy val userStateDirectory: Directory =
    ???

  lazy val applicationStateDirectory: Directory =
    resolvedServer
      .appsRootDirectory
      .unresolvedDirectory
      .subdir(".state")
      .resolve

  lazy val currentApplicationStates: Vector[StoredSyncState] =
    loadState(applicationStateDirectory)

  lazy val newResolvedApps: Iterable[ResolvedApp] =
    resolvedServer
      .resolvedApps

  lazy val currentApplicationStatesByName =
    currentApplicationStates
      .map(d => ApplicationName(d.name) -> d)
      .toMap

  lazy val newResolvedAppsByName: Map[model.ApplicationName, ResolvedApp] =
    newResolvedApps
      .map(ra => ra.application.name -> ra)
      .toMap

  lazy val allApplicationNames =
    (currentApplicationStatesByName.keySet ++ newResolvedAppsByName.keySet)
      .toVector
      .distinct

  lazy val appSyncs: Seq[Sync[_, ResolvedApp]] =
    Vector(
      CaddySync(resolvedServer.caddyDirectory),
      SupervisorSync(resolvedServer.supervisorDirectory),
      ApplicationInstallSync(resolvedServer.appsRootDirectory),
    )

  lazy val userSyncs: Seq[Sync[_, ResolvedUser]] =
    Vector(
      AuthorizedKeys2Sync,
    )

  def run: Task[Unit] =
    allApplicationNames
      .map(appName =>
        runSyncApplication(
          appName,
          currentApplicationStatesByName.get(appName),
          newResolvedAppsByName.get(appName),
        )
      )
      .sequencePar
      .logVoid

  def updateAppState(appName: ApplicationName, application: ApplicationDescriptor, states: Seq[(Sync.SyncName, Option[ast.JsVal])], delete: Boolean): Task[Unit] =
    ZIO.attemptBlocking {
      applicationStateDirectory.makeDirectories()
      val stateFile = applicationStateDirectory.file(appName.value + ".json")
      if (delete) {
        stateFile.delete()
      } else {
        val appSyncState =
          StoredSyncState(
            appName,
            application,
            states,
          )
        stateFile.write(appSyncState.prettyJson)
      }
    }

  def updateUserState(login: UserLogin, userDescriptor: UserDescriptor, states: Seq[(Sync.SyncName, Option[ast.JsVal])], delete: Boolean): Task[Unit] =
    ZIO.attemptBlocking {
      applicationStateDirectory.makeDirectories()
      val stateFile = applicationStateDirectory.file(login.value + ".json")
      if (delete) {
        stateFile.delete()
      } else {
        val appSyncState =
          StoredSyncState(
            login,
            userDescriptor,
            states,
          )
        stateFile.write(appSyncState.prettyJson)
      }
    }

  def runSyncUser(newUserOpt: Option[ResolvedUser], currentStateOpt: Option[StoredSyncState]): Task[Unit] = {

    val userLogin = newUserOpt.map(_.descriptor.login).getOrElse(UserLogin(currentStateOpt.get.name))
    val descriptor = newUserOpt.map(_.descriptor).getOrElse(currentStateOpt.get.descriptor.asInstanceOf[UserDescriptor])

    val runSyncEffect: ZIO[Any, Throwable, Unit] =
      for {
        newStates <-
          userSyncs
            .map { userSync =>
              val currentSyncState: Option[JsVal] =
                currentStateOpt
                  .flatMap(_.syncState(userSync.name))
              userSync
                .run(currentSyncState, newUserOpt)
                .map(userSync.name -> _)
            }
            .sequence
        _ <- updateUserState(userLogin, descriptor, newStates, newUserOpt.isEmpty)
      } yield ()

    runSyncEffect.correlateWith0(z"${userLogin}")
  }


  def runSyncApplication(appName: ApplicationName, currentState: Option[StoredSyncState], newApp: Option[ResolvedApp]): Task[Unit] = {

    val currentApplication = currentState.map(_.descriptor.unsafeAs[ApplicationDescriptor])
    val newApplication = newApp.map(_.application)

    val application = newApplication.getOrElse(currentApplication.get)

    val runSyncEffect: ZIO[Any, Throwable, Unit] =
      for {
        _ <- resolvedServer.runAppCommand("stop", currentApplication)
        newStates <-
          appSyncs
            .map { sync =>
              val currentSyncState: Option[JsVal] =
                currentState
                  .flatMap(_.syncState(sync.name))
              sync
                .run(currentSyncState, newApp)
                .map(sync.name -> _)
            }
            .sequence
        _ <- resolvedServer.runAppCommand("start", newApplication)
        _ <- updateAppState(appName, application, newStates, newApp.isEmpty)
      } yield ()

    runSyncEffect.correlateWith0(z"${appName}")
  }

}