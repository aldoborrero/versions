package io.accur8.neodeploy

import a8.shared.FileSystem.{Directory, File, file}
import a8.shared.SharedImports._
import a8.shared.ZString
import io.accur8.neodeploy.model.DirectoryValue
import zio.{Task, ZIO}


abstract class ConfigFileSync(configDirectory: DirectoryValue) extends Sync[String] {

  def filename(deployState: DeployState): ZString

  def configFile(deployState: DeployState): File =
    configDirectory.resolvedDirectory.file(filename(deployState).toString())

  override def currentState(initializer: DeployState): Task[Option[String]] =
    zsucceed(configFile(initializer).readAsStringOpt())

  override def applyAction(action: Sync.Action[String]): Task[Unit] = {

    def writeNewState(state: String): Task[Unit] =
      ZIO.attemptBlocking(
        configFile(action.deployState)
          .write(state)
      )

    action match {
      case Sync.Noop(_) =>
        zunit
      case Sync.Delete(deployState, _) =>
        ZIO.attemptBlocking(
          configFile(deployState).delete()
        )
      case Sync.Update(deployState, _, newState) =>
        writeNewState(newState)
      case Sync.Insert(deployState, newState) =>
        writeNewState(newState)
    }

  }


}