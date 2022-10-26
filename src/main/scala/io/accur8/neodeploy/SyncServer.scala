package io.accur8.neodeploy


import a8.shared.CompanionGen
import a8.shared.FileSystem.Directory
import io.accur8.neodeploy.SyncServer.Config
import io.accur8.neodeploy.MxMain._
import io.accur8.neodeploy.model.{ApplicationDescriptor, AppsRootDirectory, CaddyDirectory, Command, DomainName, GitServerDirectory, Install, SupervisorDirectory}
import zio.{Task, ZIO}
import a8.shared.SharedImports._
import a8.shared.app.LoggingF
import a8.versions.Exec

object SyncServer {

  object Config extends MxConfig
  @CompanionGen
  case class Config(
    supervisorDirectory: SupervisorDirectory,
    caddyDirectory: CaddyDirectory,
    appsRootDirectory: AppsRootDirectory,
    gitServerDirectory: GitServerDirectory,
    serverName: DomainName,
  )

}


case class SyncServer(config: Config) extends LoggingF {

  lazy val stateDirectory: Directory =
    config
      .appsRootDirectory
      .unresolvedDirectory
      .subdir(".state")
      .resolve

  lazy val currentApplicationDescriptors: Vector[ApplicationDescriptor] = {
    stateDirectory
      .files()
      .toVector
      .flatMap { f =>
        try {
          val jsonStr = f.readAsString()
          json.unsafeRead[ApplicationDescriptor](jsonStr).some
        } catch {
          case e: Throwable =>
            logger.error(s"error reading file ${f.canonicalPath}", e)
            None
        }
      }
  }

  lazy val newApplicationDescriptors: Iterable[ApplicationDescriptor] =
    config
      .gitServerDirectory
      .unresolvedDirectory
      .subdirs()
      .filter(_.name != ".git")
      .toVector
      .flatMap { d =>
        val f = d.file("application.json")
        try {
          val jsonStr = f.readAsString()
          json.unsafeRead[ApplicationDescriptor](jsonStr).some
        } catch {
          case e: Throwable =>
            logger.error(s"error reading ${f}", e)
            None
        }
      }

  lazy val currentApplicationDescriptorsByName =
    currentApplicationDescriptors
      .map(d => d.name -> d)
      .toMap

  lazy val newApplicationDescriptorsByName =
    newApplicationDescriptors
      .map(d => d.name -> d)
      .toMap

  lazy val allNames =
    (currentApplicationDescriptorsByName.keySet ++ newApplicationDescriptorsByName.keySet)
      .toVector
      .distinct

  lazy val deploys =
    allNames
      .map { applicationName =>
        DeployState(
          applicationName,
          config.gitServerDirectory.resolvedDirectory.subdir(applicationName.value),
          currentApplicationDescriptorsByName.get(applicationName),
          newApplicationDescriptorsByName.get(applicationName),
        )
      }

  lazy val syncs: Seq[Sync[_]] =
    Vector(
      CaddySync(config.caddyDirectory),
      SupervisorSync(config.supervisorDirectory),
      ApplicationInstallSync(config.appsRootDirectory),
    )

  def run: Task[Unit] =
    deploys
      .map(deploy =>
        if ( deploy.needsSync ) {
          runSyncApplication(deploy)
        } else {
          loggerF.info(z"no changes for ${deploy.applicationName}")
        }
      )
      .sequencePar
      .logVoid

  def updateDeployState(deployState: DeployState): Task[Unit] =
    ZIO.attemptBlocking {
      stateDirectory.makeDirectories()
      val stateFile = stateDirectory.file(deployState.applicationName.value + ".json")
      deployState.newApplicationDescriptor match {
        case Some(newApplicationDescriptor) =>
          stateFile.write(newApplicationDescriptor.prettyJson)
        case None =>
          stateFile.delete()
      }
    }

  def execCommand(command: Command): Task[Unit] = {
    ZIO
      .attemptBlocking(
        Exec(command.args).execCaptureOutput(false)
      )
      .logVoid
  }

  def runSyncApplication(deployState: DeployState): Task[Unit] = {

    val runSyncEffect =
      for {
        _ <- deployState.stopCommand.map(execCommand).getOrElse(zunit)
        _ <-
          syncs
            .map(_.run(deployState))
            .sequence
        _ <- deployState.startCommand.map(execCommand).getOrElse(zunit)
      } yield ()

    val effect =
      for {
        dryRunActions <-
          syncs
            .map(_.actions(deployState))
            .sequencePar
            .map(_.filter(_.actionRequired))
        _ <-
          if ( dryRunActions.nonEmpty ) {
            runSyncEffect
          } else {
            loggerF.info("no sync actions required")
          }
        _ <- updateDeployState(deployState)
      } yield ()

    effect.correlateWith0(z"${deployState.applicationName}")
  }

}