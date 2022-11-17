package io.accur8.neodeploy

/**

  WARNING THIS IS GENERATED CODE.  DO NOT EDIT.

  The only manually maintained code is the code between the //==== (normally where you add your imports)

*/

//====
import a8.shared.FileSystem.Directory
import a8.shared.json.ast.{JsDoc, JsObj, JsVal}
import a8.versions.RepositoryOps.RepoConfigPrefix
import com.softwaremill.sttp.Uri
import io.accur8.neodeploy.model._
import io.accur8.neodeploy.model.Install.{FromRepo, Manual}

//====

import a8.shared.Meta.{CaseClassParm, Generator, Constructors}



object Mxmodel {
  
  trait MxFromRepo {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[FromRepo,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[FromRepo,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[FromRepo,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.organization)
          .addField(_.artifact)
          .addField(_.version)
          .addField(_.webappExplode)
      )
      .build
    
    implicit val catsEq: cats.Eq[FromRepo] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[FromRepo,parameters.type] =  {
      val constructors = Constructors[FromRepo](4, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val organization: CaseClassParm[FromRepo,Organization] = CaseClassParm[FromRepo,Organization]("organization", _.organization, (d,v) => d.copy(organization = v), None, 0)
      lazy val artifact: CaseClassParm[FromRepo,Artifact] = CaseClassParm[FromRepo,Artifact]("artifact", _.artifact, (d,v) => d.copy(artifact = v), None, 1)
      lazy val version: CaseClassParm[FromRepo,Version] = CaseClassParm[FromRepo,Version]("version", _.version, (d,v) => d.copy(version = v), None, 2)
      lazy val webappExplode: CaseClassParm[FromRepo,Boolean] = CaseClassParm[FromRepo,Boolean]("webappExplode", _.webappExplode, (d,v) => d.copy(webappExplode = v), Some(()=> true), 3)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): FromRepo = {
        FromRepo(
          organization = values(0).asInstanceOf[Organization],
          artifact = values(1).asInstanceOf[Artifact],
          version = values(2).asInstanceOf[Version],
          webappExplode = values(3).asInstanceOf[Boolean],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): FromRepo = {
        val value =
          FromRepo(
            organization = values.next().asInstanceOf[Organization],
            artifact = values.next().asInstanceOf[Artifact],
            version = values.next().asInstanceOf[Version],
            webappExplode = values.next().asInstanceOf[Boolean],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(organization: Organization, artifact: Artifact, version: Version, webappExplode: Boolean): FromRepo =
        FromRepo(organization, artifact, version, webappExplode)
    
    }
    
    
    lazy val typeName = "FromRepo"
  
  }
  
  
  
  
  trait MxApplicationDescriptor {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[ApplicationDescriptor,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[ApplicationDescriptor,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[ApplicationDescriptor,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.name)
          .addField(_.install)
          .addField(_.jvmArgs)
          .addField(_.autoStart)
          .addField(_.appArgs)
          .addField(_.mainClass)
          .addField(_.listenPort)
          .addField(_.javaVersion)
          .addField(_.stopServerCommand)
          .addField(_.startServerCommand)
          .addField(_.domainName)
          .addField(_.domainNames)
          .addField(_.trigger)
          .addField(_.repository)
      )
      .build
    
    implicit val catsEq: cats.Eq[ApplicationDescriptor] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[ApplicationDescriptor,parameters.type] =  {
      val constructors = Constructors[ApplicationDescriptor](14, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val name: CaseClassParm[ApplicationDescriptor,ApplicationName] = CaseClassParm[ApplicationDescriptor,ApplicationName]("name", _.name, (d,v) => d.copy(name = v), None, 0)
      lazy val install: CaseClassParm[ApplicationDescriptor,Install] = CaseClassParm[ApplicationDescriptor,Install]("install", _.install, (d,v) => d.copy(install = v), None, 1)
      lazy val jvmArgs: CaseClassParm[ApplicationDescriptor,Iterable[String]] = CaseClassParm[ApplicationDescriptor,Iterable[String]]("jvmArgs", _.jvmArgs, (d,v) => d.copy(jvmArgs = v), Some(()=> None), 2)
      lazy val autoStart: CaseClassParm[ApplicationDescriptor,Option[Boolean]] = CaseClassParm[ApplicationDescriptor,Option[Boolean]]("autoStart", _.autoStart, (d,v) => d.copy(autoStart = v), Some(()=> None), 3)
      lazy val appArgs: CaseClassParm[ApplicationDescriptor,Iterable[String]] = CaseClassParm[ApplicationDescriptor,Iterable[String]]("appArgs", _.appArgs, (d,v) => d.copy(appArgs = v), Some(()=> Iterable.empty), 4)
      lazy val mainClass: CaseClassParm[ApplicationDescriptor,String] = CaseClassParm[ApplicationDescriptor,String]("mainClass", _.mainClass, (d,v) => d.copy(mainClass = v), None, 5)
      lazy val listenPort: CaseClassParm[ApplicationDescriptor,Option[ListenPort]] = CaseClassParm[ApplicationDescriptor,Option[ListenPort]]("listenPort", _.listenPort, (d,v) => d.copy(listenPort = v), Some(()=> None), 6)
      lazy val javaVersion: CaseClassParm[ApplicationDescriptor,JavaVersion] = CaseClassParm[ApplicationDescriptor,JavaVersion]("javaVersion", _.javaVersion, (d,v) => d.copy(javaVersion = v), Some(()=> JavaVersion(11)), 7)
      lazy val stopServerCommand: CaseClassParm[ApplicationDescriptor,Option[Command]] = CaseClassParm[ApplicationDescriptor,Option[Command]]("stopServerCommand", _.stopServerCommand, (d,v) => d.copy(stopServerCommand = v), Some(()=> None), 8)
      lazy val startServerCommand: CaseClassParm[ApplicationDescriptor,Option[Command]] = CaseClassParm[ApplicationDescriptor,Option[Command]]("startServerCommand", _.startServerCommand, (d,v) => d.copy(startServerCommand = v), Some(()=> None), 9)
      lazy val domainName: CaseClassParm[ApplicationDescriptor,Option[DomainName]] = CaseClassParm[ApplicationDescriptor,Option[DomainName]]("domainName", _.domainName, (d,v) => d.copy(domainName = v), Some(()=> None), 10)
      lazy val domainNames: CaseClassParm[ApplicationDescriptor,Iterable[DomainName]] = CaseClassParm[ApplicationDescriptor,Iterable[DomainName]]("domainNames", _.domainNames, (d,v) => d.copy(domainNames = v), Some(()=> Iterable.empty), 11)
      lazy val trigger: CaseClassParm[ApplicationDescriptor,JsDoc] = CaseClassParm[ApplicationDescriptor,JsDoc]("trigger", _.trigger, (d,v) => d.copy(trigger = v), Some(()=> JsDoc.empty), 12)
      lazy val repository: CaseClassParm[ApplicationDescriptor,Option[RepoConfigPrefix]] = CaseClassParm[ApplicationDescriptor,Option[RepoConfigPrefix]]("repository", _.repository, (d,v) => d.copy(repository = v), Some(()=> None), 13)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): ApplicationDescriptor = {
        ApplicationDescriptor(
          name = values(0).asInstanceOf[ApplicationName],
          install = values(1).asInstanceOf[Install],
          jvmArgs = values(2).asInstanceOf[Iterable[String]],
          autoStart = values(3).asInstanceOf[Option[Boolean]],
          appArgs = values(4).asInstanceOf[Iterable[String]],
          mainClass = values(5).asInstanceOf[String],
          listenPort = values(6).asInstanceOf[Option[ListenPort]],
          javaVersion = values(7).asInstanceOf[JavaVersion],
          stopServerCommand = values(8).asInstanceOf[Option[Command]],
          startServerCommand = values(9).asInstanceOf[Option[Command]],
          domainName = values(10).asInstanceOf[Option[DomainName]],
          domainNames = values(11).asInstanceOf[Iterable[DomainName]],
          trigger = values(12).asInstanceOf[JsDoc],
          repository = values(13).asInstanceOf[Option[RepoConfigPrefix]],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): ApplicationDescriptor = {
        val value =
          ApplicationDescriptor(
            name = values.next().asInstanceOf[ApplicationName],
            install = values.next().asInstanceOf[Install],
            jvmArgs = values.next().asInstanceOf[Iterable[String]],
            autoStart = values.next().asInstanceOf[Option[Boolean]],
            appArgs = values.next().asInstanceOf[Iterable[String]],
            mainClass = values.next().asInstanceOf[String],
            listenPort = values.next().asInstanceOf[Option[ListenPort]],
            javaVersion = values.next().asInstanceOf[JavaVersion],
            stopServerCommand = values.next().asInstanceOf[Option[Command]],
            startServerCommand = values.next().asInstanceOf[Option[Command]],
            domainName = values.next().asInstanceOf[Option[DomainName]],
            domainNames = values.next().asInstanceOf[Iterable[DomainName]],
            trigger = values.next().asInstanceOf[JsDoc],
            repository = values.next().asInstanceOf[Option[RepoConfigPrefix]],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(name: ApplicationName, install: Install, jvmArgs: Iterable[String], autoStart: Option[Boolean], appArgs: Iterable[String], mainClass: String, listenPort: Option[ListenPort], javaVersion: JavaVersion, stopServerCommand: Option[Command], startServerCommand: Option[Command], domainName: Option[DomainName], domainNames: Iterable[DomainName], trigger: JsDoc, repository: Option[RepoConfigPrefix]): ApplicationDescriptor =
        ApplicationDescriptor(name, install, jvmArgs, autoStart, appArgs, mainClass, listenPort, javaVersion, stopServerCommand, startServerCommand, domainName, domainNames, trigger, repository)
    
    }
    
    
    lazy val typeName = "ApplicationDescriptor"
  
  }
  
  
  
  
  trait MxUserDescriptor {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[UserDescriptor,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[UserDescriptor,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[UserDescriptor,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.login)
          .addField(_.home)
          .addField(_.authorizedKeys)
          .addField(_.a8VersionsExec)
          .addField(_.manageSshKeys)
      )
      .build
    
    implicit val catsEq: cats.Eq[UserDescriptor] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[UserDescriptor,parameters.type] =  {
      val constructors = Constructors[UserDescriptor](5, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val login: CaseClassParm[UserDescriptor,UserLogin] = CaseClassParm[UserDescriptor,UserLogin]("login", _.login, (d,v) => d.copy(login = v), None, 0)
      lazy val home: CaseClassParm[UserDescriptor,Option[String]] = CaseClassParm[UserDescriptor,Option[String]]("home", _.home, (d,v) => d.copy(home = v), Some(()=> None), 1)
      lazy val authorizedKeys: CaseClassParm[UserDescriptor,Vector[QualifiedUserName]] = CaseClassParm[UserDescriptor,Vector[QualifiedUserName]]("authorizedKeys", _.authorizedKeys, (d,v) => d.copy(authorizedKeys = v), Some(()=> Vector.empty), 2)
      lazy val a8VersionsExec: CaseClassParm[UserDescriptor,Option[String]] = CaseClassParm[UserDescriptor,Option[String]]("a8VersionsExec", _.a8VersionsExec, (d,v) => d.copy(a8VersionsExec = v), Some(()=> None), 3)
      lazy val manageSshKeys: CaseClassParm[UserDescriptor,Boolean] = CaseClassParm[UserDescriptor,Boolean]("manageSshKeys", _.manageSshKeys, (d,v) => d.copy(manageSshKeys = v), Some(()=> true), 4)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): UserDescriptor = {
        UserDescriptor(
          login = values(0).asInstanceOf[UserLogin],
          home = values(1).asInstanceOf[Option[String]],
          authorizedKeys = values(2).asInstanceOf[Vector[QualifiedUserName]],
          a8VersionsExec = values(3).asInstanceOf[Option[String]],
          manageSshKeys = values(4).asInstanceOf[Boolean],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): UserDescriptor = {
        val value =
          UserDescriptor(
            login = values.next().asInstanceOf[UserLogin],
            home = values.next().asInstanceOf[Option[String]],
            authorizedKeys = values.next().asInstanceOf[Vector[QualifiedUserName]],
            a8VersionsExec = values.next().asInstanceOf[Option[String]],
            manageSshKeys = values.next().asInstanceOf[Boolean],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(login: UserLogin, home: Option[String], authorizedKeys: Vector[QualifiedUserName], a8VersionsExec: Option[String], manageSshKeys: Boolean): UserDescriptor =
        UserDescriptor(login, home, authorizedKeys, a8VersionsExec, manageSshKeys)
    
    }
    
    
    lazy val typeName = "UserDescriptor"
  
  }
  
  
  
  
  trait MxRSnapshotClientDescriptor {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[RSnapshotClientDescriptor,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[RSnapshotClientDescriptor,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[RSnapshotClientDescriptor,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.directories)
          .addField(_.runAt)
          .addField(_.hourly)
          .addField(_.user)
          .addField(_.includeExcludeLines)
      )
      .build
    
    implicit val catsEq: cats.Eq[RSnapshotClientDescriptor] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[RSnapshotClientDescriptor,parameters.type] =  {
      val constructors = Constructors[RSnapshotClientDescriptor](5, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val directories: CaseClassParm[RSnapshotClientDescriptor,Vector[String]] = CaseClassParm[RSnapshotClientDescriptor,Vector[String]]("directories", _.directories, (d,v) => d.copy(directories = v), None, 0)
      lazy val runAt: CaseClassParm[RSnapshotClientDescriptor,String] = CaseClassParm[RSnapshotClientDescriptor,String]("runAt", _.runAt, (d,v) => d.copy(runAt = v), None, 1)
      lazy val hourly: CaseClassParm[RSnapshotClientDescriptor,Boolean] = CaseClassParm[RSnapshotClientDescriptor,Boolean]("hourly", _.hourly, (d,v) => d.copy(hourly = v), Some(()=> false), 2)
      lazy val user: CaseClassParm[RSnapshotClientDescriptor,UserLogin] = CaseClassParm[RSnapshotClientDescriptor,UserLogin]("user", _.user, (d,v) => d.copy(user = v), None, 3)
      lazy val includeExcludeLines: CaseClassParm[RSnapshotClientDescriptor,Iterable[String]] = CaseClassParm[RSnapshotClientDescriptor,Iterable[String]]("includeExcludeLines", _.includeExcludeLines, (d,v) => d.copy(includeExcludeLines = v), Some(()=> Iterable.empty), 4)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): RSnapshotClientDescriptor = {
        RSnapshotClientDescriptor(
          directories = values(0).asInstanceOf[Vector[String]],
          runAt = values(1).asInstanceOf[String],
          hourly = values(2).asInstanceOf[Boolean],
          user = values(3).asInstanceOf[UserLogin],
          includeExcludeLines = values(4).asInstanceOf[Iterable[String]],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): RSnapshotClientDescriptor = {
        val value =
          RSnapshotClientDescriptor(
            directories = values.next().asInstanceOf[Vector[String]],
            runAt = values.next().asInstanceOf[String],
            hourly = values.next().asInstanceOf[Boolean],
            user = values.next().asInstanceOf[UserLogin],
            includeExcludeLines = values.next().asInstanceOf[Iterable[String]],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(directories: Vector[String], runAt: String, hourly: Boolean, user: UserLogin, includeExcludeLines: Iterable[String]): RSnapshotClientDescriptor =
        RSnapshotClientDescriptor(directories, runAt, hourly, user, includeExcludeLines)
    
    }
    
    
    lazy val typeName = "RSnapshotClientDescriptor"
  
  }
  
  
  
  
  trait MxRSnapshotServerDescriptor {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[RSnapshotServerDescriptor,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[RSnapshotServerDescriptor,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[RSnapshotServerDescriptor,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.user)
          .addField(_.rsnapshotRootDir)
          .addField(_.rsnapshotConfigDir)
      )
      .build
    
    implicit val catsEq: cats.Eq[RSnapshotServerDescriptor] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[RSnapshotServerDescriptor,parameters.type] =  {
      val constructors = Constructors[RSnapshotServerDescriptor](3, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val user: CaseClassParm[RSnapshotServerDescriptor,UserLogin] = CaseClassParm[RSnapshotServerDescriptor,UserLogin]("user", _.user, (d,v) => d.copy(user = v), None, 0)
      lazy val rsnapshotRootDir: CaseClassParm[RSnapshotServerDescriptor,RSnapshotRootDirectory] = CaseClassParm[RSnapshotServerDescriptor,RSnapshotRootDirectory]("rsnapshotRootDir", _.rsnapshotRootDir, (d,v) => d.copy(rsnapshotRootDir = v), None, 1)
      lazy val rsnapshotConfigDir: CaseClassParm[RSnapshotServerDescriptor,RSnapshotConfigDirectory] = CaseClassParm[RSnapshotServerDescriptor,RSnapshotConfigDirectory]("rsnapshotConfigDir", _.rsnapshotConfigDir, (d,v) => d.copy(rsnapshotConfigDir = v), None, 2)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): RSnapshotServerDescriptor = {
        RSnapshotServerDescriptor(
          user = values(0).asInstanceOf[UserLogin],
          rsnapshotRootDir = values(1).asInstanceOf[RSnapshotRootDirectory],
          rsnapshotConfigDir = values(2).asInstanceOf[RSnapshotConfigDirectory],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): RSnapshotServerDescriptor = {
        val value =
          RSnapshotServerDescriptor(
            user = values.next().asInstanceOf[UserLogin],
            rsnapshotRootDir = values.next().asInstanceOf[RSnapshotRootDirectory],
            rsnapshotConfigDir = values.next().asInstanceOf[RSnapshotConfigDirectory],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(user: UserLogin, rsnapshotRootDir: RSnapshotRootDirectory, rsnapshotConfigDir: RSnapshotConfigDirectory): RSnapshotServerDescriptor =
        RSnapshotServerDescriptor(user, rsnapshotRootDir, rsnapshotConfigDir)
    
    }
    
    
    lazy val typeName = "RSnapshotServerDescriptor"
  
  }
  
  
  
  
  trait MxServerDescriptor {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[ServerDescriptor,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[ServerDescriptor,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[ServerDescriptor,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.name)
          .addField(_.appInstallDirectory)
          .addField(_.supervisorDirectory)
          .addField(_.caddyDirectory)
          .addField(_.serverName)
          .addField(_.users)
          .addField(_.rsnapshotClient)
          .addField(_.a8VersionsExec)
          .addField(_.supervisorctlExec)
          .addField(_.rsnapshotServer)
      )
      .build
    
    implicit val catsEq: cats.Eq[ServerDescriptor] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[ServerDescriptor,parameters.type] =  {
      val constructors = Constructors[ServerDescriptor](10, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val name: CaseClassParm[ServerDescriptor,ServerName] = CaseClassParm[ServerDescriptor,ServerName]("name", _.name, (d,v) => d.copy(name = v), None, 0)
      lazy val appInstallDirectory: CaseClassParm[ServerDescriptor,AppsRootDirectory] = CaseClassParm[ServerDescriptor,AppsRootDirectory]("appInstallDirectory", _.appInstallDirectory, (d,v) => d.copy(appInstallDirectory = v), None, 1)
      lazy val supervisorDirectory: CaseClassParm[ServerDescriptor,SupervisorDirectory] = CaseClassParm[ServerDescriptor,SupervisorDirectory]("supervisorDirectory", _.supervisorDirectory, (d,v) => d.copy(supervisorDirectory = v), None, 2)
      lazy val caddyDirectory: CaseClassParm[ServerDescriptor,CaddyDirectory] = CaseClassParm[ServerDescriptor,CaddyDirectory]("caddyDirectory", _.caddyDirectory, (d,v) => d.copy(caddyDirectory = v), None, 3)
      lazy val serverName: CaseClassParm[ServerDescriptor,DomainName] = CaseClassParm[ServerDescriptor,DomainName]("serverName", _.serverName, (d,v) => d.copy(serverName = v), None, 4)
      lazy val users: CaseClassParm[ServerDescriptor,Vector[UserDescriptor]] = CaseClassParm[ServerDescriptor,Vector[UserDescriptor]]("users", _.users, (d,v) => d.copy(users = v), None, 5)
      lazy val rsnapshotClient: CaseClassParm[ServerDescriptor,Option[RSnapshotClientDescriptor]] = CaseClassParm[ServerDescriptor,Option[RSnapshotClientDescriptor]]("rsnapshotClient", _.rsnapshotClient, (d,v) => d.copy(rsnapshotClient = v), Some(()=> None), 6)
      lazy val a8VersionsExec: CaseClassParm[ServerDescriptor,Option[String]] = CaseClassParm[ServerDescriptor,Option[String]]("a8VersionsExec", _.a8VersionsExec, (d,v) => d.copy(a8VersionsExec = v), Some(()=> None), 7)
      lazy val supervisorctlExec: CaseClassParm[ServerDescriptor,Option[String]] = CaseClassParm[ServerDescriptor,Option[String]]("supervisorctlExec", _.supervisorctlExec, (d,v) => d.copy(supervisorctlExec = v), Some(()=> None), 8)
      lazy val rsnapshotServer: CaseClassParm[ServerDescriptor,Option[RSnapshotServerDescriptor]] = CaseClassParm[ServerDescriptor,Option[RSnapshotServerDescriptor]]("rsnapshotServer", _.rsnapshotServer, (d,v) => d.copy(rsnapshotServer = v), Some(()=> None), 9)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): ServerDescriptor = {
        ServerDescriptor(
          name = values(0).asInstanceOf[ServerName],
          appInstallDirectory = values(1).asInstanceOf[AppsRootDirectory],
          supervisorDirectory = values(2).asInstanceOf[SupervisorDirectory],
          caddyDirectory = values(3).asInstanceOf[CaddyDirectory],
          serverName = values(4).asInstanceOf[DomainName],
          users = values(5).asInstanceOf[Vector[UserDescriptor]],
          rsnapshotClient = values(6).asInstanceOf[Option[RSnapshotClientDescriptor]],
          a8VersionsExec = values(7).asInstanceOf[Option[String]],
          supervisorctlExec = values(8).asInstanceOf[Option[String]],
          rsnapshotServer = values(9).asInstanceOf[Option[RSnapshotServerDescriptor]],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): ServerDescriptor = {
        val value =
          ServerDescriptor(
            name = values.next().asInstanceOf[ServerName],
            appInstallDirectory = values.next().asInstanceOf[AppsRootDirectory],
            supervisorDirectory = values.next().asInstanceOf[SupervisorDirectory],
            caddyDirectory = values.next().asInstanceOf[CaddyDirectory],
            serverName = values.next().asInstanceOf[DomainName],
            users = values.next().asInstanceOf[Vector[UserDescriptor]],
            rsnapshotClient = values.next().asInstanceOf[Option[RSnapshotClientDescriptor]],
            a8VersionsExec = values.next().asInstanceOf[Option[String]],
            supervisorctlExec = values.next().asInstanceOf[Option[String]],
            rsnapshotServer = values.next().asInstanceOf[Option[RSnapshotServerDescriptor]],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(name: ServerName, appInstallDirectory: AppsRootDirectory, supervisorDirectory: SupervisorDirectory, caddyDirectory: CaddyDirectory, serverName: DomainName, users: Vector[UserDescriptor], rsnapshotClient: Option[RSnapshotClientDescriptor], a8VersionsExec: Option[String], supervisorctlExec: Option[String], rsnapshotServer: Option[RSnapshotServerDescriptor]): ServerDescriptor =
        ServerDescriptor(name, appInstallDirectory, supervisorDirectory, caddyDirectory, serverName, users, rsnapshotClient, a8VersionsExec, supervisorctlExec, rsnapshotServer)
    
    }
    
    
    lazy val typeName = "ServerDescriptor"
  
  }
  
  
  
  
  trait MxRepositoryDescriptor {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[RepositoryDescriptor,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[RepositoryDescriptor,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[RepositoryDescriptor,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.rsnapshotKey)
          .addField(_.publicKeys)
          .addField(_.servers)
      )
      .build
    
    implicit val catsEq: cats.Eq[RepositoryDescriptor] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[RepositoryDescriptor,parameters.type] =  {
      val constructors = Constructors[RepositoryDescriptor](3, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val rsnapshotKey: CaseClassParm[RepositoryDescriptor,Option[AuthorizedKey]] = CaseClassParm[RepositoryDescriptor,Option[AuthorizedKey]]("rsnapshotKey", _.rsnapshotKey, (d,v) => d.copy(rsnapshotKey = v), Some(()=> None), 0)
      lazy val publicKeys: CaseClassParm[RepositoryDescriptor,Iterable[Personnel]] = CaseClassParm[RepositoryDescriptor,Iterable[Personnel]]("publicKeys", _.publicKeys, (d,v) => d.copy(publicKeys = v), Some(()=> Iterable.empty), 1)
      lazy val servers: CaseClassParm[RepositoryDescriptor,Vector[ServerDescriptor]] = CaseClassParm[RepositoryDescriptor,Vector[ServerDescriptor]]("servers", _.servers, (d,v) => d.copy(servers = v), None, 2)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): RepositoryDescriptor = {
        RepositoryDescriptor(
          rsnapshotKey = values(0).asInstanceOf[Option[AuthorizedKey]],
          publicKeys = values(1).asInstanceOf[Iterable[Personnel]],
          servers = values(2).asInstanceOf[Vector[ServerDescriptor]],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): RepositoryDescriptor = {
        val value =
          RepositoryDescriptor(
            rsnapshotKey = values.next().asInstanceOf[Option[AuthorizedKey]],
            publicKeys = values.next().asInstanceOf[Iterable[Personnel]],
            servers = values.next().asInstanceOf[Vector[ServerDescriptor]],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(rsnapshotKey: Option[AuthorizedKey], publicKeys: Iterable[Personnel], servers: Vector[ServerDescriptor]): RepositoryDescriptor =
        RepositoryDescriptor(rsnapshotKey, publicKeys, servers)
    
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
      )
      .build
    
    implicit val catsEq: cats.Eq[Personnel] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[Personnel,parameters.type] =  {
      val constructors = Constructors[Personnel](4, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val id: CaseClassParm[Personnel,QualifiedUserName] = CaseClassParm[Personnel,QualifiedUserName]("id", _.id, (d,v) => d.copy(id = v), None, 0)
      lazy val description: CaseClassParm[Personnel,String] = CaseClassParm[Personnel,String]("description", _.description, (d,v) => d.copy(description = v), None, 1)
      lazy val authorizedKeysUrl: CaseClassParm[Personnel,Option[String]] = CaseClassParm[Personnel,Option[String]]("authorizedKeysUrl", _.authorizedKeysUrl, (d,v) => d.copy(authorizedKeysUrl = v), Some(()=> None), 2)
      lazy val authorizedKeys: CaseClassParm[Personnel,Iterable[AuthorizedKey]] = CaseClassParm[Personnel,Iterable[AuthorizedKey]]("authorizedKeys", _.authorizedKeys, (d,v) => d.copy(authorizedKeys = v), Some(()=> None), 3)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): Personnel = {
        Personnel(
          id = values(0).asInstanceOf[QualifiedUserName],
          description = values(1).asInstanceOf[String],
          authorizedKeysUrl = values(2).asInstanceOf[Option[String]],
          authorizedKeys = values(3).asInstanceOf[Iterable[AuthorizedKey]],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): Personnel = {
        val value =
          Personnel(
            id = values.next().asInstanceOf[QualifiedUserName],
            description = values.next().asInstanceOf[String],
            authorizedKeysUrl = values.next().asInstanceOf[Option[String]],
            authorizedKeys = values.next().asInstanceOf[Iterable[AuthorizedKey]],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(id: QualifiedUserName, description: String, authorizedKeysUrl: Option[String], authorizedKeys: Iterable[AuthorizedKey]): Personnel =
        Personnel(id, description, authorizedKeysUrl, authorizedKeys)
    
    }
    
    
    lazy val typeName = "Personnel"
  
  }
}
