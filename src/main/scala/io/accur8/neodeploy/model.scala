package io.accur8.neodeploy


import a8.shared.FileSystem.{Directory, dir}
import a8.shared.{CascadingHocon, CompanionGen, ConfigMojo, LongValue, StringValue}
import io.accur8.neodeploy.Mxmodel._
import a8.shared.SharedImports._
import a8.shared.app.Logging
import a8.shared.json.ast.{JsArr, JsDoc, JsNothing, JsObj, JsStr, JsVal}
import a8.shared.json.{JsonCodec, JsonTypedCodec, UnionCodecBuilder}
import a8.versions.RepositoryOps.RepoConfigPrefix
import com.softwaremill.sttp.Uri
import io.accur8.neodeploy.Sync.SyncName
import io.accur8.neodeploy.model.PersonnelId

import scala.collection.Iterable

object model extends Logging {

  object ListenPort extends LongValue.Companion[ListenPort]
  case class ListenPort(value: Long) extends LongValue

  object Version extends StringValue.Companion[Version]
  case class Version(value: String) extends StringValue

  object JavaVersion extends LongValue.Companion[JavaVersion]
  case class JavaVersion(value: Long) extends LongValue

  object ApplicationName extends StringValue.Companion[ApplicationName]
  case class ApplicationName(value: String) extends StringValue

  object DomainName extends StringValue.Companion[DomainName]
  case class DomainName(value: String) extends StringValue

  object Organization extends StringValue.Companion[Organization]
  case class Organization(value: String) extends StringValue

  object Artifact extends StringValue.Companion[Artifact]
  case class Artifact(value: String) extends StringValue

  object Command {
    implicit val jsonCodec =
      JsonTypedCodec.JsArr.dimap[Command](
        arr => Command(arr.values.collect{ case JsStr(s) => s }),
        cmd => JsArr(cmd.args.map(JsStr.apply).toList)
      )
  }
  case class Command(args: Iterable[String])

  abstract class DirectoryValue extends StringValue {
    lazy val resolvedDirectory: Directory = {
      val d = unresolvedDirectory
      if ( !d.exists() ) {
        d.makeDirectories()
      }
      d
    }
    lazy val unresolvedDirectory: Directory = dir(value)
  }

  object SupervisorDirectory extends StringValue.Companion[SupervisorDirectory]
  case class SupervisorDirectory(value: String) extends DirectoryValue

  object CaddyDirectory extends StringValue.Companion[CaddyDirectory]
  case class CaddyDirectory(value: String) extends DirectoryValue

  object AppsRootDirectory extends StringValue.Companion[AppsRootDirectory]
  case class AppsRootDirectory(value: String) extends DirectoryValue

  object GitServerDirectory extends StringValue.Companion[GitServerDirectory]
  case class GitServerDirectory(value: String) extends DirectoryValue

  object GitRootDirectory extends StringValue.Companion[GitRootDirectory]
  case class GitRootDirectory(value: String) extends DirectoryValue

  sealed trait Install
  object Install {

    implicit val jsonCodec =
      UnionCodecBuilder[Install]
        .typeFieldName("kind")
        .addSingleton("manual", Manual)
        .defaultType[FromRepo]
        .addType[FromRepo]("repoe")
        .build


    object FromRepo extends MxFromRepo
    @CompanionGen
    case class FromRepo(
      organization: Organization,
      artifact: Artifact,
      version: Version,
      webappExplode: Boolean = true,
    ) extends Install

    case object Manual extends Install

  }

  object ApplicationDescriptor extends MxApplicationDescriptor {
    def supervisorCommand(action: String, applicationName: ApplicationName): Command =
      Command(Seq(
        "supervisorctl",
        action,
        applicationName.value
      ))
  }
  @CompanionGen
  case class ApplicationDescriptor(
    name: ApplicationName,
    install: Install,
    jvmArgs: Iterable[String] = None,
    autoStart: Option[Boolean] = None,
    appArgs: Iterable[String] = Iterable.empty,
    mainClass: String,
    user: String = "dev",
    listenPort: Option[ListenPort] = None,
    javaVersion: JavaVersion = JavaVersion(11),
    stopServerCommand: Option[Command] = None,
    startServerCommand: Option[Command] = None,
    domainName: Option[DomainName],
    trigger: JsDoc = JsDoc.empty,
    repository: Option[RepoConfigPrefix] = None,
  ) {

    lazy val resolvedStopCommand: Command =
      stopServerCommand
        .getOrElse(ApplicationDescriptor.supervisorCommand("stop", name))

    lazy val resolvedStartCommand: Command =
      startServerCommand
        .getOrElse(ApplicationDescriptor.supervisorCommand("start", name))

  }

  object StoredSyncState extends MxStoredSyncState {
    def apply(appName: ApplicationName, applicationDescriptor: ApplicationDescriptor, states: Seq[(SyncName,Option[JsVal])]): StoredSyncState = {
      val statesJsoValues =
        states
          .flatMap {
            case (syncName, Some(state)) =>
              Some(syncName.value -> state)
            case _ =>
              None
          }
          .toMap

      new StoredSyncState(
        appName.value,
        applicationDescriptor.toJsDoc,
        JsObj(statesJsoValues).toJsDoc,
      )
    }
  }
  @CompanionGen
  case class StoredSyncState(
    name: String,
    descriptor: JsDoc,
    states: JsDoc,
  ) {
    def syncState(name: String): Option[JsVal] =
      states.actualJsVal match {
        case jso: JsObj =>
          jso.values.get(name)
        case _ =>
          None
      }
  }

  object UserDescriptor extends MxUserDescriptor
  @CompanionGen
  case class UserDescriptor(
    login: String,
    home: Option[String] = None,
    authorizedKeys: Iterable[AuthorizedKey] = Iterable.empty,
    authorizedPersonnel: Iterable[PersonnelId] = Iterable.empty,
    appsDirectory: AppsRootDirectory,
  )

  case class ResolvedUser(
    descriptor: UserDescriptor,
    home: Directory,
    appsDirectory: Directory,
    server: ResolvedServer,
  ) {

    def personnel =
      descriptor
        .authorizedPersonnel
        .flatMap( personnelId =>
          server
            .repository
            .personnel(personnelId)
        )

    def authorizedKeys =
      descriptor
        .authorizedKeys
  }



  object ServerName extends StringValue.Companion[ServerName]
  case class ServerName(value: String) extends StringValue

  object RSnapshotDescriptor extends MxRSnapshotDescriptor
  @CompanionGen
  case class RSnapshotDescriptor(
    directories: Vector[String],
    runAt: String,
  )


  object ServerDescriptor extends MxServerDescriptor
  @CompanionGen
  case class ServerDescriptor(
    name: ServerName,
    appsDirectory: AppsRootDirectory,
    supervisorDirectory: SupervisorDirectory,
    caddyDirectory: CaddyDirectory,
    serverName: DomainName,
    users: Iterable[UserDescriptor],
    applications: Iterable[ApplicationDescriptor],
    rsnapshot: Option[RSnapshotDescriptor] = None,
  )

  case class ResolvedServer(
    descriptor: ServerDescriptor,
    gitServerDirectory: GitServerDirectory,
    repository: ResolvedRepository,
  ) {
    def name = descriptor.name
    lazy val resolvedApps = {
      descriptor
        .applications
        .map(a => ResolvedApp(a, this, gitServerDirectory.unresolvedDirectory.subdir(a.name.value)))
    }
    def appsRootDirectory: AppsRootDirectory = descriptor.appsDirectory
    def supervisorDirectory: SupervisorDirectory = descriptor.supervisorDirectory
    def caddyDirectory: CaddyDirectory = descriptor.caddyDirectory

  }

  case class ResolvedApp(
    application: ApplicationDescriptor,
    server: ResolvedServer,
    gitDirectory: Directory,
  ) {
  }

  object AuthorizedKey extends StringValue.Companion[AuthorizedKey]
  case class AuthorizedKey(value: String) extends StringValue

  object RepositoryDescriptor extends MxRepositoryDescriptor
  @CompanionGen
  case class RepositoryDescriptor(
    rsnapshotKey: Option[AuthorizedKey] = None,
    personnel: Iterable[Personnel] = Iterable.empty,
    servers: Iterable[ServerDescriptor] = Iterable.empty,
  )

  object PersonnelId extends StringValue.Companion[PersonnelId]
  case class PersonnelId(value: String) extends StringValue

  object Personnel extends MxPersonnel
  @CompanionGen
  case class Personnel(
    id: PersonnelId,
    description: String,
    authorizedKeysUrl: Option[String],
    authorizedKeys: Iterable[AuthorizedKey],
  ) {

    def resolvedKeys: Vector[AuthorizedKey] =
      CodeBits.downloadKeys(authorizedKeysUrl)


  }

  object ResolvedRepository {

    def loadFromDisk(gitRootDirectory: GitRootDirectory): ResolvedRepository = {
      val ch = CascadingHocon.loadConfigsInDirectory(gitRootDirectory.unresolvedDirectory.asNioPath, recurse = false)
      ch.resolve
      val file = gitRootDirectory.unresolvedDirectory.file("repository.hocon")
      val jsonStr = file.readAsString()
      val desc = json.unsafeRead[RepositoryDescriptor](jsonStr)
      ResolvedRepository(
        desc,
        gitRootDirectory,
      )
    }

  }

  case class ResolvedRepository(
    descriptor: RepositoryDescriptor,
    gitRootDirectory: GitRootDirectory,
  ) {

    def personnel(id: PersonnelId): Option[Personnel] = {
      val result =
        descriptor
          .personnel
          .find(_.id === id)
      if ( result.isEmpty ) {
        logger.warn(s"Personnel not found: $id")
      }
      result
    }

    lazy val servers =
      descriptor
        .servers
        .map { serverDescriptor =>
          ResolvedServer(
            serverDescriptor,
            GitServerDirectory(gitRootDirectory.unresolvedDirectory.subdir(serverDescriptor.name.value).asNioPath.toString),
            this,
          )
        }
  }

}
