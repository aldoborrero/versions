package io.accur8.neodeploy


import a8.shared.FileSystem.Directory
import a8.shared.json.JsonCodec
import zio.{Task, ZIO}
import a8.shared.SharedImports._
import a8.shared.StringValue
import a8.shared.json.ast.{JsDoc, JsVal}
import a8.versions.Exec
import io.accur8.neodeploy.Sync.SyncName
import io.accur8.neodeploy.model.Command

object Sync {

  abstract class Modification[A: JsonCodec, B] {
    def newStateOpt: Option[A]
  }

//  case class Noop[A: JsonCodec, B](newStateOpt: Option[A]) extends Modification[A, B] {
//    override def actionRequired: Boolean = false
//  }

  case class Delete[A: JsonCodec, B](currentState: A) extends Modification[A, B] {
    def newStateOpt = None
  }
  case class Update[A: JsonCodec, B](currentState: A, newState: A, newInput: B) extends Modification[A, B] {
    def newStateOpt = Some(newState)
  }
  case class Insert[A: JsonCodec, B](newState: A, newInput: B) extends Modification[A, B] {
    def newStateOpt = Some(newState)
  }

  object SyncName extends StringValue.Companion[SyncName]
  case class SyncName(value: String) extends StringValue


  case class Phase(sequence: Int)
  object Phase {
    val Pre = Phase(10)
    val Apply = Phase(20)
    val Post = Phase(30)
  }

  object Step {

    def runCommand(
      phase: Phase,
      command: Command,
      workingDirectory: Option[Directory] = None,
      failOnNonZeroExitCode: Boolean = true
    ): Step = {
      Step(
        phase = phase,
        description = z"run command: ${command.args.mkString(" ")}",
        action =
          ZIO.attemptBlocking {
            val exec = new Exec(command.args, workingDirectory)
            exec.execCaptureOutput(failOnNonZeroExitCode)
          }
      )
    }

  }

  case class Step(phase: Phase, description: String, action: Task[Unit])
  case class ResolvedSteps(syncName: SyncName, newState: Option[JsVal], steps: Vector[Step])

  case class ContainerSteps(name: StringValue, resolvedSteps: Seq[ResolvedSteps], additionalSteps: Seq[Step]) {

    def nonEmpty: Boolean = sortedSteps.nonEmpty

    lazy val sortedSteps: Seq[Step] =
      (resolvedSteps.flatMap(_.steps) ++ additionalSteps)
        .sortBy(_.phase.sequence)

    def descriptions(indent: String) =
      sortedSteps
        .map(step => s"${indent}${step.description}")
        .mkString("\n")

  }

}



/**
  * A is the state for the specific sync
  * B is the input value to determine sync state
  */
abstract class Sync[A : JsonCodec, B] {

  import Sync._

  val name: SyncName

  def state(input: B): Task[Option[A]]

  def resolveModification(currentState: Option[A], newInput: Option[B]): Task[(Option[A], Option[Modification[A,B]])] = {
    for {
      newState <- newInput.map(state).getOrElse(zsucceed(None))
    } yield {
      val action =
        (currentState, newState) match {
          case (None, None) =>
            // this won't happen
            None
          case (Some(cs), None) =>
            Delete[A,B](cs).some
          case (Some(cs), Some(ns)) if cs == ns =>
            None
          case (Some(cs), Some(ns)) =>
            Update[A,B](cs, ns, newInput.get).some
          case (None, Some(ns)) =>
            Insert[A,B](ns, newInput.get).some
        }
      newState -> action
    }
  }

  def resolveStepsFromModification(modification: Modification[A,B]): Vector[Step]


  def resolveSteps(currentStateJsv: Option[JsVal], newInput: Option[B]): Task[ResolvedSteps] = {
    val currentStateOpt = currentStateJsv.map(_.unsafeAs[A])
    resolveModification(currentStateOpt, newInput)
      .map { case (newState, modificationOpt) =>
        val newStateJsv = newState.map(_.toJsVal)
        modificationOpt match {
          case Some(modification) =>
            ResolvedSteps(
              name,
              newStateJsv,
              resolveStepsFromModification(modification),
            )
          case None =>
            ResolvedSteps(
              name,
              newStateJsv,
              Vector.empty,
            )
        }
      }
  }

  //  def run(currentStateJs: Option[JsVal], newInput: Option[B]): Task[Option[JsVal]] = {
//    val currentState = currentStateJs.map(_.unsafeAs[A])
//    actions(currentState, newInput)
//      .flatMap { case (newState, action) =>
//        applyAction(newInput, action)
//          .as(newState.map(_.toJsVal))
//      }
//  }

}
