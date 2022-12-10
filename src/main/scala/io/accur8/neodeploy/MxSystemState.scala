package io.accur8.neodeploy

/**

  WARNING THIS IS GENERATED CODE.  DO NOT EDIT.

  The only manually maintained code is the code between the //==== (normally where you add your imports)

*/

//====
import io.accur8.neodeploy.systemstate.SystemState._
import io.accur8.neodeploy.model.{ApplicationDescriptor, UserLogin}
import io.accur8.neodeploy.model.Install.FromRepo
import io.accur8.neodeploy.systemstate.SystemState
import io.accur8.neodeploy.systemstate.SystemStateModel.{PreviousState, UnixPerms}

//====

import a8.shared.Meta.{CaseClassParm, Generator, Constructors}



object MxSystemState {
  
  trait MxPreviousState {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[PreviousState,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[PreviousState,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[PreviousState,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.value)
      )
      .build
    
    implicit val catsEq: cats.Eq[PreviousState] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[PreviousState,parameters.type] =  {
      val constructors = Constructors[PreviousState](1, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val value: CaseClassParm[PreviousState,SystemState] = CaseClassParm[PreviousState,SystemState]("value", _.value, (d,v) => d.copy(value = v), None, 0)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): PreviousState = {
        PreviousState(
          value = values(0).asInstanceOf[SystemState],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): PreviousState = {
        val value =
          PreviousState(
            value = values.next().asInstanceOf[SystemState],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(value: SystemState): PreviousState =
        PreviousState(value)
    
    }
    
    
    lazy val typeName = "PreviousState"
  
  }
  
  
  
  
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
  
  
  
  
  trait MxSystemd {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[Systemd,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[Systemd,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[Systemd,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.user)
          .addField(_.unitName)
          .addField(_.unitFiles)
      )
      .build
    
    implicit val catsEq: cats.Eq[Systemd] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[Systemd,parameters.type] =  {
      val constructors = Constructors[Systemd](3, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val user: CaseClassParm[Systemd,UserLogin] = CaseClassParm[Systemd,UserLogin]("user", _.user, (d,v) => d.copy(user = v), None, 0)
      lazy val unitName: CaseClassParm[Systemd,String] = CaseClassParm[Systemd,String]("unitName", _.unitName, (d,v) => d.copy(unitName = v), None, 1)
      lazy val unitFiles: CaseClassParm[Systemd,Vector[TextFile]] = CaseClassParm[Systemd,Vector[TextFile]]("unitFiles", _.unitFiles, (d,v) => d.copy(unitFiles = v), None, 2)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): Systemd = {
        Systemd(
          user = values(0).asInstanceOf[UserLogin],
          unitName = values(1).asInstanceOf[String],
          unitFiles = values(2).asInstanceOf[Vector[TextFile]],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): Systemd = {
        val value =
          Systemd(
            user = values.next().asInstanceOf[UserLogin],
            unitName = values.next().asInstanceOf[String],
            unitFiles = values.next().asInstanceOf[Vector[TextFile]],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(user: UserLogin, unitName: String, unitFiles: Vector[TextFile]): Systemd =
        Systemd(user, unitName, unitFiles)
    
    }
    
    
    lazy val typeName = "Systemd"
  
  }
  
  
  
  
  trait MxSupervisor {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[Supervisor,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[Supervisor,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[Supervisor,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.configFile)
      )
      .build
    
    implicit val catsEq: cats.Eq[Supervisor] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[Supervisor,parameters.type] =  {
      val constructors = Constructors[Supervisor](1, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val configFile: CaseClassParm[Supervisor,TextFile] = CaseClassParm[Supervisor,TextFile]("configFile", _.configFile, (d,v) => d.copy(configFile = v), None, 0)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): Supervisor = {
        Supervisor(
          configFile = values(0).asInstanceOf[TextFile],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): Supervisor = {
        val value =
          Supervisor(
            configFile = values.next().asInstanceOf[TextFile],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(configFile: TextFile): Supervisor =
        Supervisor(configFile)
    
    }
    
    
    lazy val typeName = "Supervisor"
  
  }
  
  
  
  
  trait MxCaddy {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[Caddy,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[Caddy,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[Caddy,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.configFile)
      )
      .build
    
    implicit val catsEq: cats.Eq[Caddy] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[Caddy,parameters.type] =  {
      val constructors = Constructors[Caddy](1, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val configFile: CaseClassParm[Caddy,TextFile] = CaseClassParm[Caddy,TextFile]("configFile", _.configFile, (d,v) => d.copy(configFile = v), None, 0)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): Caddy = {
        Caddy(
          configFile = values(0).asInstanceOf[TextFile],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): Caddy = {
        val value =
          Caddy(
            configFile = values.next().asInstanceOf[TextFile],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(configFile: TextFile): Caddy =
        Caddy(configFile)
    
    }
    
    
    lazy val typeName = "Caddy"
  
  }
  
  
  
  
  trait MxComposite {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[Composite,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[Composite,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[Composite,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.description)
          .addField(_.states)
      )
      .build
    
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
}
