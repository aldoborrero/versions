package io.accur8.neodeploy


import a8.shared.FileSystem.Directory
import a8.shared.json.JsonCodec
import a8.shared.SharedImports._
import a8.shared.StringValue
import a8.shared.ZString.ZStringer
import a8.shared.app.{Logging, LoggingF}
import a8.shared.jdbcf.ISeriesDialect.logger
import a8.shared.json.ast.{JsDoc, JsVal}
import io.accur8.neodeploy.PushRemoteSyncSubCommand.Filter
import io.accur8.neodeploy.Sync.SyncName
import io.accur8.neodeploy.SyncContainer.Prefix
import io.accur8.neodeploy.model.ApplicationName
import io.accur8.neodeploy.systemstate.{Interpretter, SystemState}
import io.accur8.neodeploy.systemstate.SystemStateModel._
import zio.prelude.Equal
import zio.{Task, UIO, ZIO}
import PredefAssist._
import io.accur8.neodeploy.systemstate.Interpretter.{ActionNeededCache, RunArgs}

object SyncContainer extends Logging {

  case class Prefix(value: String)

}

abstract class SyncContainer[Resolved, Name <: StringValue : Equal](
  prefix: Prefix,
  stateDirectory: Directory,
  filter: Filter[Name],
)
  extends LoggingF
{

  val newResolveds: Vector[Resolved]
  val syncs: Seq[Sync[Resolved]]

  lazy val newResolvedsByName: Map[Name,Resolved] =
    newResolveds
      .map(r => name(r) -> r)
      .toMap

  def name(resolved: Resolved): Name
  def nameFromStr(nameStr: String): Name

  case class NamePair(syncName: SyncName, resolvedName: Name)

  lazy val previousStates: Vector[PreviousState] = loadState

  lazy val previousStatesByNamePair: Map[NamePair, PreviousState] =
    previousStates
      .map(s => NamePair(s.syncName, nameFromStr(s.resolvedName)) -> s)
      .toMap

  lazy val allNamePairs: Vector[NamePair] = {

    val currentNamePairs: Vector[NamePair] =
      newResolveds.flatMap(resolved =>
        syncs.map(sync =>
          NamePair(sync.name, name(resolved))
        )
      )

    val result =
      (previousStatesByNamePair.keySet.toVector ++ currentNamePairs)
        .distinct

//    logger.debug(s"allNamePairs = ${result}")

    result

  }

  def run: ZIO[Environ, Nothing, Either[Throwable,Unit]] =
    allNamePairs
      .map { pair =>
        val previousState: PreviousState =
          previousStatesByNamePair
            .get(pair)
            .getOrElse(PreviousState(ResolvedState(pair.resolvedName.value, pair.  syncName, SystemState.Empty)))
        run(pair, previousState)
      }
      .sequence
      .as(())
      .either

  def run(namePair: NamePair, previousState: PreviousState): M[Unit] = {

    val resolvedOpt = newResolveds.find(r => name(r) === namePair.resolvedName)
    val syncOpt = syncs.find(_.name === namePair.syncName)

    val newStateEffect =
      (
        (syncOpt, resolvedOpt) match {
          case (Some(sync), Some(resolved)) =>
            sync.systemState(resolved)
          case _ =>
            zsucceed(SystemState.Empty)
        }
      ).map(s => NewState(ResolvedState(namePair.resolvedName.value, namePair.syncName, s)))

    val effect: M[Unit] =
      for {
        newState <- newStateEffect
        actionNeededCache <- systemstate.Interpretter.actionNeededCache(newState)
        runArgs = RunArgs(newState, previousState, actionNeededCache)
        _ <- runArgs.dryRunLog(loggerF)
        _ <- systemstate.Interpretter.run(runArgs)
        _ <- updateState(newState)
      } yield ()

    effect
      .either
      .flatMap {
        case Right(_) =>
          zunit
        case Left(th) =>
          loggerF.error(s"error processing ${namePair}", th)
      }

  }

  def updateState(newState: NewState): Task[Unit] = {
    val isEmpty = Interpretter.isEmpty(newState.systemState)
    val stateFile = stateDirectory.file(z"${prefix.value}-${newState.resolvedName}-${newState.syncName}.json")
    if (isEmpty) {
      if ( stateFile.exists() ) {
        loggerF.debug(z"deleting state ${stateFile}") *>
          ZIO.attemptBlocking(
            stateFile.delete()
          )
      } else {
        zunit
      }
    } else {
      loggerF.debug(z"updating state ${stateFile}") *>
        ZIO.attemptBlocking(
          stateFile.write(newState.prettyJson)
        )
    }
  }

  def loadState: Vector[PreviousState] = {
    stateDirectory
      .files()
      .filter(f => f.name.startsWith(prefix.value) && f.name.endsWith(".json"))
      .toVector
      .flatMap { f =>
        try {
          val jsonStr = f.readAsString()
          json.unsafeRead[PreviousState](jsonStr).some
        } catch {
          case e: Throwable =>
            logger.error(s"error reading file ${f.canonicalPath}", e)
            None
        }
      }
  }

}
