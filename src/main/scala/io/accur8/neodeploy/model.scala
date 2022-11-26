package io.accur8.neodeploy


import a8.shared.FileSystem.{Directory, dir}
import a8.shared.{CascadingHocon, CompanionGen, ConfigMojo, Exec, LongValue, StringValue, ZString}
import io.accur8.neodeploy.Mxmodel._
import a8.shared.SharedImports._
import a8.shared.ZString.ZStringer
import a8.shared.app.{LoggerF, Logging, LoggingF}
import a8.shared.json.ast.{JsArr, JsDoc, JsNothing, JsObj, JsStr, JsVal}
import a8.shared.json.{JsonCodec, JsonTypedCodec, UnionCodecBuilder}
import a8.versions.RepositoryOps.RepoConfigPrefix
import com.softwaremill.sttp.Uri
import io.accur8.neodeploy.Sync.SyncName
import io.accur8.neodeploy.resolvedmodel.ResolvedApp
import zio.process.CommandError
import zio.process.CommandError.NonZeroErrorCode
import zio.{Chunk, ExitCode, UIO, ZIO}

import scala.collection.Iterable

object model extends LoggingF {

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

  object DirectoryValue {
    implicit def zstringer[A <: DirectoryValue]: ZStringer[A] =
      new ZStringer[A] {
        override def toZString(a: A): ZString =
          a.unresolvedDirectory.asNioPath.toFile.getAbsolutePath
      }
  }

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

  object RSnapshotRootDirectory extends StringValue.Companion[RSnapshotRootDirectory]
  case class RSnapshotRootDirectory(value: String) extends DirectoryValue

  object RSnapshotConfigDirectory extends StringValue.Companion[RSnapshotConfigDirectory]
  case class RSnapshotConfigDirectory(value: String) extends DirectoryValue

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

  sealed trait Install {
    def description: String
  }
  object Install {

    implicit val jsonCodec =
      UnionCodecBuilder[Install]
        .typeFieldName("kind")
        .addSingleton("manual", Manual)
        .defaultType[FromRepo]
        .addType[FromRepo]("repo")
        .build


    object FromRepo extends MxFromRepo
    @CompanionGen
    case class FromRepo(
      organization: Organization,
      artifact: Artifact,
      version: Version,
      webappExplode: Boolean = true,
    ) extends Install {
      override def description: String = s"$organization:$artifact:$version"
    }

    case object Manual extends Install {
      override def description: String = "Manual install"
    }

  }

  object ApplicationDescriptor extends MxApplicationDescriptor {
  }
  @CompanionGen
  case class ApplicationDescriptor(
    name: ApplicationName,
    install: Install,
    jvmArgs: Iterable[String] = None,
    autoStart: Option[Boolean] = None,
    appArgs: Iterable[String] = Iterable.empty,
    mainClass: String,
    listenPort: Option[ListenPort] = None,
    javaVersion: JavaVersion = JavaVersion(11),
    stopServerCommand: Option[Command] = None,
    startServerCommand: Option[Command] = None,
    domainName: Option[DomainName] = None,
    domainNames: Iterable[DomainName] = Iterable.empty,
    trigger: JsDoc = JsDoc.empty,
    repository: Option[RepoConfigPrefix] = None,
  ) {
    def resolvedDomainNames = domainName ++ domainNames
  }

  object UserLogin extends StringValue.Companion[UserLogin] {
    def thisUser(): UserLogin =
      UserLogin(System.getProperty("user.name"))
  }

  case class UserLogin(value: String) extends StringValue

  object UserDescriptor extends MxUserDescriptor
  @CompanionGen
  case class UserDescriptor(
    login: UserLogin,
    aliases: Vector[QualifiedUserName] = Vector.empty,
    home: Option[String] = None,
    authorizedKeys: Vector[QualifiedUserName] = Vector.empty,
    a8VersionsExec: Option[String] = None,
    manageSshKeys: Boolean = true,
    appInstallDirectory: Option[AppsRootDirectory] = None,
  )


  object ServerName extends StringValue.Companion[ServerName] {
    def thisServer(): ServerName =
      ServerName(
        Exec("hostname")
          .execCaptureOutput()
          .stdout
          .splitList("\\.")
          .head
    )
  }
  case class ServerName(value: String) extends StringValue

  object RSnapshotClientDescriptor extends MxRSnapshotClientDescriptor
  @CompanionGen
  case class RSnapshotClientDescriptor(
    directories: Vector[String],
    runAt: String,
    hourly: Boolean = false,
    user: UserLogin,
    includeExcludeLines: Iterable[String] = Iterable.empty,
  ) {
  }

  object RSnapshotServerDescriptor extends MxRSnapshotServerDescriptor
  @CompanionGen
  case class RSnapshotServerDescriptor(
    user: UserLogin,
    rsnapshotRootDir: RSnapshotRootDirectory,
    rsnapshotConfigDir: RSnapshotConfigDirectory,
  )


  object ServerDescriptor extends MxServerDescriptor
  @CompanionGen
  case class ServerDescriptor(
    name: ServerName,
    supervisorDirectory: SupervisorDirectory,
    caddyDirectory: CaddyDirectory,
    serverName: DomainName,
    users: Vector[UserDescriptor],
    rsnapshotClient: Option[RSnapshotClientDescriptor] = None,
    a8VersionsExec: Option[String] = None,
    supervisorctlExec: Option[String] = None,
    rsnapshotServer: Option[RSnapshotServerDescriptor] = None,
  )

  object AuthorizedKey extends StringValue.Companion[AuthorizedKey]
  case class AuthorizedKey(value: String) extends StringValue

  object RepositoryDescriptor extends MxRepositoryDescriptor
  @CompanionGen
  case class RepositoryDescriptor(
    rsnapshotKey: Option[AuthorizedKey] = None,
    publicKeys: Iterable[Personnel] = Iterable.empty,
    servers: Vector[ServerDescriptor],
  )

  object QualifiedUserName extends StringValue.Companion[QualifiedUserName]
  case class QualifiedUserName(value: String) extends StringValue

  object Personnel extends MxPersonnel
  @CompanionGen
  case class Personnel(
    id: QualifiedUserName,
    description: String,
    authorizedKeysUrl: Option[String] = None,
    authorizedKeys: Iterable[AuthorizedKey] = None,
    members: Iterable[QualifiedUserName] = Iterable.empty,
  )

}
