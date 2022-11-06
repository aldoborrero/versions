package io.accur8.neodeploy

import a8.shared.FileSystem.{Directory, File, file}
import a8.shared.SharedImports._
import a8.shared.{CompanionGen, StringValue, ZString}
import io.accur8.neodeploy.ConfigFileSync.State
import io.accur8.neodeploy.MxConfigFileSync._
import io.accur8.neodeploy.model.DirectoryValue
import zio.{Task, ZIO}


object ConfigFileSync {

  object State extends MxState
  @CompanionGen
  case class State(
    filename: String,
    fileContents: String,
  )

}

abstract class ConfigFileSync[B] extends Sync[State,B] {

  def configFile(input: B): File

  def configFileContents(input: B): Task[Option[String]]

  override def state(input: B): Task[Option[State]] = {
    val file = configFile(input)
    configFileContents(input)
      .map { contentsOpt =>
        contentsOpt.map( contents =>
          State(file.asNioPath.toAbsolutePath.toString, contents)
        )
      }
  }

  override def applyAction(input: Option[B], action: Sync.Action[State]): Task[Unit] = {

    def writeNewState(newState: State): Task[Unit] =
      ZIO.attemptBlocking(
        file(newState.filename)
          .write(newState.fileContents)
      )

    action match {
      case Sync.Noop(_) =>
        zunit
      case Sync.Delete(currentState) =>
        ZIO.attemptBlocking(
          file(currentState.filename).delete()
        )
      case Sync.Update(_, newState) =>
        writeNewState(newState)
      case Sync.Insert(newState) =>
        writeNewState(newState)
    }

  }


}