package io.accur8.neodeploy.systemstate

/**

  WARNING THIS IS GENERATED CODE.  DO NOT EDIT.

  The only manually maintained code is the code between the //==== (normally where you add your imports)

*/

//====
import io.accur8.neodeploy.HealthchecksDotIo
import io.accur8.neodeploy.model.ApplicationDescriptor
import io.accur8.neodeploy.model.Install.FromRepo
import io.accur8.neodeploy.systemstate.SystemState._
import io.accur8.neodeploy.systemstate.SystemStateModel._

//====

import a8.shared.Meta.{CaseClassParm, Generator, Constructors}



object MxSystemState {
  
  trait MxTextFile {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[TextFile,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[TextFile,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[TextFile,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.filename)
          .addField(_.contents)
          .addField(_.perms)
      )
      .build
    
    implicit val zioEq: zio.prelude.Equal[TextFile] = zio.prelude.Equal.default
    
    implicit val catsEq: cats.Eq[TextFile] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[TextFile,parameters.type] =  {
      val constructors = Constructors[TextFile](3, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val filename: CaseClassParm[TextFile,String] = CaseClassParm[TextFile,String]("filename", _.filename, (d,v) => d.copy(filename = v), None, 0)
      lazy val contents: CaseClassParm[TextFile,String] = CaseClassParm[TextFile,String]("contents", _.contents, (d,v) => d.copy(contents = v), None, 1)
      lazy val perms: CaseClassParm[TextFile,UnixPerms] = CaseClassParm[TextFile,UnixPerms]("perms", _.perms, (d,v) => d.copy(perms = v), Some(()=> UnixPerms.empty), 2)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): TextFile = {
        TextFile(
          filename = values(0).asInstanceOf[String],
          contents = values(1).asInstanceOf[String],
          perms = values(2).asInstanceOf[UnixPerms],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): TextFile = {
        val value =
          TextFile(
            filename = values.next().asInstanceOf[String],
            contents = values.next().asInstanceOf[String],
            perms = values.next().asInstanceOf[UnixPerms],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(filename: String, contents: String, perms: UnixPerms): TextFile =
        TextFile(filename, contents, perms)
    
    }
    
    
    lazy val typeName = "TextFile"
  
  }
  
  
  
  
  trait MxSecretsTextFile {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[SecretsTextFile,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[SecretsTextFile,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[SecretsTextFile,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.filename)
          .addField(_.secretContents)
          .addField(_.perms)
      )
      .build
    
    implicit val zioEq: zio.prelude.Equal[SecretsTextFile] = zio.prelude.Equal.default
    
    implicit val catsEq: cats.Eq[SecretsTextFile] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[SecretsTextFile,parameters.type] =  {
      val constructors = Constructors[SecretsTextFile](3, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val filename: CaseClassParm[SecretsTextFile,String] = CaseClassParm[SecretsTextFile,String]("filename", _.filename, (d,v) => d.copy(filename = v), None, 0)
      lazy val secretContents: CaseClassParm[SecretsTextFile,SecretContent] = CaseClassParm[SecretsTextFile,SecretContent]("secretContents", _.secretContents, (d,v) => d.copy(secretContents = v), None, 1)
      lazy val perms: CaseClassParm[SecretsTextFile,UnixPerms] = CaseClassParm[SecretsTextFile,UnixPerms]("perms", _.perms, (d,v) => d.copy(perms = v), Some(()=> UnixPerms.empty), 2)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): SecretsTextFile = {
        SecretsTextFile(
          filename = values(0).asInstanceOf[String],
          secretContents = values(1).asInstanceOf[SecretContent],
          perms = values(2).asInstanceOf[UnixPerms],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): SecretsTextFile = {
        val value =
          SecretsTextFile(
            filename = values.next().asInstanceOf[String],
            secretContents = values.next().asInstanceOf[SecretContent],
            perms = values.next().asInstanceOf[UnixPerms],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(filename: String, secretContents: SecretContent, perms: UnixPerms): SecretsTextFile =
        SecretsTextFile(filename, secretContents, perms)
    
    }
    
    
    lazy val typeName = "SecretsTextFile"
  
  }
  
  
  
  
  trait MxJavaAppInstall {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[JavaAppInstall,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[JavaAppInstall,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[JavaAppInstall,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.appInstallDir)
          .addField(_.fromRepo)
          .addField(_.descriptor)
          .addField(_.gitAppDirectory)
      )
      .build
    
    implicit val zioEq: zio.prelude.Equal[JavaAppInstall] = zio.prelude.Equal.default
    
    implicit val catsEq: cats.Eq[JavaAppInstall] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[JavaAppInstall,parameters.type] =  {
      val constructors = Constructors[JavaAppInstall](4, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val appInstallDir: CaseClassParm[JavaAppInstall,String] = CaseClassParm[JavaAppInstall,String]("appInstallDir", _.appInstallDir, (d,v) => d.copy(appInstallDir = v), None, 0)
      lazy val fromRepo: CaseClassParm[JavaAppInstall,FromRepo] = CaseClassParm[JavaAppInstall,FromRepo]("fromRepo", _.fromRepo, (d,v) => d.copy(fromRepo = v), None, 1)
      lazy val descriptor: CaseClassParm[JavaAppInstall,ApplicationDescriptor] = CaseClassParm[JavaAppInstall,ApplicationDescriptor]("descriptor", _.descriptor, (d,v) => d.copy(descriptor = v), None, 2)
      lazy val gitAppDirectory: CaseClassParm[JavaAppInstall,String] = CaseClassParm[JavaAppInstall,String]("gitAppDirectory", _.gitAppDirectory, (d,v) => d.copy(gitAppDirectory = v), None, 3)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): JavaAppInstall = {
        JavaAppInstall(
          appInstallDir = values(0).asInstanceOf[String],
          fromRepo = values(1).asInstanceOf[FromRepo],
          descriptor = values(2).asInstanceOf[ApplicationDescriptor],
          gitAppDirectory = values(3).asInstanceOf[String],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): JavaAppInstall = {
        val value =
          JavaAppInstall(
            appInstallDir = values.next().asInstanceOf[String],
            fromRepo = values.next().asInstanceOf[FromRepo],
            descriptor = values.next().asInstanceOf[ApplicationDescriptor],
            gitAppDirectory = values.next().asInstanceOf[String],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(appInstallDir: String, fromRepo: FromRepo, descriptor: ApplicationDescriptor, gitAppDirectory: String): JavaAppInstall =
        JavaAppInstall(appInstallDir, fromRepo, descriptor, gitAppDirectory)
    
    }
    
    
    lazy val typeName = "JavaAppInstall"
  
  }
  
  
  
  
  trait MxDirectory {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[Directory,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[Directory,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[Directory,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.path)
          .addField(_.perms)
      )
      .build
    
    implicit val zioEq: zio.prelude.Equal[Directory] = zio.prelude.Equal.default
    
    implicit val catsEq: cats.Eq[Directory] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[Directory,parameters.type] =  {
      val constructors = Constructors[Directory](2, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val path: CaseClassParm[Directory,String] = CaseClassParm[Directory,String]("path", _.path, (d,v) => d.copy(path = v), None, 0)
      lazy val perms: CaseClassParm[Directory,UnixPerms] = CaseClassParm[Directory,UnixPerms]("perms", _.perms, (d,v) => d.copy(perms = v), Some(()=> UnixPerms.empty), 1)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): Directory = {
        Directory(
          path = values(0).asInstanceOf[String],
          perms = values(1).asInstanceOf[UnixPerms],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): Directory = {
        val value =
          Directory(
            path = values.next().asInstanceOf[String],
            perms = values.next().asInstanceOf[UnixPerms],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(path: String, perms: UnixPerms): Directory =
        Directory(path, perms)
    
    }
    
    
    lazy val typeName = "Directory"
  
  }
  
  
  
  
  trait MxComposite {
  
    implicit val zioEq: zio.prelude.Equal[Composite] = zio.prelude.Equal.default
    
    implicit val catsEq: cats.Eq[Composite] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[Composite,parameters.type] =  {
      val constructors = Constructors[Composite](2, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val description: CaseClassParm[Composite,String] = CaseClassParm[Composite,String]("description", _.description, (d,v) => d.copy(description = v), None, 0)
      lazy val states: CaseClassParm[Composite,Vector[SystemState]] = CaseClassParm[Composite,Vector[SystemState]]("states", _.states, (d,v) => d.copy(states = v), None, 1)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): Composite = {
        Composite(
          description = values(0).asInstanceOf[String],
          states = values(1).asInstanceOf[Vector[SystemState]],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): Composite = {
        val value =
          Composite(
            description = values.next().asInstanceOf[String],
            states = values.next().asInstanceOf[Vector[SystemState]],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(description: String, states: Vector[SystemState]): Composite =
        Composite(description, states)
    
    }
    
    
    lazy val typeName = "Composite"
  
  }
  
  
  
  
  trait MxHealthCheck {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[HealthCheck,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[HealthCheck,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[HealthCheck,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.data)
      )
      .build
    
    implicit val zioEq: zio.prelude.Equal[HealthCheck] = zio.prelude.Equal.default
    
    implicit val catsEq: cats.Eq[HealthCheck] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[HealthCheck,parameters.type] =  {
      val constructors = Constructors[HealthCheck](1, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val data: CaseClassParm[HealthCheck,HealthchecksDotIo.CheckUpsertRequest] = CaseClassParm[HealthCheck,HealthchecksDotIo.CheckUpsertRequest]("data", _.data, (d,v) => d.copy(data = v), None, 0)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): HealthCheck = {
        HealthCheck(
          data = values(0).asInstanceOf[HealthchecksDotIo.CheckUpsertRequest],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): HealthCheck = {
        val value =
          HealthCheck(
            data = values.next().asInstanceOf[HealthchecksDotIo.CheckUpsertRequest],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(data: HealthchecksDotIo.CheckUpsertRequest): HealthCheck =
        HealthCheck(data)
    
    }
    
    
    lazy val typeName = "HealthCheck"
  
  }
  
  
  
  
  trait MxRunCommandState {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[RunCommandState,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[RunCommandState,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[RunCommandState,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.stateKey)
          .addField(_.installCommands)
          .addField(_.uninstallCommands)
      )
      .build
    
    implicit val zioEq: zio.prelude.Equal[RunCommandState] = zio.prelude.Equal.default
    
    implicit val catsEq: cats.Eq[RunCommandState] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[RunCommandState,parameters.type] =  {
      val constructors = Constructors[RunCommandState](3, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val stateKey: CaseClassParm[RunCommandState,Option[StateKey]] = CaseClassParm[RunCommandState,Option[StateKey]]("stateKey", _.stateKey, (d,v) => d.copy(stateKey = v), Some(()=> None), 0)
      lazy val installCommands: CaseClassParm[RunCommandState,Vector[Command]] = CaseClassParm[RunCommandState,Vector[Command]]("installCommands", _.installCommands, (d,v) => d.copy(installCommands = v), Some(()=> Vector.empty), 1)
      lazy val uninstallCommands: CaseClassParm[RunCommandState,Vector[Command]] = CaseClassParm[RunCommandState,Vector[Command]]("uninstallCommands", _.uninstallCommands, (d,v) => d.copy(uninstallCommands = v), Some(()=> Vector.empty), 2)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): RunCommandState = {
        RunCommandState(
          stateKey = values(0).asInstanceOf[Option[StateKey]],
          installCommands = values(1).asInstanceOf[Vector[Command]],
          uninstallCommands = values(2).asInstanceOf[Vector[Command]],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): RunCommandState = {
        val value =
          RunCommandState(
            stateKey = values.next().asInstanceOf[Option[StateKey]],
            installCommands = values.next().asInstanceOf[Vector[Command]],
            uninstallCommands = values.next().asInstanceOf[Vector[Command]],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(stateKey: Option[StateKey], installCommands: Vector[Command], uninstallCommands: Vector[Command]): RunCommandState =
        RunCommandState(stateKey, installCommands, uninstallCommands)
    
    }
    
    
    lazy val typeName = "RunCommandState"
  
  }
  
  
  
  
  trait MxTriggeredState {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[TriggeredState,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[TriggeredState,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[TriggeredState,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.preTriggerState)
          .addField(_.postTriggerState)
          .addField(_.triggerState)
      )
      .build
    
    implicit val zioEq: zio.prelude.Equal[TriggeredState] = zio.prelude.Equal.default
    
    implicit val catsEq: cats.Eq[TriggeredState] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[TriggeredState,parameters.type] =  {
      val constructors = Constructors[TriggeredState](3, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val preTriggerState: CaseClassParm[TriggeredState,SystemState] = CaseClassParm[TriggeredState,SystemState]("preTriggerState", _.preTriggerState, (d,v) => d.copy(preTriggerState = v), Some(()=> SystemState.Empty), 0)
      lazy val postTriggerState: CaseClassParm[TriggeredState,SystemState] = CaseClassParm[TriggeredState,SystemState]("postTriggerState", _.postTriggerState, (d,v) => d.copy(postTriggerState = v), Some(()=> SystemState.Empty), 1)
      lazy val triggerState: CaseClassParm[TriggeredState,SystemState] = CaseClassParm[TriggeredState,SystemState]("triggerState", _.triggerState, (d,v) => d.copy(triggerState = v), Some(()=> SystemState.Empty), 2)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): TriggeredState = {
        TriggeredState(
          preTriggerState = values(0).asInstanceOf[SystemState],
          postTriggerState = values(1).asInstanceOf[SystemState],
          triggerState = values(2).asInstanceOf[SystemState],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): TriggeredState = {
        val value =
          TriggeredState(
            preTriggerState = values.next().asInstanceOf[SystemState],
            postTriggerState = values.next().asInstanceOf[SystemState],
            triggerState = values.next().asInstanceOf[SystemState],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(preTriggerState: SystemState, postTriggerState: SystemState, triggerState: SystemState): TriggeredState =
        TriggeredState(preTriggerState, postTriggerState, triggerState)
    
    }
    
    
    lazy val typeName = "TriggeredState"
  
  }
}
