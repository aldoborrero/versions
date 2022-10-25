package io.accur8.neodeploy


import a8.shared.CompanionGen
import a8.shared.FileSystem.Directory
import io.accur8.neodeploy.SyncServer.Config
import io.accur8.neodeploy.MxMain._
import io.accur8.neodeploy.model.{ApplicationDescriptor, AppsRootDirectory, CaddyDirectory, DomainName, GitServerDirectory, SupervisorDirectory}
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
      .toVector
      .flatMap { d =>
        try {
          val jsonStr = d.file("application.json").readAsString()
          json.unsafeRead[ApplicationDescriptor](jsonStr).some
        } catch {
          case e: Throwable =>
            logger.error(s"error reading directory ${d.canonicalPath}", e)
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

  lazy val syncs =
    Vector(
      CaddySync(config.caddyDirectory),
      SupervisorSync(config.supervisorDirectory),
      ApplicationInstallSync(config.appsRootDirectory),
    )

  def run: Task[Unit] =
    deploys
      .map(deploy =>
        runDeploy(deploy).correlateWith(deploy.applicationName.value)
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

  def execCommand(command: Seq[String]): Task[Unit] = {
    ZIO
      .attemptBlocking(
        Exec(command).execCaptureOutput(false)
      )
      .logVoid
  }

  def runDeploy(deployState: DeployState): Task[Unit] = {
    for {
      _ <- execCommand(deployState.stopCommand)
      _ <-
        syncs
          .map(_.run(deployState))
          .sequence
      _ <- updateDeployState(deployState)
      _ <- execCommand(deployState.startCommand)
    } yield ()
  }

}