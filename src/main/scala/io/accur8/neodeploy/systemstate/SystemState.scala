package io.accur8.neodeploy.systemstate

import a8.shared.CompanionGen
import a8.shared.json.UnionCodecBuilder
import io.accur8.neodeploy.HealthchecksDotIo
import io.accur8.neodeploy.model.Install.FromRepo
import io.accur8.neodeploy.model.{ApplicationDescriptor, UserLogin}
import io.accur8.neodeploy.systemstate.MxSystemState._
import io.accur8.neodeploy.systemstate.SystemStateModel._



object SystemState {

  object TextFile extends MxTextFile
  @CompanionGen
  case class TextFile(
    filename: String,
    contents: String,
    perms: UnixPerms = UnixPerms.empty,
  ) extends SystemState

  object SecretsTextFile extends MxSecretsTextFile {
  }
  @CompanionGen
  case class SecretsTextFile(
    filename: String,
    contents: SecretContent,
    perms: UnixPerms = UnixPerms.empty,
  ) extends SystemState {
    def asTextFile =
      TextFile(filename, contents.value, perms)
  }

  object JavaAppInstall extends MxJavaAppInstall
  @CompanionGen
  case class JavaAppInstall(
    appInstallDir: String,
    fromRepo: FromRepo,
    descriptor: ApplicationDescriptor,
    gitAppDirectory: String,
  ) extends SystemState

  object Directory extends MxDirectory
  @CompanionGen
  case class Directory(
    path: String,
    perms: UnixPerms = UnixPerms.empty,
  ) extends SystemState

  object Systemd extends MxSystemd
  @CompanionGen
  case class Systemd(
    unitName: String,
    enable: Vector[String] = Vector.empty,
    unitFiles: Vector[TextFile],
  ) extends HasSubStates {
    override def subStates: Vector[SystemState] = unitFiles
  }

  object Supervisor extends MxSupervisor
  @CompanionGen
  case class Supervisor(
    configFile: TextFile,
  ) extends HasSubStates {
    override def subStates: Vector[SystemState] = Vector(configFile)
  }

  object Caddy extends MxCaddy
  @CompanionGen
  case class Caddy(
    configFile: TextFile,
  ) extends HasSubStates {
    override def subStates: Vector[SystemState] = Vector(configFile)
  }

  case object Empty extends SystemState

  object Composite extends MxComposite
  @CompanionGen
  case class Composite(
    description: String,
    states: Vector[SystemState],
  ) extends HasSubStates {
    override def subStates: Vector[SystemState] = states
  }

  object HealthCheck extends MxHealthCheck
  @CompanionGen
  case class HealthCheck(
    data: HealthchecksDotIo.CheckUpsertRequest,
  ) extends SystemState

  sealed trait HasSubStates extends SystemState {
    def subStates: Vector[SystemState]
  }


  implicit val jsonCodec =
    UnionCodecBuilder[SystemState]
      .typeFieldName("kind")
      .addSingleton("empty", Empty)
      .addType[Caddy]("caddy")
      .addType[Composite]("composite")
      .addType[Directory]("directory")
      .addType[HealthCheck]("healthcheck")
      .addType[JavaAppInstall]("javaappinstall")
      .addType[Supervisor]("supervisor")
      .addType[Systemd]("systemd")
      .addType[TextFile]("textfile")
      .build

}

sealed trait SystemState


