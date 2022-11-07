package io.accur8.neodeploy

import a8.shared.Meta.{CaseClassParm, Generator, Constructors}

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
          .addField(_.user)
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
      val constructors = Constructors[ApplicationDescriptor](15, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val name: CaseClassParm[ApplicationDescriptor,ApplicationName] = CaseClassParm[ApplicationDescriptor,ApplicationName]("name", _.name, (d,v) => d.copy(name = v), None, 0)
      lazy val install: CaseClassParm[ApplicationDescriptor,Install] = CaseClassParm[ApplicationDescriptor,Install]("install", _.install, (d,v) => d.copy(install = v), None, 1)
      lazy val jvmArgs: CaseClassParm[ApplicationDescriptor,Iterable[String]] = CaseClassParm[ApplicationDescriptor,Iterable[String]]("jvmArgs", _.jvmArgs, (d,v) => d.copy(jvmArgs = v), Some(()=> None), 2)
      lazy val autoStart: CaseClassParm[ApplicationDescriptor,Option[Boolean]] = CaseClassParm[ApplicationDescriptor,Option[Boolean]]("autoStart", _.autoStart, (d,v) => d.copy(autoStart = v), Some(()=> None), 3)
      lazy val appArgs: CaseClassParm[ApplicationDescriptor,Iterable[String]] = CaseClassParm[ApplicationDescriptor,Iterable[String]]("appArgs", _.appArgs, (d,v) => d.copy(appArgs = v), Some(()=> Iterable.empty), 4)
      lazy val mainClass: CaseClassParm[ApplicationDescriptor,String] = CaseClassParm[ApplicationDescriptor,String]("mainClass", _.mainClass, (d,v) => d.copy(mainClass = v), None, 5)
      lazy val user: CaseClassParm[ApplicationDescriptor,String] = CaseClassParm[ApplicationDescriptor,String]("user", _.user, (d,v) => d.copy(user = v), Some(()=> "dev"), 6)
      lazy val listenPort: CaseClassParm[ApplicationDescriptor,Option[ListenPort]] = CaseClassParm[ApplicationDescriptor,Option[ListenPort]]("listenPort", _.listenPort, (d,v) => d.copy(listenPort = v), Some(()=> None), 7)
      lazy val javaVersion: CaseClassParm[ApplicationDescriptor,JavaVersion] = CaseClassParm[ApplicationDescriptor,JavaVersion]("javaVersion", _.javaVersion, (d,v) => d.copy(javaVersion = v), Some(()=> JavaVersion(11)), 8)
      lazy val stopServerCommand: CaseClassParm[ApplicationDescriptor,Option[Command]] = CaseClassParm[ApplicationDescriptor,Option[Command]]("stopServerCommand", _.stopServerCommand, (d,v) => d.copy(stopServerCommand = v), Some(()=> None), 9)
      lazy val startServerCommand: CaseClassParm[ApplicationDescriptor,Option[Command]] = CaseClassParm[ApplicationDescriptor,Option[Command]]("startServerCommand", _.startServerCommand, (d,v) => d.copy(startServerCommand = v), Some(()=> None), 10)
      lazy val domainName: CaseClassParm[ApplicationDescriptor,Option[DomainName]] = CaseClassParm[ApplicationDescriptor,Option[DomainName]]("domainName", _.domainName, (d,v) => d.copy(domainName = v), Some(()=> None), 11)
      lazy val domainNames: CaseClassParm[ApplicationDescriptor,Iterable[DomainName]] = CaseClassParm[ApplicationDescriptor,Iterable[DomainName]]("domainNames", _.domainNames, (d,v) => d.copy(domainNames = v), Some(()=> Iterable.empty), 12)
      lazy val trigger: CaseClassParm[ApplicationDescriptor,JsDoc] = CaseClassParm[ApplicationDescriptor,JsDoc]("trigger", _.trigger, (d,v) => d.copy(trigger = v), Some(()=> JsDoc.empty), 13)
      lazy val repository: CaseClassParm[ApplicationDescriptor,Option[RepoConfigPrefix]] = CaseClassParm[ApplicationDescriptor,Option[RepoConfigPrefix]]("repository", _.repository, (d,v) => d.copy(repository = v), Some(()=> None), 14)
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
          user = values(6).asInstanceOf[String],
          listenPort = values(7).asInstanceOf[Option[ListenPort]],
          javaVersion = values(8).asInstanceOf[JavaVersion],
          stopServerCommand = values(9).asInstanceOf[Option[Command]],
          startServerCommand = values(10).asInstanceOf[Option[Command]],
          domainName = values(11).asInstanceOf[Option[DomainName]],
          domainNames = values(12).asInstanceOf[Iterable[DomainName]],
          trigger = values(13).asInstanceOf[JsDoc],
          repository = values(14).asInstanceOf[Option[RepoConfigPrefix]],
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
            user = values.next().asInstanceOf[String],
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
      def typedConstruct(name: ApplicationName, install: Install, jvmArgs: Iterable[String], autoStart: Option[Boolean], appArgs: Iterable[String], mainClass: String, user: String, listenPort: Option[ListenPort], javaVersion: JavaVersion, stopServerCommand: Option[Command], startServerCommand: Option[Command], domainName: Option[DomainName], domainNames: Iterable[DomainName], trigger: JsDoc, repository: Option[RepoConfigPrefix]): ApplicationDescriptor =
        ApplicationDescriptor(name, install, jvmArgs, autoStart, appArgs, mainClass, user, listenPort, javaVersion, stopServerCommand, startServerCommand, domainName, domainNames, trigger, repository)
    
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
          .addField(_.authorizedPersonnel)
      )
      .build
    
    implicit val catsEq: cats.Eq[UserDescriptor] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[UserDescriptor,parameters.type] =  {
      val constructors = Constructors[UserDescriptor](4, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val login: CaseClassParm[UserDescriptor,UserLogin] = CaseClassParm[UserDescriptor,UserLogin]("login", _.login, (d,v) => d.copy(login = v), None, 0)
      lazy val home: CaseClassParm[UserDescriptor,Option[String]] = CaseClassParm[UserDescriptor,Option[String]]("home", _.home, (d,v) => d.copy(home = v), Some(()=> None), 1)
      lazy val authorizedKeys: CaseClassParm[UserDescriptor,Iterable[AuthorizedKey]] = CaseClassParm[UserDescriptor,Iterable[AuthorizedKey]]("authorizedKeys", _.authorizedKeys, (d,v) => d.copy(authorizedKeys = v), Some(()=> Iterable.empty), 2)
      lazy val authorizedPersonnel: CaseClassParm[UserDescriptor,Iterable[PersonnelId]] = CaseClassParm[UserDescriptor,Iterable[PersonnelId]]("authorizedPersonnel", _.authorizedPersonnel, (d,v) => d.copy(authorizedPersonnel = v), Some(()=> Iterable.empty), 3)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): UserDescriptor = {
        UserDescriptor(
          login = values(0).asInstanceOf[UserLogin],
          home = values(1).asInstanceOf[Option[String]],
          authorizedKeys = values(2).asInstanceOf[Iterable[AuthorizedKey]],
          authorizedPersonnel = values(3).asInstanceOf[Iterable[PersonnelId]],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): UserDescriptor = {
        val value =
          UserDescriptor(
            login = values.next().asInstanceOf[UserLogin],
            home = values.next().asInstanceOf[Option[String]],
            authorizedKeys = values.next().asInstanceOf[Iterable[AuthorizedKey]],
            authorizedPersonnel = values.next().asInstanceOf[Iterable[PersonnelId]],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(login: UserLogin, home: Option[String], authorizedKeys: Iterable[AuthorizedKey], authorizedPersonnel: Iterable[PersonnelId]): UserDescriptor =
        UserDescriptor(login, home, authorizedKeys, authorizedPersonnel)
    
    }
    
    
    lazy val typeName = "UserDescriptor"
  
  }
  
  
  
  
  trait MxRSnapshotDescriptor {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[RSnapshotDescriptor,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[RSnapshotDescriptor,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[RSnapshotDescriptor,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.directories)
          .addField(_.runAt)
      )
      .build
    
    implicit val catsEq: cats.Eq[RSnapshotDescriptor] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[RSnapshotDescriptor,parameters.type] =  {
      val constructors = Constructors[RSnapshotDescriptor](2, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val directories: CaseClassParm[RSnapshotDescriptor,Vector[String]] = CaseClassParm[RSnapshotDescriptor,Vector[String]]("directories", _.directories, (d,v) => d.copy(directories = v), None, 0)
      lazy val runAt: CaseClassParm[RSnapshotDescriptor,String] = CaseClassParm[RSnapshotDescriptor,String]("runAt", _.runAt, (d,v) => d.copy(runAt = v), None, 1)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): RSnapshotDescriptor = {
        RSnapshotDescriptor(
          directories = values(0).asInstanceOf[Vector[String]],
          runAt = values(1).asInstanceOf[String],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): RSnapshotDescriptor = {
        val value =
          RSnapshotDescriptor(
            directories = values.next().asInstanceOf[Vector[String]],
            runAt = values.next().asInstanceOf[String],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(directories: Vector[String], runAt: String): RSnapshotDescriptor =
        RSnapshotDescriptor(directories, runAt)
    
    }
    
    
    lazy val typeName = "RSnapshotDescriptor"
  
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
          .addField(_.rsnapshot)
          .addField(_.a8VersionsExec)
          .addField(_.supervisorctlExec)
      )
      .build
    
    implicit val catsEq: cats.Eq[ServerDescriptor] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[ServerDescriptor,parameters.type] =  {
      val constructors = Constructors[ServerDescriptor](9, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val name: CaseClassParm[ServerDescriptor,ServerName] = CaseClassParm[ServerDescriptor,ServerName]("name", _.name, (d,v) => d.copy(name = v), None, 0)
      lazy val appInstallDirectory: CaseClassParm[ServerDescriptor,AppsRootDirectory] = CaseClassParm[ServerDescriptor,AppsRootDirectory]("appInstallDirectory", _.appInstallDirectory, (d,v) => d.copy(appInstallDirectory = v), None, 1)
      lazy val supervisorDirectory: CaseClassParm[ServerDescriptor,SupervisorDirectory] = CaseClassParm[ServerDescriptor,SupervisorDirectory]("supervisorDirectory", _.supervisorDirectory, (d,v) => d.copy(supervisorDirectory = v), None, 2)
      lazy val caddyDirectory: CaseClassParm[ServerDescriptor,CaddyDirectory] = CaseClassParm[ServerDescriptor,CaddyDirectory]("caddyDirectory", _.caddyDirectory, (d,v) => d.copy(caddyDirectory = v), None, 3)
      lazy val serverName: CaseClassParm[ServerDescriptor,DomainName] = CaseClassParm[ServerDescriptor,DomainName]("serverName", _.serverName, (d,v) => d.copy(serverName = v), None, 4)
      lazy val users: CaseClassParm[ServerDescriptor,Iterable[UserDescriptor]] = CaseClassParm[ServerDescriptor,Iterable[UserDescriptor]]("users", _.users, (d,v) => d.copy(users = v), None, 5)
      lazy val rsnapshot: CaseClassParm[ServerDescriptor,Option[RSnapshotDescriptor]] = CaseClassParm[ServerDescriptor,Option[RSnapshotDescriptor]]("rsnapshot", _.rsnapshot, (d,v) => d.copy(rsnapshot = v), Some(()=> None), 6)
      lazy val a8VersionsExec: CaseClassParm[ServerDescriptor,Option[String]] = CaseClassParm[ServerDescriptor,Option[String]]("a8VersionsExec", _.a8VersionsExec, (d,v) => d.copy(a8VersionsExec = v), Some(()=> None), 7)
      lazy val supervisorctlExec: CaseClassParm[ServerDescriptor,Option[String]] = CaseClassParm[ServerDescriptor,Option[String]]("supervisorctlExec", _.supervisorctlExec, (d,v) => d.copy(supervisorctlExec = v), Some(()=> None), 8)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): ServerDescriptor = {
        ServerDescriptor(
          name = values(0).asInstanceOf[ServerName],
          appInstallDirectory = values(1).asInstanceOf[AppsRootDirectory],
          supervisorDirectory = values(2).asInstanceOf[SupervisorDirectory],
          caddyDirectory = values(3).asInstanceOf[CaddyDirectory],
          serverName = values(4).asInstanceOf[DomainName],
          users = values(5).asInstanceOf[Iterable[UserDescriptor]],
          rsnapshot = values(6).asInstanceOf[Option[RSnapshotDescriptor]],
          a8VersionsExec = values(7).asInstanceOf[Option[String]],
          supervisorctlExec = values(8).asInstanceOf[Option[String]],
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
            users = values.next().asInstanceOf[Iterable[UserDescriptor]],
            rsnapshot = values.next().asInstanceOf[Option[RSnapshotDescriptor]],
            a8VersionsExec = values.next().asInstanceOf[Option[String]],
            supervisorctlExec = values.next().asInstanceOf[Option[String]],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(name: ServerName, appInstallDirectory: AppsRootDirectory, supervisorDirectory: SupervisorDirectory, caddyDirectory: CaddyDirectory, serverName: DomainName, users: Iterable[UserDescriptor], rsnapshot: Option[RSnapshotDescriptor], a8VersionsExec: Option[String], supervisorctlExec: Option[String]): ServerDescriptor =
        ServerDescriptor(name, appInstallDirectory, supervisorDirectory, caddyDirectory, serverName, users, rsnapshot, a8VersionsExec, supervisorctlExec)
    
    }
    
    
    lazy val typeName = "ServerDescriptor"
  
  }
  
  
  
  
  trait MxRepositoryDescriptor {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[RepositoryDescriptor,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[RepositoryDescriptor,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[RepositoryDescriptor,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.rsnapshotKey)
          .addField(_.personnel)
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
      lazy val personnel: CaseClassParm[RepositoryDescriptor,Iterable[Personnel]] = CaseClassParm[RepositoryDescriptor,Iterable[Personnel]]("personnel", _.personnel, (d,v) => d.copy(personnel = v), Some(()=> Iterable.empty), 1)
      lazy val servers: CaseClassParm[RepositoryDescriptor,Iterable[ServerDescriptor]] = CaseClassParm[RepositoryDescriptor,Iterable[ServerDescriptor]]("servers", _.servers, (d,v) => d.copy(servers = v), None, 2)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): RepositoryDescriptor = {
        RepositoryDescriptor(
          rsnapshotKey = values(0).asInstanceOf[Option[AuthorizedKey]],
          personnel = values(1).asInstanceOf[Iterable[Personnel]],
          servers = values(2).asInstanceOf[Iterable[ServerDescriptor]],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): RepositoryDescriptor = {
        val value =
          RepositoryDescriptor(
            rsnapshotKey = values.next().asInstanceOf[Option[AuthorizedKey]],
            personnel = values.next().asInstanceOf[Iterable[Personnel]],
            servers = values.next().asInstanceOf[Iterable[ServerDescriptor]],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(rsnapshotKey: Option[AuthorizedKey], personnel: Iterable[Personnel], servers: Iterable[ServerDescriptor]): RepositoryDescriptor =
        RepositoryDescriptor(rsnapshotKey, personnel, servers)
    
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
      lazy val id: CaseClassParm[Personnel,PersonnelId] = CaseClassParm[Personnel,PersonnelId]("id", _.id, (d,v) => d.copy(id = v), None, 0)
      lazy val description: CaseClassParm[Personnel,String] = CaseClassParm[Personnel,String]("description", _.description, (d,v) => d.copy(description = v), None, 1)
      lazy val authorizedKeysUrl: CaseClassParm[Personnel,Option[String]] = CaseClassParm[Personnel,Option[String]]("authorizedKeysUrl", _.authorizedKeysUrl, (d,v) => d.copy(authorizedKeysUrl = v), Some(()=> None), 2)
      lazy val authorizedKeys: CaseClassParm[Personnel,Iterable[AuthorizedKey]] = CaseClassParm[Personnel,Iterable[AuthorizedKey]]("authorizedKeys", _.authorizedKeys, (d,v) => d.copy(authorizedKeys = v), Some(()=> None), 3)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): Personnel = {
        Personnel(
          id = values(0).asInstanceOf[PersonnelId],
          description = values(1).asInstanceOf[String],
          authorizedKeysUrl = values(2).asInstanceOf[Option[String]],
          authorizedKeys = values(3).asInstanceOf[Iterable[AuthorizedKey]],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): Personnel = {
        val value =
          Personnel(
            id = values.next().asInstanceOf[PersonnelId],
            description = values.next().asInstanceOf[String],
            authorizedKeysUrl = values.next().asInstanceOf[Option[String]],
            authorizedKeys = values.next().asInstanceOf[Iterable[AuthorizedKey]],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(id: PersonnelId, description: String, authorizedKeysUrl: Option[String], authorizedKeys: Iterable[AuthorizedKey]): Personnel =
        Personnel(id, description, authorizedKeysUrl, authorizedKeys)
    
    }
    
    
    lazy val typeName = "Personnel"
  
  }
}
