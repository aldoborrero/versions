package io.accur8.neodeploy


import a8.shared.FileSystem.{Directory, File}
import a8.shared.SharedImports._
import a8.shared.json.JsonCodec
import io.accur8.neodeploy.Sync.Action
import io.accur8.neodeploy.model.ApplicationDescriptor
import zio.{Task, ZIO}


object Sync {

  abstract class Action[A : JsonCodec] {
    val deployState: DeployState
    def actionRequired: Boolean = true
  }

  case class Noop[A : JsonCodec](deployState: DeployState) extends Action[A] {
    override def actionRequired: Boolean = false
  }
  case class Delete[A : JsonCodec](deployState: DeployState, currentState: A) extends Action[A]
  case class Update[A : JsonCodec](deployState: DeployState, currentState: A, newState: A) extends Action[A]
  case class Insert[A : JsonCodec](deployState: DeployState, newState: A) extends Action[A]

}


abstract class Sync[A : JsonCodec] {

  def state(applicationDescriptor: ApplicationDescriptor): Task[Option[A]]

  def currentState(deployState: DeployState): Task[Option[A]] =
    deployState
      .currentApplicationDescriptor
      .map(state)
      .getOrElse(zsucceed(None))

  def newState(deployState: DeployState): Task[Option[A]] =
    deployState
      .newApplicationDescriptor
      .map(state)
      .getOrElse(zsucceed(None))

  def actions(deployState: DeployState): Task[Action[A]] = {
    for {
      currentState <- currentState(deployState)
      newState <- newState(deployState)
    } yield
      (currentState, newState) match {
        case (None, None) =>
          // this won't happen
          Sync.Noop(deployState)
        case (Some(cs), None) =>
          Sync.Delete(deployState, cs)
        case (Some(cs), Some(ns)) if cs == ns =>
          Sync.Noop(deployState)
        case (Some(cs), Some(ns)) =>
          Sync.Update(deployState, cs, ns)
        case (None, Some(ns)) =>
          Sync.Insert(deployState, ns)
      }
  }

  def applyAction(action: Action[A]): Task[Unit]

  def run(deployState: DeployState): Task[Unit] =
    actions(deployState)
      .flatMap(applyAction)

}
