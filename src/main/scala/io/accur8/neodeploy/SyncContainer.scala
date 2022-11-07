package io.accur8.neodeploy


import a8.shared.FileSystem.Directory
import a8.shared.json.JsonCodec
import io.accur8.neodeploy.resolvedmodel.StoredSyncState
import a8.shared.SharedImports._
import a8.shared.StringValue
import a8.shared.ZString.ZStringer
import a8.shared.app.{Logging, LoggingF}
import a8.shared.jdbcf.ISeriesDialect.logger
import a8.shared.json.ast.{JsDoc, JsVal}
import io.accur8.neodeploy.SyncContainer.loadState
import zio.{Task, ZIO}

object SyncContainer extends Logging {

  def loadState(directory: Directory): Vector[StoredSyncState] = {
    directory
      .files()
      .toVector
      .flatMap { f =>
        try {
          val jsonStr = f.readAsString()
          json.unsafeRead[StoredSyncState](jsonStr).some
        } catch {
          case e: Throwable =>
            logger.error(s"error reading file ${f.canonicalPath}", e)
            None
        }
      }
  }

}

abstract class SyncContainer[Resolved, Descriptor : JsonCodec, Name <: StringValue](
  context: String,
  syncServer: SyncServer,
  stateDirectory: Directory,
)
  extends LoggingF
{

  lazy val currentApplicationStates: Vector[StoredSyncState] =
    loadState(stateDirectory)

  val newResolveds: Iterable[Resolved]

  def descriptorFromResolved(resolved: Resolved): Descriptor
  def nameFromStr(value: String): Name
  def name(descriptor: Descriptor): Name

  def nameFromResolved(resolved: Resolved): Name =
    name(descriptorFromResolved(resolved))

  def descriptorToJson(descriptor: Descriptor): JsVal =
    descriptor.toJsVal

  def jsonToDescriptor(jsd: JsDoc): Descriptor =
    jsd.unsafeAs[Descriptor]

  def nameToString(name: Name): String = name.value

  lazy val currentApplicationStatesByName: Map[Name, StoredSyncState] =
    currentApplicationStates
      .map(d => nameFromStr(d.name) -> d)
      .toMap

  lazy val newResolvedsByName: Map[Name, Resolved] =
    newResolveds
      .map(r => nameFromResolved(r) -> r)
      .toMap

  lazy val allNames: Vector[Name] =
    (currentApplicationStatesByName.keySet ++ newResolvedsByName.keySet)
      .toVector
      .distinct

  val syncs: Seq[Sync[_, Resolved]]

  def run: Task[Unit] =
    allNames
      .map(name =>
        runSync(
          name,
          newResolvedsByName.get(name),
          currentApplicationStatesByName.get(name),
        )
      )
      .sequencePar
      .logVoid

  def updateState(name: Name, descriptor: Descriptor, states: Seq[(Sync.SyncName, Option[JsVal])], delete: Boolean): Task[Unit] =
    ZIO.attemptBlocking {
      stateDirectory.makeDirectories()
      val stateFile = stateDirectory.file(name.value + ".json")
      if (delete) {
        stateFile.delete()
      } else {
        val appSyncState =
          StoredSyncState(
            name,
            descriptor,
            states,
          )
        stateFile.write(appSyncState.prettyJson)
      }
    }

  def runSync(name: Name, newResolvedOpt: Option[Resolved], currentStateOpt: Option[StoredSyncState]): Task[Unit] = {

    val currentDescriptor = currentStateOpt.map(cs => jsonToDescriptor(cs.descriptor))
    val nameStr = nameToString(name)

    val descriptor = newResolvedOpt.map(descriptorFromResolved).getOrElse(currentDescriptor.get)

    val runSyncEffect: Task[Unit] =
      for {
        _ <- runBeforeSync(newResolvedOpt, currentDescriptor)
        newStates <-
          syncs
            .map { sync =>
              val currentSyncState: Option[JsVal] =
                currentStateOpt
                  .flatMap(_.syncState(sync.name))
              sync
                .run(currentSyncState, newResolvedOpt)
                .map(sync.name -> _)
            }
            .sequencePar
        _ <- runAfterSync(newResolvedOpt, currentDescriptor)
        _ <- updateState(name, descriptor, newStates, newResolvedOpt.isEmpty)
      } yield ()

    if ( context.trim.length == 0 )
      toString

    runSyncEffect.correlateWith0(s"${context} - ${nameStr}")

  }

  def runBeforeSync(newResolvedOpt: Option[Resolved], currentDescriptorOpt: Option[Descriptor]): Task[Unit] =
    zunit

  def runAfterSync(newResolvedOpt: Option[Resolved], currentDescriptorOpt: Option[Descriptor]): Task[Unit] =
    zunit

}
