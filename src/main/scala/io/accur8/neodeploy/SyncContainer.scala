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
import io.accur8.neodeploy.SyncContainer.{Prefix, loadState}
import io.accur8.neodeploy.model.ApplicationName
import io.accur8.neodeploy.systemstate.SystemState
import io.accur8.neodeploy.systemstate.SystemStateModel._
import zio.{Task, UIO, ZIO}

object SyncContainer extends Logging {

  def loadState(directory: Directory, prefix: Prefix): Vector[PreviousState] = {
    directory
      .files()
      .filter(f => f.name.startsWith(prefix.value) && f.name.endsWith(".json") )
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

  case class Prefix(value: String)

}

abstract class SyncContainer[Resolved, Name <: StringValue](
  prefix: Prefix,
  stateDirectory: Directory,
  filter: Filter[Name],
)
  extends LoggingF
{

  val newResolveds: Vector[Resolved]
  val syncs: Seq[Sync[Resolved]]

  def name(resolved: Resolved): Name
  def nameFromStr(nameStr: String): Name

  case class NamePair(syncName: SyncName, resolvedName: Name)

  lazy val previousSates: Vector[PreviousState] = SyncContainer.loadState(stateDirectory, prefix)

  lazy val previousStatesByNamePair: Map[NamePair, PreviousState] =
    previousSates
      .map(s => NamePair(s.syncName, nameFromStr(s.resolvedName)) -> s)
      .toMap

  lazy val allNamePairs: Vector[NamePair] = {

    val currentNamePairs: Vector[NamePair] =
      newResolveds.flatMap(resolved =>
        syncs.map(sync =>
          NamePair(sync.name, name(resolved))
        )
      )

    (previousStatesByNamePair.keySet.toVector ++ currentNamePairs)
      .distinct

  }

  def run: ZIO[Environ, Nothing, Either[Throwable,Unit]] =
    allNamePairs
      .map { pair =>
        val previousState: PreviousState =
          previousStatesByNamePair
            .get(pair)
            .getOrElse(PreviousState(pair.resolvedName.value, pair.syncName, SystemState.Empty))
        run(pair, previousState)
      }
      .sequence
      .as(())
      .either

  def run(namePair: NamePair, previousState: PreviousState): M[Unit] =
    ???



//  lazy val currentApplicationStates: Vector[StoredSyncState] =
//    loadState(stateDirectory, prefix)
//
//  val newResolveds: Iterable[Resolved]
//
//  def descriptorFromResolved(resolved: Resolved): Descriptor
//  def nameFromStr(value: String): Name
//  def name(descriptor: Descriptor): Name
//
//  def nameFromResolved(resolved: Resolved): Name =
//    name(descriptorFromResolved(resolved))
//
//  def descriptorToJson(descriptor: Descriptor): JsDoc =
//    descriptor.toJsDoc
//
//  def jsonToDescriptor(jsd: JsDoc): Descriptor =
//    jsd.unsafeAs[Descriptor]
//
//  def nameToString(name: Name): String = name.value
//
//  lazy val currentApplicationStatesByName: Map[Name, StoredSyncState] =
//    currentApplicationStates
//      .map(d => nameFromStr(d.name) -> d)
//      .toMap
//
//  lazy val newResolvedsByName: Map[Name, Resolved] =
//    newResolveds
//      .map(r => nameFromResolved(r) -> r)
//      .toMap
//
//  lazy val allNames: Vector[Name] =
//    (currentApplicationStatesByName.keySet ++ newResolvedsByName.keySet)
//      .toVector
//      .filter(filter.matches)
//      .distinct
//
//  val syncs: Seq[Sync[Resolved]]
//
//  def run: UIO[Vector[(Name,Either[Throwable,Unit])]] =
//    ZIO.collectAll(
//      allNames
//        .map(name =>
//          runSyncs(
//            name,
//            newResolvedsByName.get(name),
//            currentApplicationStatesByName.get(name),
//          )
//        )
//    )
//
//
//  def updateState(name: Name, descriptor: Descriptor, containerSteps: ContainerSteps, delete: Boolean): Task[Unit] =
//    ZIO.attemptBlocking {
//      stateDirectory.makeDirectories()
//      val stateFile = stateDirectory.file(prefix.value + "-" + name.value + ".json")
//      if (delete) {
//        stateFile.delete()
//      } else {
//        val appSyncState =
//          StoredSyncState.fromResolvedSteps(
//            name = name,
//            descriptor = descriptorToJson(descriptor),
//            containerSteps = containerSteps,
//          )
//        stateFile.write(appSyncState.prettyJson)
//      }
//    }
//
//  def runSyncs(name: Name, newResolvedOpt: Option[Resolved], currentStateOpt: Option[StoredSyncState]): UIO[(Name,Either[Throwable,Unit])] = {
//
//    val currentDescriptor = currentStateOpt.map(cs => jsonToDescriptor(cs.descriptor))
//    val nameStr = nameToString(name)
//
//    val descriptor = newResolvedOpt.map(descriptorFromResolved).getOrElse(currentDescriptor.get)
//
//    val runSyncEffect: Task[Unit] =
//      for {
//        containerSteps <-
//          syncs
//            .map { sync =>
//              val currentSyncState: Option[JsVal] =
//                currentStateOpt
//                  .flatMap(_.syncState(sync.name))
//              newResolvedOpt
//                .map(sync.systemState)
//                .getOrElse(zsucceed(SystemState.Empty))
//            }
//            .sequencePar
//            .map(rs => containerSteps(name, newResolvedOpt, currentDescriptor, rs))
//        _ <- runSteps(containerSteps)
//        _ <- updateState(name, descriptor, containerSteps, newResolvedOpt.isEmpty)
//      } yield ()
//
//    runSyncEffect
//      .correlateWith(s"${prefix.value} - ${nameStr}")
//      .either
//      .map(name -> _)
//
//  }
//
//  def runSteps(containerSteps: ContainerSteps): Task[Unit] = {
//    if ( containerSteps.nonEmpty ) (
//      loggerF.debug(s"running the following steps for ${containerSteps.name}\n${containerSteps.descriptions("        ")}\n            ")
//        *>
//          containerSteps
//            .sortedSteps
//            .map(_.action)
//            .sequence
//            .as(())
//    ) else {
//      zunit
//    }
//  }
//
//  def containerSteps(name: Name, newResolvedOpt: Option[Resolved], currentStateOpt: Option[Descriptor], resolvedSteps: Seq[ResolvedSteps]): ContainerSteps = {
//    val initialContainerSteps = ContainerSteps(name, resolvedSteps, Seq.empty)
//    initialContainerSteps.copy(additionalSteps = additionalSteps(name, newResolvedOpt, currentStateOpt, initialContainerSteps))
//  }
//
//  def additionalSteps(name: Name, newResolvedOpt: Option[Resolved], currentStateOpt: Option[Descriptor], containerSteps: ContainerSteps): Seq[Step] =
//    Seq.empty

}
