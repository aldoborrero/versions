package io.accur8.neodeploy.systemstate


import a8.shared.{CompanionGen, ZFileSystem}
import a8.shared.json.ast.JsObj
import a8.shared.json.{JsonCodec, JsonTypedCodec, UnionCodecBuilder, ast}
import io.accur8.neodeploy.{HealthchecksDotIo, LazyJsonCodec}
import io.accur8.neodeploy.model.Install.FromRepo
import io.accur8.neodeploy.model.{ApplicationDescriptor, UserLogin}
import io.accur8.neodeploy.systemstate.MxSystemState._
import io.accur8.neodeploy.systemstate.SystemStateModel._
import zio.Chunk
import a8.shared.SharedImports._

object SystemState {

  object TextFile extends MxTextFile
  @CompanionGen
  case class TextFile(
    filename: String,
    contents: String,
    perms: UnixPerms = UnixPerms.empty,
  ) extends SystemState with TextFileContentsMixin {
    override def prefix: String = ""
  }

  object SecretsTextFile extends MxSecretsTextFile {
  }
  @CompanionGen
  case class SecretsTextFile(
    filename: String,
    secretContents: SecretContent,
    perms: UnixPerms = UnixPerms.empty,
  ) extends SystemState with TextFileContentsMixin {
    override def contents: String = secretContents.value
    override def prefix: String = "secret "
  }


  object JavaAppInstall extends MxJavaAppInstall
  @CompanionGen
  case class JavaAppInstall(
    appInstallDir: String,
    fromRepo: FromRepo,
    descriptor: ApplicationDescriptor,
    gitAppDirectory: String,
  ) extends SystemState with JavaAppInstallMixin

  object Directory extends MxDirectory
  @CompanionGen
  case class Directory(
    path: String,
    perms: UnixPerms = UnixPerms.empty,
  ) extends SystemState with DirectoryMixin

  object Systemd extends MxSystemd
  @CompanionGen
  case class Systemd(
    unitName: String,
    enable: Vector[String] = Vector.empty,
    unitFiles: Vector[TextFile],
  ) extends HasSubStates with SystemdMixin {
    override def subStates: Vector[SystemState] = unitFiles
  }

  object Supervisor extends MxSupervisor
  @CompanionGen
  case class Supervisor(
    configFile: TextFile,
  ) extends HasSubStates with SupervisorMixin {
    override def subStates: Vector[SystemState] = Vector(configFile)
  }

  object Caddy extends MxCaddy
  @CompanionGen
  case class Caddy(
    configFile: TextFile,
  ) extends HasSubStates with CaddyMixin {
    override def subStates: Vector[SystemState] = Vector(configFile)
  }

  case object Empty extends SystemState {
    override def stateKey = None
    override def dryRun: Vector[String] = Vector.empty
    override def isActionNeeded = zsucceed(false)
    override def runApplyNewState = zunit
    override def runUninstallObsolete = zunit
  }

  object Composite extends MxComposite {

    implicit lazy val jsonCodec: JsonTypedCodec[Composite, JsObj] =
      LazyJsonCodec(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.description)
          .addField(_.states)
          .build
      )

  }
  @CompanionGen(jsonCodec = false)
  case class Composite(
    description: String,
    states: Vector[SystemState],
  ) extends HasSubStates with CompositeMixin {
    override def subStates: Vector[SystemState] = states
  }

  object HealthCheck extends MxHealthCheck
  @CompanionGen
  case class HealthCheck(
    data: HealthchecksDotIo.CheckUpsertRequest,
  ) extends SystemState with HealthCheckMixin

  sealed trait HasSubStates extends SystemState {
    def subStates: Vector[SystemState]
  }

  object RunCommandState extends MxRunCommandState
  @CompanionGen
  case class RunCommandState(
    override val stateKey: Option[StateKey] = None,
    installCommand: Option[Command] = None,
    uninstallCommand: Option[Command] = None,
  ) extends SystemState with RunCommandStateMixin


  object TriggeredState extends MxTriggeredState
  /**
   * if any changes are needed in triggerState then preTriggeredState will get applied
   * then triggerState then postTriggerState
   * for example for systemd the triggerState are all the systemd unit files
   * and the postTriggerState is the systemd daemon reload and enable commands
   */
  @CompanionGen
  case class TriggeredState(
    preTriggerState: SystemState,
    postTriggerState: SystemState,
    triggerState: SystemState,
  ) extends SystemState with TriggeredStateMixin


  implicit lazy val jsonCodec: JsonTypedCodec[SystemState, ast.JsObj] =
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
      .addType[SecretsTextFile]("secretstextfile")
      .build

}

sealed trait SystemState extends SystemStateMixin {
}


