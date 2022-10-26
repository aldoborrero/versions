package io.accur8.neodeploy


import a8.shared.FileSystem.{Directory, dir}
import a8.shared.{CompanionGen, LongValue, StringValue}
import io.accur8.neodeploy.Mxmodel._
import a8.shared.SharedImports._
import a8.shared.json.ast.{JsArr, JsDoc, JsNothing, JsStr, JsVal}
import a8.shared.json.{JsonCodec, JsonTypedCodec, UnionCodecBuilder}

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
    javaVersion: Option[JavaVersion] = None,
    stopServerCommand: Option[Command] = None,
    startServerCommand: Option[Command] = None,
    domainName: Option[DomainName],
    trigger: JsDoc = JsDoc.empty,
  ) {

    lazy val resolvedStopCommand: Command =
      stopServerCommand
        .getOrElse(ApplicationDescriptor.supervisorCommand("stop", name))

    lazy val resolvedStartCommand: Command =
      startServerCommand
        .getOrElse(ApplicationDescriptor.supervisorCommand("start", name))


  }

}
