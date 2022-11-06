package io.accur8.neodeploy


import a8.shared.json.JsonCodec
import zio.Task
import a8.shared.SharedImports._
import a8.shared.StringValue
import a8.shared.json.ast.{JsDoc, JsVal}
import io.accur8.neodeploy.Sync.SyncName

object Sync {

  abstract class Action[A: JsonCodec] {
    def actionRequired: Boolean = true
    def newStateOpt: Option[A]
  }

  case class Noop[A: JsonCodec](newStateOpt: Option[A]) extends Action[A] {
    override def actionRequired: Boolean = false
  }

  case class Delete[A: JsonCodec](currentState: A) extends Action[A] {
    def newStateOpt = None
  }
  case class Update[A: JsonCodec](currentState: A, newState: A) extends Action[A] {
    def newStateOpt = Some(newState)
  }
  case class Insert[A: JsonCodec](newState: A) extends Action[A] {
    def newStateOpt = Some(newState)
  }

  object SyncName extends StringValue.Companion[SyncName]
  case class SyncName(value: String) extends StringValue

}



/**
  * A is the state for the specific sync
  * B is the input value to determine sync state
  */
abstract class Sync[A : JsonCodec, B] {

  import Sync._

  val name: SyncName

  def state(input: B): Task[Option[A]]

  def actions(currentState: Option[A], newInput: Option[B]): Task[(Option[A], Action[A])] = {
    for {
      newState <- newInput.map(state).getOrElse(zsucceed(None))
    } yield {
      val action =
        (currentState, newState) match {
          case (None, None) =>
            // this won't happen
            Noop(newState)
          case (Some(cs), None) =>
            Delete(cs)
          case (Some(cs), Some(ns)) if cs == ns =>
            Noop(newState)
          case (Some(cs), Some(ns)) =>
            Update(cs, ns)
          case (None, Some(ns)) =>
            Insert(ns)
        }
      newState -> action
    }
  }

  def applyAction(input: Option[B], action: Action[A]): Task[Unit]

  def run(currentStateJs: Option[JsDoc], newInput: Option[B]): Task[Option[JsVal]] = {
    val currentState = currentStateJs.map(_.unsafeAs[A])
    actions(currentState, newInput)
      .flatMap { case (newState, action) =>
        applyAction(newInput, action)
          .as(newState.map(_.toJsVal))
      }
  }

}
