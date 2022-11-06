package io.accur8.neodeploy


import a8.shared.CompanionGen
import a8.shared.FileSystem.Directory
import io.accur8.neodeploy.model.{ApplicationDescriptor, ApplicationName, AppsRootDirectory, CaddyDirectory, Command, DomainName, GitServerDirectory, Install, SupervisorDirectory, UserDescriptor}
import zio.{Task, ZIO}
import a8.shared.SharedImports._
import a8.shared.app.{Logging, LoggingF}
import a8.shared.json.ast
import a8.shared.json.ast.JsObj
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

  def execCommand(command: Command): Task[Unit] = {
    ZIO
      .attemptBlocking(
        Exec(command.args).execCaptureOutput(false)
      )
      .logVoid
  }

  def runSyncApplication(appName: ApplicationName, currentState: Option[StoredSyncState], newApp: Option[ResolvedApp]): Task[Unit] = {

    val currentApplication = currentState.map(_.descriptor.unsafeAs[ApplicationDescriptor])
    val newApplication = newApp.map(_.application)

    val application = newApplication.getOrElse(currentApplication.get)

    val runSyncEffect: ZIO[Any, Throwable, Unit] =
      for {
        _ <- currentApplication.map(_.resolvedStopCommand).map(execCommand).getOrElse(zunit)
        newStates <-
          appSyncs
            .map { sync =>
              sync
                .run(currentState.map(_.states(sync.name.value)), newApp)
                .map(sync.name -> _)
            }
            .sequence
        _ <- newApplication.map(_.resolvedStartCommand).map(execCommand).getOrElse(zunit)
        _ <- updateAppState(appName, application, newStates, newApp.isEmpty)
      } yield ()

    runSyncEffect.correlateWith0(z"${appName}")
  }

}