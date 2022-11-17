package io.accur8.neodeploy.dsl


import a8.shared.SharedImports._
import a8.shared.FileSystem
import a8.shared.app.LoggingF
import io.accur8.neodeploy.dsl.Step.impl
import zio.{Task, Trace, UIO, ZIO, ZLayer}
import io.accur8.neodeploy.PredefAssist._
import io.accur8.neodeploy.dsl

object Step extends LoggingF {

  import impl._

  object StepLogger {
    val simpleLayer =
      ZLayer.succeed(
        new dsl.Step.Environ {
          override def warn(message: String)(implicit trace: Trace): Task[Unit] =
            ZIO.attemptBlocking(
              wvlet.log.Logger(trace.toString).warn(message)
            )
        }
      )
  }

  trait StepLogger {
    def warn(message: String)(implicit trace: Trace): zio.Task[Unit]
  }

  type Environ = StepLogger

  type M[A] = zio.ZIO[Environ, Throwable, A]

  val empty: Step = EmptyStep

  object impl {

    case object EmptyStep extends Step

    case class FileStep(
      fileEffect: M[FileSystem.File],
      contentEffect: M[String],
    ) extends Step

    case class SequentialSteps(
      steps: Vector[Step],
    ) extends Step

    case class ParallelSteps(
      steps: Vector[Step],
    ) extends Step

    case class EffectStep(
      description: String,
      effect: M[Unit],
    ) extends Step

    case class ActionBeforeStep(
      description: String,
      step: Step,
      effect: M[Unit],
    ) extends Step

    case class ActionAfterStep(
      description: String,
      step: Step,
      effect: M[Unit],
    ) extends Step

    case class RawStep(
      description: String,
      actionRequired: M[Boolean],
      actionEffect: M[Unit],
    ) extends Step

    case class SpanStep(
      description: String,
      step: Step,
    ) extends Step

  }

  def fileStep(
    fileEffect: M[FileSystem.File],
    contentEffect: M[String],
  ): Step =
    FileStep(fileEffect, contentEffect)

  def fileStep(
    file: =>FileSystem.File,
    content: =>String,
  ): Step =
    FileStep(
      ZIO.attemptBlocking(file),
      ZIO.attemptBlocking(content),
    )

  def rawEffect(
    description: String,
    actionRequired: M[Boolean],
    actionEffect: M[Unit],
  ): Step =
    RawStep(description, actionRequired, actionEffect)

  def fromEffect(
    description: String,
    effect: M[Unit],
  ): Step =
    EffectStep(description, effect)

  def fromEffect[A](
    description: String,
    effect: =>A,
  ): Step =
    EffectStep(description, ZIO.attemptBlocking(effect))

  def actionRequired(step: Step): M[Boolean] =
    step match {
      case EmptyStep =>
        zsucceed(false)
      case RawStep(_, actionRequired, _) =>
        actionRequired
      case FileStep(fileEffect, contentEffect) =>
        for {
          file <- fileEffect
          result <-
            ZIO.attemptBlocking(
              file.readAsStringOpt() match {
                case None =>
                  zsucceed(true)
                case Some(existingContent) =>
                  contentEffect
                    .map(newContent => existingContent != newContent)
              }
            )
              .flatten
        } yield result
      case SpanStep(_, step) =>
        actionRequired(step)
      case EffectStep(_, _) =>
        zsucceed(true)
      case ActionBeforeStep(_, step, _) =>
        actionRequired(step)
      case ActionAfterStep(_, step, _) =>
        actionRequired(step)
      case ParallelSteps(steps) =>
        ZIO.foreachPar(steps)(actionRequired).map(_.exists(identity))
      case SequentialSteps(steps) =>
        ZIO.foreachPar(steps)(actionRequired).map(_.exists(identity))
    }


  def performAction(step: Step): M[Unit] = {
    val performActionEffect =
      step match {
        case EmptyStep =>
          zsucceed(())
        case SpanStep(_, step) =>
          performAction(step)
        case RawStep(_, _, actionEffect) =>
          actionEffect
        case FileStep(fileEffect, contentEffect) =>
          for {
            file <- fileEffect
            content <- contentEffect
            _ <-
              ZIO.attemptBlocking {
                file.parent.resolve
                file.write(content)
              }
          } yield ()
        case EffectStep(_, effect) =>
          effect
        case ActionAfterStep(description, step, afterEffect) =>
          for {
            _ <- performAction(step)
            _ <- afterEffect
          } yield ()
        case ActionBeforeStep(description, step, beforeEffect) =>
          for {
            _ <- beforeEffect
            _ <- performAction(step)
          } yield ()
        case ParallelSteps(steps) =>
          ZIO.foreachPar(steps)(performAction)
            .as(())
        case SequentialSteps(steps) =>
          ZIO.foreach(steps)(performAction)
            .as(())
      }
    for {
      actionRequired <- actionRequired(step)
      _ <-
        if (actionRequired) {
          for {
            logMessages <- dryRun(step, checkIfActionIsRequired = false, recurse = false)
            _ <-
              ZIO.collectAll(
                logMessages
                  .map(m => loggerF.debug(s"running -- ${m}"))
              )
            _ <- performActionEffect
          } yield ()
        } else
          zunit
    } yield ()
  }

  def dryRun(step: Step, checkIfActionIsRequired: Boolean, recurse: Boolean, indent: String = "   "): M[Vector[String]] = {

    def impl(s0: Step, newIndent: String = ""): M[Vector[String]] = {
      if ( recurse ) {
        dryRun(s0, checkIfActionIsRequired, true, newIndent)
      } else
        zsucceed(Vector.empty)
    }

    val dryRunEffect: M[Vector[String]] =
      step match {
        case EmptyStep =>
          zsucceed(Vector.empty)
        case SpanStep(description, step) =>
          impl(step, "    ")
            .map(description +: _)
        case RawStep(description, _, _) =>
          zsucceed(Vector(description))
        case FileStep(fileEffect, contentEffect) =>
          for {
            file <- fileEffect
            action <-
              ZIO.attemptBlocking(
                if ( file.exists() )
                  "update"
                else
                  "write"
              )
          } yield Vector(z"${action} file ${file}")
        case EffectStep(description, _) =>
          zsucceed(Vector(description))
        case ActionAfterStep(description, step, _) =>
          impl(step)
            .map(_ :+ description)
        case ActionBeforeStep(description, step, _) =>
          impl(step)
            .map(description +: _)
        case ParallelSteps(steps) =>
          ZIO.foreach(steps)(s => impl(s))
            .map(_.flatten)
        case SequentialSteps(steps) =>
          ZIO.foreach(steps)(s => impl(s))
            .map(_.flatten)
      }

    val resultEffect =
      if ( checkIfActionIsRequired ) {
        for {
          actionRequired <- actionRequired(step)
          result <-
            if (actionRequired) dryRunEffect
            else zsucceed(Vector.empty)
        } yield result
      } else {
        dryRunEffect
      }

    // add the indent
    resultEffect
      .map(_.map(indent + _))

  }


}



sealed trait Step {

  import Step._
  import impl._

  def >>(step: Step): Step =
    andThen(step)

  /**
    * run this step and when it completes run the next step
    */
  def andThen(step: Step): Step =
    SequentialSteps(
      steps = Vector(this, step),
    )

  /**
    * run this step and when it completes run the next step
    */
  def andThen(description: String, effect: M[Unit]): Step =
    EffectStep(description, effect)

  /**
    * run this step in parallel with the supplied step
    */
  def par(step: Step): Step =
    ParallelSteps(
      steps = Vector(this, step),
    )

  /**
    * Run the effect if the wrapped step actually does anything
    */
  def onActionRunAfter(description: String, effect: M[Unit]): Step =
    ActionAfterStep(
      description = description,
      step = this,
      effect = effect,
    )

  /**
    * Run the effect if the wrapped step actually does anything
    */
  def onActionRunBefore(description: String, effect: M[Unit]): Step =
    ActionBeforeStep(
      description = description,
      step = this,
      effect = effect,
    )

  def span(description: String): Step =
    SpanStep(description, this)

}
