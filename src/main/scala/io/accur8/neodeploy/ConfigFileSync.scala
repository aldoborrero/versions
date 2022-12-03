package io.accur8.neodeploy

import a8.shared.FileSystem.{Directory, File, file}
import a8.shared.SharedImports._
import a8.shared.app.LoggingF
import a8.shared.{CompanionGen, FileSystem, StringValue, ZString}
import io.accur8.neodeploy.ConfigFileSync.State
import io.accur8.neodeploy.MxConfigFileSync._
import io.accur8.neodeploy.Sync.Step
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

abstract class ConfigFileSync[B] extends Sync[State,B] with LoggingF {

  val perms: Option[String] = None

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


  override def resolveStepsFromModification(modification: Sync.Modification[State, B]): Vector[Sync.Step] = {

    def writeNewState(newState: State): Vector[Sync.Step] = {
      val action =
        ZIO.attemptBlocking {
          val configFile = file(newState.filename)
          configFile.parent.makeDirectories()
          configFile
            .write(newState.fileContents)
        }
      Vector(
        Step(
          phase = Sync.Phase.Apply,
          description = z"write ${newState.filename}",
          action = action
        )
      ) ++ perms.map(Sync.Step.chmod(_, FileSystem.file(newState.filename)))
    }

    modification match {
      case Sync.Delete(currentState) =>
        Vector(Step(
          phase = Sync.Phase.Apply,
          description = s"delete ${currentState.filename}",
          ZIO.attemptBlocking(
            file(currentState.filename).delete()
          )
        ))
      case Sync.Update(_, newState, _) =>
        writeNewState(newState)
      case Sync.Insert(newState, _) =>
        writeNewState(newState)
    }

  }


}