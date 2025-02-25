package io.accur8.neodeploy

/**

  WARNING THIS IS GENERATED CODE.  DO NOT EDIT.

  The only manually maintained code is the code between the //==== (normally where you add your imports)

*/

//====
import a8.shared.ZFileSystem.Directory
import a8.shared.json.ast.{JsDoc, JsObj, JsVal}
import a8.versions.RepositoryOps.RepoConfigPrefix
import com.softwaremill.sttp.Uri
import io.accur8.neodeploy.model.DockerDescriptor.UninstallAction
import io.accur8.neodeploy.model._
import io.accur8.neodeploy.model.Install.{JavaApp, Manual}

//====

import a8.shared.Meta.{CaseClassParm, Generator, Constructors}



object Mxmodel {
  
  trait MxJavaApp {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[JavaApp,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[JavaApp,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[JavaApp,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.organization)
          .addField(_.artifact)
          .addField(_.version)
          .addField(_.webappExplode)
          .addField(_.jvmArgs)
          .addField(_.appArgs)
          .addField(_.mainClass)
          .addField(_.javaVersion)
          .addField(_.repository)
      )
      .build
    
    implicit val zioEq: zio.prelude.Equal[JavaApp] = zio.prelude.Equal.default
    
    implicit val catsEq: cats.Eq[JavaApp] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[JavaApp,parameters.type] =  {
      val constructors = Constructors[JavaApp](9, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val organization: CaseClassParm[JavaApp,Organization] = CaseClassParm[JavaApp,Organization]("organization", _.organization, (d,v) => d.copy(organization = v), None, 0)
      lazy val artifact: CaseClassParm[JavaApp,Artifact] = CaseClassParm[JavaApp,Artifact]("artifact", _.artifact, (d,v) => d.copy(artifact = v), None, 1)
      lazy val version: CaseClassParm[JavaApp,Version] = CaseClassParm[JavaApp,Version]("version", _.version, (d,v) => d.copy(version = v), None, 2)
      lazy val webappExplode: CaseClassParm[JavaApp,Boolean] = CaseClassParm[JavaApp,Boolean]("webappExplode", _.webappExplode, (d,v) => d.copy(webappExplode = v), Some(()=> true), 3)
      lazy val jvmArgs: CaseClassParm[JavaApp,Iterable[String]] = CaseClassParm[JavaApp,Iterable[String]]("jvmArgs", _.jvmArgs, (d,v) => d.copy(jvmArgs = v), Some(()=> None), 4)
      lazy val appArgs: CaseClassParm[JavaApp,Iterable[String]] = CaseClassParm[JavaApp,Iterable[String]]("appArgs", _.appArgs, (d,v) => d.copy(appArgs = v), Some(()=> Iterable.empty), 5)
      lazy val mainClass: CaseClassParm[JavaApp,String] = CaseClassParm[JavaApp,String]("mainClass", _.mainClass, (d,v) => d.copy(mainClass = v), None, 6)
      lazy val javaVersion: CaseClassParm[JavaApp,JavaVersion] = CaseClassParm[JavaApp,JavaVersion]("javaVersion", _.javaVersion, (d,v) => d.copy(javaVersion = v), Some(()=> JavaVersion(11)), 7)
      lazy val repository: CaseClassParm[JavaApp,Option[RepoConfigPrefix]] = CaseClassParm[JavaApp,Option[RepoConfigPrefix]]("repository", _.repository, (d,v) => d.copy(repository = v), Some(()=> None), 8)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): JavaApp = {
        JavaApp(
          organization = values(0).asInstanceOf[Organization],
          artifact = values(1).asInstanceOf[Artifact],
          version = values(2).asInstanceOf[Version],
          webappExplode = values(3).asInstanceOf[Boolean],
          jvmArgs = values(4).asInstanceOf[Iterable[String]],
          appArgs = values(5).asInstanceOf[Iterable[String]],
          mainClass = values(6).asInstanceOf[String],
          javaVersion = values(7).asInstanceOf[JavaVersion],
          repository = values(8).asInstanceOf[Option[RepoConfigPrefix]],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): JavaApp = {
        val value =
          JavaApp(
            organization = values.next().asInstanceOf[Organization],
            artifact = values.next().asInstanceOf[Artifact],
            version = values.next().asInstanceOf[Version],
            webappExplode = values.next().asInstanceOf[Boolean],
            jvmArgs = values.next().asInstanceOf[Iterable[String]],
            appArgs = values.next().asInstanceOf[Iterable[String]],
            mainClass = values.next().asInstanceOf[String],
            javaVersion = values.next().asInstanceOf[JavaVersion],
            repository = values.next().asInstanceOf[Option[RepoConfigPrefix]],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(organization: Organization, artifact: Artifact, version: Version, webappExplode: Boolean, jvmArgs: Iterable[String], appArgs: Iterable[String], mainClass: String, javaVersion: JavaVersion, repository: Option[RepoConfigPrefix]): JavaApp =
        JavaApp(organization, artifact, version, webappExplode, jvmArgs, appArgs, mainClass, javaVersion, repository)
    
    }
    
    
    lazy val typeName = "JavaApp"
  
  }
  
  
  
  
  trait MxManual {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[Manual,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[Manual,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[Manual,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.description)
          .addField(_.execArgs)
      )
      .build
    
    implicit val zioEq: zio.prelude.Equal[Manual] = zio.prelude.Equal.default
    
    implicit val catsEq: cats.Eq[Manual] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[Manual,parameters.type] =  {
      val constructors = Constructors[Manual](2, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val description: CaseClassParm[Manual,String] = CaseClassParm[Manual,String]("description", _.description, (d,v) => d.copy(description = v), Some(()=> "manual install"), 0)
      lazy val execArgs: CaseClassParm[Manual,Vector[String]] = CaseClassParm[Manual,Vector[String]]("execArgs", _.execArgs, (d,v) => d.copy(execArgs = v), Some(()=> Vector.empty), 1)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): Manual = {
        Manual(
          description = values(0).asInstanceOf[String],
          execArgs = values(1).asInstanceOf[Vector[String]],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): Manual = {
        val value =
          Manual(
            description = values.next().asInstanceOf[String],
            execArgs = values.next().asInstanceOf[Vector[String]],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(description: String, execArgs: Vector[String]): Manual =
        Manual(description, execArgs)
    
    }
    
    
    lazy val typeName = "Manual"
  
  }
  
  
  
  
  trait MxSupervisorDescriptor {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[SupervisorDescriptor,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[SupervisorDescriptor,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[SupervisorDescriptor,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.autoStart)
          .addField(_.autoRestart)
          .addField(_.startRetries)
          .addField(_.startSecs)
      )
      .build
    
    implicit val zioEq: zio.prelude.Equal[SupervisorDescriptor] = zio.prelude.Equal.default
    
    implicit val catsEq: cats.Eq[SupervisorDescriptor] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[SupervisorDescriptor,parameters.type] =  {
      val constructors = Constructors[SupervisorDescriptor](4, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val autoStart: CaseClassParm[SupervisorDescriptor,Option[Boolean]] = CaseClassParm[SupervisorDescriptor,Option[Boolean]]("autoStart", _.autoStart, (d,v) => d.copy(autoStart = v), Some(()=> None), 0)
      lazy val autoRestart: CaseClassParm[SupervisorDescriptor,Option[Boolean]] = CaseClassParm[SupervisorDescriptor,Option[Boolean]]("autoRestart", _.autoRestart, (d,v) => d.copy(autoRestart = v), Some(()=> None), 1)
      lazy val startRetries: CaseClassParm[SupervisorDescriptor,Option[Int]] = CaseClassParm[SupervisorDescriptor,Option[Int]]("startRetries", _.startRetries, (d,v) => d.copy(startRetries = v), Some(()=> None), 2)
      lazy val startSecs: CaseClassParm[SupervisorDescriptor,Option[Int]] = CaseClassParm[SupervisorDescriptor,Option[Int]]("startSecs", _.startSecs, (d,v) => d.copy(startSecs = v), Some(()=> None), 3)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): SupervisorDescriptor = {
        SupervisorDescriptor(
          autoStart = values(0).asInstanceOf[Option[Boolean]],
          autoRestart = values(1).asInstanceOf[Option[Boolean]],
          startRetries = values(2).asInstanceOf[Option[Int]],
          startSecs = values(3).asInstanceOf[Option[Int]],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): SupervisorDescriptor = {
        val value =
          SupervisorDescriptor(
            autoStart = values.next().asInstanceOf[Option[Boolean]],
            autoRestart = values.next().asInstanceOf[Option[Boolean]],
            startRetries = values.next().asInstanceOf[Option[Int]],
            startSecs = values.next().asInstanceOf[Option[Int]],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(autoStart: Option[Boolean], autoRestart: Option[Boolean], startRetries: Option[Int], startSecs: Option[Int]): SupervisorDescriptor =
        SupervisorDescriptor(autoStart, autoRestart, startRetries, startSecs)
    
    }
    
    
    lazy val typeName = "SupervisorDescriptor"
  
  }
  
  
  
  
  trait MxSystemdDescriptor {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[SystemdDescriptor,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[SystemdDescriptor,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[SystemdDescriptor,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.unitName)
          .addField(_.environment)
          .addField(_.Type)
      )
      .build
    
    implicit val zioEq: zio.prelude.Equal[SystemdDescriptor] = zio.prelude.Equal.default
    
    implicit val catsEq: cats.Eq[SystemdDescriptor] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[SystemdDescriptor,parameters.type] =  {
      val constructors = Constructors[SystemdDescriptor](3, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val unitName: CaseClassParm[SystemdDescriptor,Option[String]] = CaseClassParm[SystemdDescriptor,Option[String]]("unitName", _.unitName, (d,v) => d.copy(unitName = v), Some(()=> None), 0)
      lazy val environment: CaseClassParm[SystemdDescriptor,Map[String,String]] = CaseClassParm[SystemdDescriptor,Map[String,String]]("environment", _.environment, (d,v) => d.copy(environment = v), Some(()=> Map.empty), 1)
      lazy val Type: CaseClassParm[SystemdDescriptor,String] = CaseClassParm[SystemdDescriptor,String]("Type", _.Type, (d,v) => d.copy(Type = v), Some(()=> "simple"), 2)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): SystemdDescriptor = {
        SystemdDescriptor(
          unitName = values(0).asInstanceOf[Option[String]],
          environment = values(1).asInstanceOf[Map[String,String]],
          Type = values(2).asInstanceOf[String],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): SystemdDescriptor = {
        val value =
          SystemdDescriptor(
            unitName = values.next().asInstanceOf[Option[String]],
            environment = values.next().asInstanceOf[Map[String,String]],
            Type = values.next().asInstanceOf[String],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(unitName: Option[String], environment: Map[String,String], Type: String): SystemdDescriptor =
        SystemdDescriptor(unitName, environment, Type)
    
    }
    
    
    lazy val typeName = "SystemdDescriptor"
  
  }
  
  
  
  
  trait MxDockerDescriptor {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[DockerDescriptor,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[DockerDescriptor,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[DockerDescriptor,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.name)
          .addField(_.args)
          .addField(_.uninstallAction)
      )
      .build
    
    implicit val zioEq: zio.prelude.Equal[DockerDescriptor] = zio.prelude.Equal.default
    
    implicit val catsEq: cats.Eq[DockerDescriptor] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[DockerDescriptor,parameters.type] =  {
      val constructors = Constructors[DockerDescriptor](3, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val name: CaseClassParm[DockerDescriptor,String] = CaseClassParm[DockerDescriptor,String]("name", _.name, (d,v) => d.copy(name = v), None, 0)
      lazy val args: CaseClassParm[DockerDescriptor,Vector[String]] = CaseClassParm[DockerDescriptor,Vector[String]]("args", _.args, (d,v) => d.copy(args = v), None, 1)
      lazy val uninstallAction: CaseClassParm[DockerDescriptor,UninstallAction] = CaseClassParm[DockerDescriptor,UninstallAction]("uninstallAction", _.uninstallAction, (d,v) => d.copy(uninstallAction = v), Some(()=> UninstallAction.Stop), 2)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): DockerDescriptor = {
        DockerDescriptor(
          name = values(0).asInstanceOf[String],
          args = values(1).asInstanceOf[Vector[String]],
          uninstallAction = values(2).asInstanceOf[UninstallAction],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): DockerDescriptor = {
        val value =
          DockerDescriptor(
            name = values.next().asInstanceOf[String],
            args = values.next().asInstanceOf[Vector[String]],
            uninstallAction = values.next().asInstanceOf[UninstallAction],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(name: String, args: Vector[String], uninstallAction: UninstallAction): DockerDescriptor =
        DockerDescriptor(name, args, uninstallAction)
    
    }
    
    
    lazy val typeName = "DockerDescriptor"
  
  }
  
  
  
  
  trait MxApplicationDescriptor {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[ApplicationDescriptor,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[ApplicationDescriptor,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[ApplicationDescriptor,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.name)
          .addField(_.install)
          .addField(_.caddyConfig)
          .addField(_.listenPort)
          .addField(_.stopServerCommand)
          .addField(_.startServerCommand)
          .addField(_.domainName)
          .addField(_.domainNames)
          .addField(_.launcher)
      )
      .build
    
    implicit val zioEq: zio.prelude.Equal[ApplicationDescriptor] = zio.prelude.Equal.default
    
    implicit val catsEq: cats.Eq[ApplicationDescriptor] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[ApplicationDescriptor,parameters.type] =  {
      val constructors = Constructors[ApplicationDescriptor](9, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val name: CaseClassParm[ApplicationDescriptor,ApplicationName] = CaseClassParm[ApplicationDescriptor,ApplicationName]("name", _.name, (d,v) => d.copy(name = v), None, 0)
      lazy val install: CaseClassParm[ApplicationDescriptor,Install] = CaseClassParm[ApplicationDescriptor,Install]("install", _.install, (d,v) => d.copy(install = v), Some(()=> Install.Manual.empty), 1)
      lazy val caddyConfig: CaseClassParm[ApplicationDescriptor,Option[String]] = CaseClassParm[ApplicationDescriptor,Option[String]]("caddyConfig", _.caddyConfig, (d,v) => d.copy(caddyConfig = v), Some(()=> None), 2)
      lazy val listenPort: CaseClassParm[ApplicationDescriptor,Option[ListenPort]] = CaseClassParm[ApplicationDescriptor,Option[ListenPort]]("listenPort", _.listenPort, (d,v) => d.copy(listenPort = v), Some(()=> None), 3)
      lazy val stopServerCommand: CaseClassParm[ApplicationDescriptor,Option[Command]] = CaseClassParm[ApplicationDescriptor,Option[Command]]("stopServerCommand", _.stopServerCommand, (d,v) => d.copy(stopServerCommand = v), Some(()=> None), 4)
      lazy val startServerCommand: CaseClassParm[ApplicationDescriptor,Option[Command]] = CaseClassParm[ApplicationDescriptor,Option[Command]]("startServerCommand", _.startServerCommand, (d,v) => d.copy(startServerCommand = v), Some(()=> None), 5)
      lazy val domainName: CaseClassParm[ApplicationDescriptor,Option[DomainName]] = CaseClassParm[ApplicationDescriptor,Option[DomainName]]("domainName", _.domainName, (d,v) => d.copy(domainName = v), Some(()=> None), 6)
      lazy val domainNames: CaseClassParm[ApplicationDescriptor,Iterable[DomainName]] = CaseClassParm[ApplicationDescriptor,Iterable[DomainName]]("domainNames", _.domainNames, (d,v) => d.copy(domainNames = v), Some(()=> Iterable.empty), 7)
      lazy val launcher: CaseClassParm[ApplicationDescriptor,Launcher] = CaseClassParm[ApplicationDescriptor,Launcher]("launcher", _.launcher, (d,v) => d.copy(launcher = v), Some(()=> SupervisorDescriptor.empty), 8)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): ApplicationDescriptor = {
        ApplicationDescriptor(
          name = values(0).asInstanceOf[ApplicationName],
          install = values(1).asInstanceOf[Install],
          caddyConfig = values(2).asInstanceOf[Option[String]],
          listenPort = values(3).asInstanceOf[Option[ListenPort]],
          stopServerCommand = values(4).asInstanceOf[Option[Command]],
          startServerCommand = values(5).asInstanceOf[Option[Command]],
          domainName = values(6).asInstanceOf[Option[DomainName]],
          domainNames = values(7).asInstanceOf[Iterable[DomainName]],
          launcher = values(8).asInstanceOf[Launcher],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): ApplicationDescriptor = {
        val value =
          ApplicationDescriptor(
            name = values.next().asInstanceOf[ApplicationName],
            install = values.next().asInstanceOf[Install],
            caddyConfig = values.next().asInstanceOf[Option[String]],
            listenPort = values.next().asInstanceOf[Option[ListenPort]],
            stopServerCommand = values.next().asInstanceOf[Option[Command]],
            startServerCommand = values.next().asInstanceOf[Option[Command]],
            domainName = values.next().asInstanceOf[Option[DomainName]],
            domainNames = values.next().asInstanceOf[Iterable[DomainName]],
            launcher = values.next().asInstanceOf[Launcher],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(name: ApplicationName, install: Install, caddyConfig: Option[String], listenPort: Option[ListenPort], stopServerCommand: Option[Command], startServerCommand: Option[Command], domainName: Option[DomainName], domainNames: Iterable[DomainName], launcher: Launcher): ApplicationDescriptor =
        ApplicationDescriptor(name, install, caddyConfig, listenPort, stopServerCommand, startServerCommand, domainName, domainNames, launcher)
    
    }
    
    
    lazy val typeName = "ApplicationDescriptor"
  
  }
  
  
  
  
  trait MxUserDescriptor {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[UserDescriptor,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[UserDescriptor,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[UserDescriptor,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.login)
          .addField(_.aliases)
          .addField(_.home)
          .addField(_.authorizedKeys)
          .addField(_.a8VersionsExec)
          .addField(_.manageSshKeys)
          .addField(_.appInstallDirectory)
          .addField(_.plugins)
      )
      .build
    
    implicit val zioEq: zio.prelude.Equal[UserDescriptor] = zio.prelude.Equal.default
    
    implicit val catsEq: cats.Eq[UserDescriptor] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[UserDescriptor,parameters.type] =  {
      val constructors = Constructors[UserDescriptor](8, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val login: CaseClassParm[UserDescriptor,UserLogin] = CaseClassParm[UserDescriptor,UserLogin]("login", _.login, (d,v) => d.copy(login = v), None, 0)
      lazy val aliases: CaseClassParm[UserDescriptor,Vector[QualifiedUserName]] = CaseClassParm[UserDescriptor,Vector[QualifiedUserName]]("aliases", _.aliases, (d,v) => d.copy(aliases = v), Some(()=> Vector.empty), 1)
      lazy val home: CaseClassParm[UserDescriptor,Option[Directory]] = CaseClassParm[UserDescriptor,Option[Directory]]("home", _.home, (d,v) => d.copy(home = v), Some(()=> None), 2)
      lazy val authorizedKeys: CaseClassParm[UserDescriptor,Vector[QualifiedUserName]] = CaseClassParm[UserDescriptor,Vector[QualifiedUserName]]("authorizedKeys", _.authorizedKeys, (d,v) => d.copy(authorizedKeys = v), Some(()=> Vector.empty), 3)
      lazy val a8VersionsExec: CaseClassParm[UserDescriptor,Option[String]] = CaseClassParm[UserDescriptor,Option[String]]("a8VersionsExec", _.a8VersionsExec, (d,v) => d.copy(a8VersionsExec = v), Some(()=> None), 4)
      lazy val manageSshKeys: CaseClassParm[UserDescriptor,Boolean] = CaseClassParm[UserDescriptor,Boolean]("manageSshKeys", _.manageSshKeys, (d,v) => d.copy(manageSshKeys = v), Some(()=> true), 5)
      lazy val appInstallDirectory: CaseClassParm[UserDescriptor,Option[AppsRootDirectory]] = CaseClassParm[UserDescriptor,Option[AppsRootDirectory]]("appInstallDirectory", _.appInstallDirectory, (d,v) => d.copy(appInstallDirectory = v), Some(()=> None), 6)
      lazy val plugins: CaseClassParm[UserDescriptor,JsDoc] = CaseClassParm[UserDescriptor,JsDoc]("plugins", _.plugins, (d,v) => d.copy(plugins = v), Some(()=> JsDoc.empty), 7)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): UserDescriptor = {
        UserDescriptor(
          login = values(0).asInstanceOf[UserLogin],
          aliases = values(1).asInstanceOf[Vector[QualifiedUserName]],
          home = values(2).asInstanceOf[Option[Directory]],
          authorizedKeys = values(3).asInstanceOf[Vector[QualifiedUserName]],
          a8VersionsExec = values(4).asInstanceOf[Option[String]],
          manageSshKeys = values(5).asInstanceOf[Boolean],
          appInstallDirectory = values(6).asInstanceOf[Option[AppsRootDirectory]],
          plugins = values(7).asInstanceOf[JsDoc],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): UserDescriptor = {
        val value =
          UserDescriptor(
            login = values.next().asInstanceOf[UserLogin],
            aliases = values.next().asInstanceOf[Vector[QualifiedUserName]],
            home = values.next().asInstanceOf[Option[Directory]],
            authorizedKeys = values.next().asInstanceOf[Vector[QualifiedUserName]],
            a8VersionsExec = values.next().asInstanceOf[Option[String]],
            manageSshKeys = values.next().asInstanceOf[Boolean],
            appInstallDirectory = values.next().asInstanceOf[Option[AppsRootDirectory]],
            plugins = values.next().asInstanceOf[JsDoc],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(login: UserLogin, aliases: Vector[QualifiedUserName], home: Option[Directory], authorizedKeys: Vector[QualifiedUserName], a8VersionsExec: Option[String], manageSshKeys: Boolean, appInstallDirectory: Option[AppsRootDirectory], plugins: JsDoc): UserDescriptor =
        UserDescriptor(login, aliases, home, authorizedKeys, a8VersionsExec, manageSshKeys, appInstallDirectory, plugins)
    
    }
    
    
    lazy val typeName = "UserDescriptor"
  
  }
  
  
  
  
  trait MxRSnapshotClientDescriptor {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[RSnapshotClientDescriptor,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[RSnapshotClientDescriptor,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[RSnapshotClientDescriptor,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.name)
          .addField(_.directories)
          .addField(_.runAt)
          .addField(_.hourly)
          .addField(_.includeExcludeLines)
      )
      .build
    
    implicit val zioEq: zio.prelude.Equal[RSnapshotClientDescriptor] = zio.prelude.Equal.default
    
    implicit val catsEq: cats.Eq[RSnapshotClientDescriptor] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[RSnapshotClientDescriptor,parameters.type] =  {
      val constructors = Constructors[RSnapshotClientDescriptor](5, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val name: CaseClassParm[RSnapshotClientDescriptor,String] = CaseClassParm[RSnapshotClientDescriptor,String]("name", _.name, (d,v) => d.copy(name = v), None, 0)
      lazy val directories: CaseClassParm[RSnapshotClientDescriptor,Vector[String]] = CaseClassParm[RSnapshotClientDescriptor,Vector[String]]("directories", _.directories, (d,v) => d.copy(directories = v), None, 1)
      lazy val runAt: CaseClassParm[RSnapshotClientDescriptor,OnCalendarValue] = CaseClassParm[RSnapshotClientDescriptor,OnCalendarValue]("runAt", _.runAt, (d,v) => d.copy(runAt = v), None, 2)
      lazy val hourly: CaseClassParm[RSnapshotClientDescriptor,Boolean] = CaseClassParm[RSnapshotClientDescriptor,Boolean]("hourly", _.hourly, (d,v) => d.copy(hourly = v), Some(()=> false), 3)
      lazy val includeExcludeLines: CaseClassParm[RSnapshotClientDescriptor,Iterable[String]] = CaseClassParm[RSnapshotClientDescriptor,Iterable[String]]("includeExcludeLines", _.includeExcludeLines, (d,v) => d.copy(includeExcludeLines = v), Some(()=> Iterable.empty), 4)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): RSnapshotClientDescriptor = {
        RSnapshotClientDescriptor(
          name = values(0).asInstanceOf[String],
          directories = values(1).asInstanceOf[Vector[String]],
          runAt = values(2).asInstanceOf[OnCalendarValue],
          hourly = values(3).asInstanceOf[Boolean],
          includeExcludeLines = values(4).asInstanceOf[Iterable[String]],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): RSnapshotClientDescriptor = {
        val value =
          RSnapshotClientDescriptor(
            name = values.next().asInstanceOf[String],
            directories = values.next().asInstanceOf[Vector[String]],
            runAt = values.next().asInstanceOf[OnCalendarValue],
            hourly = values.next().asInstanceOf[Boolean],
            includeExcludeLines = values.next().asInstanceOf[Iterable[String]],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(name: String, directories: Vector[String], runAt: OnCalendarValue, hourly: Boolean, includeExcludeLines: Iterable[String]): RSnapshotClientDescriptor =
        RSnapshotClientDescriptor(name, directories, runAt, hourly, includeExcludeLines)
    
    }
    
    
    lazy val typeName = "RSnapshotClientDescriptor"
  
  }
  
  
  
  
  trait MxRSnapshotServerDescriptor {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[RSnapshotServerDescriptor,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[RSnapshotServerDescriptor,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[RSnapshotServerDescriptor,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.name)
          .addField(_.snapshotRootDir)
          .addField(_.configDir)
          .addField(_.logDir)
          .addField(_.runDir)
      )
      .build
    
    implicit val zioEq: zio.prelude.Equal[RSnapshotServerDescriptor] = zio.prelude.Equal.default
    
    implicit val catsEq: cats.Eq[RSnapshotServerDescriptor] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[RSnapshotServerDescriptor,parameters.type] =  {
      val constructors = Constructors[RSnapshotServerDescriptor](5, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val name: CaseClassParm[RSnapshotServerDescriptor,String] = CaseClassParm[RSnapshotServerDescriptor,String]("name", _.name, (d,v) => d.copy(name = v), None, 0)
      lazy val snapshotRootDir: CaseClassParm[RSnapshotServerDescriptor,RSnapshotRootDirectory] = CaseClassParm[RSnapshotServerDescriptor,RSnapshotRootDirectory]("snapshotRootDir", _.snapshotRootDir, (d,v) => d.copy(snapshotRootDir = v), None, 1)
      lazy val configDir: CaseClassParm[RSnapshotServerDescriptor,RSnapshotConfigDirectory] = CaseClassParm[RSnapshotServerDescriptor,RSnapshotConfigDirectory]("configDir", _.configDir, (d,v) => d.copy(configDir = v), None, 2)
      lazy val logDir: CaseClassParm[RSnapshotServerDescriptor,String] = CaseClassParm[RSnapshotServerDescriptor,String]("logDir", _.logDir, (d,v) => d.copy(logDir = v), Some(()=> "/var/log"), 3)
      lazy val runDir: CaseClassParm[RSnapshotServerDescriptor,String] = CaseClassParm[RSnapshotServerDescriptor,String]("runDir", _.runDir, (d,v) => d.copy(runDir = v), Some(()=> "/var/run"), 4)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): RSnapshotServerDescriptor = {
        RSnapshotServerDescriptor(
          name = values(0).asInstanceOf[String],
          snapshotRootDir = values(1).asInstanceOf[RSnapshotRootDirectory],
          configDir = values(2).asInstanceOf[RSnapshotConfigDirectory],
          logDir = values(3).asInstanceOf[String],
          runDir = values(4).asInstanceOf[String],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): RSnapshotServerDescriptor = {
        val value =
          RSnapshotServerDescriptor(
            name = values.next().asInstanceOf[String],
            snapshotRootDir = values.next().asInstanceOf[RSnapshotRootDirectory],
            configDir = values.next().asInstanceOf[RSnapshotConfigDirectory],
            logDir = values.next().asInstanceOf[String],
            runDir = values.next().asInstanceOf[String],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(name: String, snapshotRootDir: RSnapshotRootDirectory, configDir: RSnapshotConfigDirectory, logDir: String, runDir: String): RSnapshotServerDescriptor =
        RSnapshotServerDescriptor(name, snapshotRootDir, configDir, logDir, runDir)
    
    }
    
    
    lazy val typeName = "RSnapshotServerDescriptor"
  
  }
  
  
  
  
  trait MxPgbackrestClientDescriptor {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[PgbackrestClientDescriptor,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[PgbackrestClientDescriptor,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[PgbackrestClientDescriptor,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.name)
          .addField(_.pgdata)
          .addField(_.stanzaNameOverride)
          .addField(_.onCalendar)
          .addField(_.configFile)
      )
      .build
    
    implicit val zioEq: zio.prelude.Equal[PgbackrestClientDescriptor] = zio.prelude.Equal.default
    
    implicit val catsEq: cats.Eq[PgbackrestClientDescriptor] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[PgbackrestClientDescriptor,parameters.type] =  {
      val constructors = Constructors[PgbackrestClientDescriptor](5, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val name: CaseClassParm[PgbackrestClientDescriptor,String] = CaseClassParm[PgbackrestClientDescriptor,String]("name", _.name, (d,v) => d.copy(name = v), None, 0)
      lazy val pgdata: CaseClassParm[PgbackrestClientDescriptor,String] = CaseClassParm[PgbackrestClientDescriptor,String]("pgdata", _.pgdata, (d,v) => d.copy(pgdata = v), None, 1)
      lazy val stanzaNameOverride: CaseClassParm[PgbackrestClientDescriptor,Option[String]] = CaseClassParm[PgbackrestClientDescriptor,Option[String]]("stanzaNameOverride", _.stanzaNameOverride, (d,v) => d.copy(stanzaNameOverride = v), Some(()=> None), 2)
      lazy val onCalendar: CaseClassParm[PgbackrestClientDescriptor,Option[OnCalendarValue]] = CaseClassParm[PgbackrestClientDescriptor,Option[OnCalendarValue]]("onCalendar", _.onCalendar, (d,v) => d.copy(onCalendar = v), Some(()=> None), 3)
      lazy val configFile: CaseClassParm[PgbackrestClientDescriptor,Option[String]] = CaseClassParm[PgbackrestClientDescriptor,Option[String]]("configFile", _.configFile, (d,v) => d.copy(configFile = v), Some(()=> None), 4)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): PgbackrestClientDescriptor = {
        PgbackrestClientDescriptor(
          name = values(0).asInstanceOf[String],
          pgdata = values(1).asInstanceOf[String],
          stanzaNameOverride = values(2).asInstanceOf[Option[String]],
          onCalendar = values(3).asInstanceOf[Option[OnCalendarValue]],
          configFile = values(4).asInstanceOf[Option[String]],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): PgbackrestClientDescriptor = {
        val value =
          PgbackrestClientDescriptor(
            name = values.next().asInstanceOf[String],
            pgdata = values.next().asInstanceOf[String],
            stanzaNameOverride = values.next().asInstanceOf[Option[String]],
            onCalendar = values.next().asInstanceOf[Option[OnCalendarValue]],
            configFile = values.next().asInstanceOf[Option[String]],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(name: String, pgdata: String, stanzaNameOverride: Option[String], onCalendar: Option[OnCalendarValue], configFile: Option[String]): PgbackrestClientDescriptor =
        PgbackrestClientDescriptor(name, pgdata, stanzaNameOverride, onCalendar, configFile)
    
    }
    
    
    lazy val typeName = "PgbackrestClientDescriptor"
  
  }
  
  
  
  
  trait MxPgbackrestServerDescriptor {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[PgbackrestServerDescriptor,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[PgbackrestServerDescriptor,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[PgbackrestServerDescriptor,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.name)
          .addField(_.configHeader)
          .addField(_.configFile)
      )
      .build
    
    implicit val zioEq: zio.prelude.Equal[PgbackrestServerDescriptor] = zio.prelude.Equal.default
    
    implicit val catsEq: cats.Eq[PgbackrestServerDescriptor] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[PgbackrestServerDescriptor,parameters.type] =  {
      val constructors = Constructors[PgbackrestServerDescriptor](3, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val name: CaseClassParm[PgbackrestServerDescriptor,String] = CaseClassParm[PgbackrestServerDescriptor,String]("name", _.name, (d,v) => d.copy(name = v), None, 0)
      lazy val configHeader: CaseClassParm[PgbackrestServerDescriptor,String] = CaseClassParm[PgbackrestServerDescriptor,String]("configHeader", _.configHeader, (d,v) => d.copy(configHeader = v), None, 1)
      lazy val configFile: CaseClassParm[PgbackrestServerDescriptor,Option[String]] = CaseClassParm[PgbackrestServerDescriptor,Option[String]]("configFile", _.configFile, (d,v) => d.copy(configFile = v), Some(()=> None), 2)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): PgbackrestServerDescriptor = {
        PgbackrestServerDescriptor(
          name = values(0).asInstanceOf[String],
          configHeader = values(1).asInstanceOf[String],
          configFile = values(2).asInstanceOf[Option[String]],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): PgbackrestServerDescriptor = {
        val value =
          PgbackrestServerDescriptor(
            name = values.next().asInstanceOf[String],
            configHeader = values.next().asInstanceOf[String],
            configFile = values.next().asInstanceOf[Option[String]],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(name: String, configHeader: String, configFile: Option[String]): PgbackrestServerDescriptor =
        PgbackrestServerDescriptor(name, configHeader, configFile)
    
    }
    
    
    lazy val typeName = "PgbackrestServerDescriptor"
  
  }
  
  
  
  
  trait MxServerDescriptor {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[ServerDescriptor,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[ServerDescriptor,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[ServerDescriptor,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.name)
          .addField(_.aliases)
          .addField(_.supervisorDirectory)
          .addField(_.caddyDirectory)
          .addField(_.publicDomainName)
          .addField(_.vpnDomainName)
          .addField(_.users)
          .addField(_.a8VersionsExec)
          .addField(_.supervisorctlExec)
      )
      .build
    
    implicit val zioEq: zio.prelude.Equal[ServerDescriptor] = zio.prelude.Equal.default
    
    implicit val catsEq: cats.Eq[ServerDescriptor] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[ServerDescriptor,parameters.type] =  {
      val constructors = Constructors[ServerDescriptor](9, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val name: CaseClassParm[ServerDescriptor,ServerName] = CaseClassParm[ServerDescriptor,ServerName]("name", _.name, (d,v) => d.copy(name = v), None, 0)
      lazy val aliases: CaseClassParm[ServerDescriptor,Iterable[ServerName]] = CaseClassParm[ServerDescriptor,Iterable[ServerName]]("aliases", _.aliases, (d,v) => d.copy(aliases = v), Some(()=> Iterable.empty), 1)
      lazy val supervisorDirectory: CaseClassParm[ServerDescriptor,SupervisorDirectory] = CaseClassParm[ServerDescriptor,SupervisorDirectory]("supervisorDirectory", _.supervisorDirectory, (d,v) => d.copy(supervisorDirectory = v), None, 2)
      lazy val caddyDirectory: CaseClassParm[ServerDescriptor,CaddyDirectory] = CaseClassParm[ServerDescriptor,CaddyDirectory]("caddyDirectory", _.caddyDirectory, (d,v) => d.copy(caddyDirectory = v), None, 3)
      lazy val publicDomainName: CaseClassParm[ServerDescriptor,Option[DomainName]] = CaseClassParm[ServerDescriptor,Option[DomainName]]("publicDomainName", _.publicDomainName, (d,v) => d.copy(publicDomainName = v), Some(()=> None), 4)
      lazy val vpnDomainName: CaseClassParm[ServerDescriptor,DomainName] = CaseClassParm[ServerDescriptor,DomainName]("vpnDomainName", _.vpnDomainName, (d,v) => d.copy(vpnDomainName = v), None, 5)
      lazy val users: CaseClassParm[ServerDescriptor,Vector[UserDescriptor]] = CaseClassParm[ServerDescriptor,Vector[UserDescriptor]]("users", _.users, (d,v) => d.copy(users = v), None, 6)
      lazy val a8VersionsExec: CaseClassParm[ServerDescriptor,Option[String]] = CaseClassParm[ServerDescriptor,Option[String]]("a8VersionsExec", _.a8VersionsExec, (d,v) => d.copy(a8VersionsExec = v), Some(()=> None), 7)
      lazy val supervisorctlExec: CaseClassParm[ServerDescriptor,Option[String]] = CaseClassParm[ServerDescriptor,Option[String]]("supervisorctlExec", _.supervisorctlExec, (d,v) => d.copy(supervisorctlExec = v), Some(()=> None), 8)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): ServerDescriptor = {
        ServerDescriptor(
          name = values(0).asInstanceOf[ServerName],
          aliases = values(1).asInstanceOf[Iterable[ServerName]],
          supervisorDirectory = values(2).asInstanceOf[SupervisorDirectory],
          caddyDirectory = values(3).asInstanceOf[CaddyDirectory],
          publicDomainName = values(4).asInstanceOf[Option[DomainName]],
          vpnDomainName = values(5).asInstanceOf[DomainName],
          users = values(6).asInstanceOf[Vector[UserDescriptor]],
          a8VersionsExec = values(7).asInstanceOf[Option[String]],
          supervisorctlExec = values(8).asInstanceOf[Option[String]],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): ServerDescriptor = {
        val value =
          ServerDescriptor(
            name = values.next().asInstanceOf[ServerName],
            aliases = values.next().asInstanceOf[Iterable[ServerName]],
            supervisorDirectory = values.next().asInstanceOf[SupervisorDirectory],
            caddyDirectory = values.next().asInstanceOf[CaddyDirectory],
            publicDomainName = values.next().asInstanceOf[Option[DomainName]],
            vpnDomainName = values.next().asInstanceOf[DomainName],
            users = values.next().asInstanceOf[Vector[UserDescriptor]],
            a8VersionsExec = values.next().asInstanceOf[Option[String]],
            supervisorctlExec = values.next().asInstanceOf[Option[String]],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(name: ServerName, aliases: Iterable[ServerName], supervisorDirectory: SupervisorDirectory, caddyDirectory: CaddyDirectory, publicDomainName: Option[DomainName], vpnDomainName: DomainName, users: Vector[UserDescriptor], a8VersionsExec: Option[String], supervisorctlExec: Option[String]): ServerDescriptor =
        ServerDescriptor(name, aliases, supervisorDirectory, caddyDirectory, publicDomainName, vpnDomainName, users, a8VersionsExec, supervisorctlExec)
    
    }
    
    
    lazy val typeName = "ServerDescriptor"
  
  }
  
  
  
  
  trait MxRepositoryDescriptor {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[RepositoryDescriptor,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[RepositoryDescriptor,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[RepositoryDescriptor,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.publicKeys)
          .addField(_.servers)
          .addField(_.healthchecksApiToken)
      )
      .build
    
    implicit val zioEq: zio.prelude.Equal[RepositoryDescriptor] = zio.prelude.Equal.default
    
    implicit val catsEq: cats.Eq[RepositoryDescriptor] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[RepositoryDescriptor,parameters.type] =  {
      val constructors = Constructors[RepositoryDescriptor](3, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val publicKeys: CaseClassParm[RepositoryDescriptor,Iterable[Personnel]] = CaseClassParm[RepositoryDescriptor,Iterable[Personnel]]("publicKeys", _.publicKeys, (d,v) => d.copy(publicKeys = v), Some(()=> Iterable.empty), 0)
      lazy val servers: CaseClassParm[RepositoryDescriptor,Vector[ServerDescriptor]] = CaseClassParm[RepositoryDescriptor,Vector[ServerDescriptor]]("servers", _.servers, (d,v) => d.copy(servers = v), None, 1)
      lazy val healthchecksApiToken: CaseClassParm[RepositoryDescriptor,HealthchecksDotIo.ApiAuthToken] = CaseClassParm[RepositoryDescriptor,HealthchecksDotIo.ApiAuthToken]("healthchecksApiToken", _.healthchecksApiToken, (d,v) => d.copy(healthchecksApiToken = v), None, 2)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): RepositoryDescriptor = {
        RepositoryDescriptor(
          publicKeys = values(0).asInstanceOf[Iterable[Personnel]],
          servers = values(1).asInstanceOf[Vector[ServerDescriptor]],
          healthchecksApiToken = values(2).asInstanceOf[HealthchecksDotIo.ApiAuthToken],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): RepositoryDescriptor = {
        val value =
          RepositoryDescriptor(
            publicKeys = values.next().asInstanceOf[Iterable[Personnel]],
            servers = values.next().asInstanceOf[Vector[ServerDescriptor]],
            healthchecksApiToken = values.next().asInstanceOf[HealthchecksDotIo.ApiAuthToken],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(publicKeys: Iterable[Personnel], servers: Vector[ServerDescriptor], healthchecksApiToken: HealthchecksDotIo.ApiAuthToken): RepositoryDescriptor =
        RepositoryDescriptor(publicKeys, servers, healthchecksApiToken)
    
    }
    
    
    lazy val typeName = "RepositoryDescriptor"
  
  }
  
  
  
  
  trait MxPersonnel {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[Personnel,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[Personnel,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[Personnel,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.id)
          .addField(_.description)
          .addField(_.authorizedKeysUrl)
          .addField(_.authorizedKeys)
          .addField(_.members)
      )
      .build
    
    implicit val zioEq: zio.prelude.Equal[Personnel] = zio.prelude.Equal.default
    
    implicit val catsEq: cats.Eq[Personnel] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[Personnel,parameters.type] =  {
      val constructors = Constructors[Personnel](5, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val id: CaseClassParm[Personnel,QualifiedUserName] = CaseClassParm[Personnel,QualifiedUserName]("id", _.id, (d,v) => d.copy(id = v), None, 0)
      lazy val description: CaseClassParm[Personnel,String] = CaseClassParm[Personnel,String]("description", _.description, (d,v) => d.copy(description = v), None, 1)
      lazy val authorizedKeysUrl: CaseClassParm[Personnel,Option[String]] = CaseClassParm[Personnel,Option[String]]("authorizedKeysUrl", _.authorizedKeysUrl, (d,v) => d.copy(authorizedKeysUrl = v), Some(()=> None), 2)
      lazy val authorizedKeys: CaseClassParm[Personnel,Iterable[AuthorizedKey]] = CaseClassParm[Personnel,Iterable[AuthorizedKey]]("authorizedKeys", _.authorizedKeys, (d,v) => d.copy(authorizedKeys = v), Some(()=> None), 3)
      lazy val members: CaseClassParm[Personnel,Iterable[QualifiedUserName]] = CaseClassParm[Personnel,Iterable[QualifiedUserName]]("members", _.members, (d,v) => d.copy(members = v), Some(()=> Iterable.empty), 4)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): Personnel = {
        Personnel(
          id = values(0).asInstanceOf[QualifiedUserName],
          description = values(1).asInstanceOf[String],
          authorizedKeysUrl = values(2).asInstanceOf[Option[String]],
          authorizedKeys = values(3).asInstanceOf[Iterable[AuthorizedKey]],
          members = values(4).asInstanceOf[Iterable[QualifiedUserName]],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): Personnel = {
        val value =
          Personnel(
            id = values.next().asInstanceOf[QualifiedUserName],
            description = values.next().asInstanceOf[String],
            authorizedKeysUrl = values.next().asInstanceOf[Option[String]],
            authorizedKeys = values.next().asInstanceOf[Iterable[AuthorizedKey]],
            members = values.next().asInstanceOf[Iterable[QualifiedUserName]],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(id: QualifiedUserName, description: String, authorizedKeysUrl: Option[String], authorizedKeys: Iterable[AuthorizedKey], members: Iterable[QualifiedUserName]): Personnel =
        Personnel(id, description, authorizedKeysUrl, authorizedKeys, members)
    
    }
    
    
    lazy val typeName = "Personnel"
  
  }
}
