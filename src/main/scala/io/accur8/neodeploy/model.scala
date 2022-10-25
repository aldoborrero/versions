package io.accur8.neodeploy


import a8.shared.FileSystem.{Directory, dir}
import a8.shared.{CompanionGen, LongValue, StringValue}
import io.accur8.neodeploy.Mxmodel._
import a8.shared.SharedImports._

import scala.collection.Iterable

object model {

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

  object SupervisorConfig extends MxSupervisorConfig
  @CompanionGen
  case class SupervisorConfig(
    jvmArgs: Iterable[String] = None,
    autoStart: Option[Boolean] = None,
    appArgs: Iterable[String] = Iterable.empty,
    mainClass: String,
  ) {

  }

  object CaddyConfig extends MxCaddyConfig
  @CompanionGen
  case class CaddyConfig(
    domainName: DomainName,
  )

  object ApplicationDescriptor extends MxApplicationDescriptor
  @CompanionGen
  case class ApplicationDescriptor(
    name: ApplicationName,
    organization: Organization,
    artifact: Artifact,
    version: Version,
    description: Option[String] = None,
    listenPort: Option[ListenPort] = None,
    javaVersion: Option[JavaVersion] = None,
    supervisorConfig: Option[SupervisorConfig] = None,
    caddyConfig: Option[CaddyConfig] = None,
    stopServerCommand: Iterable[String] = Seq.empty,
    startServerCommand: Iterable[String] = Seq.empty,
    webappExplode: Boolean = true,
  ) {

    def resolvedStopCommand: Iterable[String] =
      stopServerCommand match {
        case Seq() =>
          Seq(
            "supervisorctl",
            "stop",
            name.value,
          )
        case s =>
          s
      }

    def resolvedStartCommand: Iterable[String] =
      startServerCommand match {
        case Seq() =>
          Seq(
            "supervisorctl",
            "start",
            name.value,
          )
        case s =>
          s
      }

  }

}
