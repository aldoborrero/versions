package io.accur8.neodeploy

import a8.shared.Meta.{CaseClassParm, Generator, Constructors}

/**

  WARNING THIS IS GENERATED CODE.  DO NOT EDIT.

  The only manually maintained code is the code between the //==== (normally where you add your imports)

*/

//====
import io.accur8.neodeploy.model._

//====


object Mxmodel {
  
  trait MxSupervisorConfig {
  
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[SupervisorConfig,a8.shared.json.ast.JsObj] =
      a8.shared.json.JsonObjectCodecBuilder(generator)
        .addField(_.jvmArgs)
        .addField(_.autoStart)
        .addField(_.appArgs)
        .addField(_.mainClass)
        .build
    
    implicit val catsEq: cats.Eq[SupervisorConfig] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[SupervisorConfig,parameters.type] =  {
      val constructors = Constructors[SupervisorConfig](4, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val jvmArgs: CaseClassParm[SupervisorConfig,Iterable[String]] = CaseClassParm[SupervisorConfig,Iterable[String]]("jvmArgs", _.jvmArgs, (d,v) => d.copy(jvmArgs = v), Some(()=> None), 0)
      lazy val autoStart: CaseClassParm[SupervisorConfig,Option[Boolean]] = CaseClassParm[SupervisorConfig,Option[Boolean]]("autoStart", _.autoStart, (d,v) => d.copy(autoStart = v), Some(()=> None), 1)
      lazy val appArgs: CaseClassParm[SupervisorConfig,Iterable[String]] = CaseClassParm[SupervisorConfig,Iterable[String]]("appArgs", _.appArgs, (d,v) => d.copy(appArgs = v), Some(()=> Iterable.empty), 2)
      lazy val mainClass: CaseClassParm[SupervisorConfig,String] = CaseClassParm[SupervisorConfig,String]("mainClass", _.mainClass, (d,v) => d.copy(mainClass = v), None, 3)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): SupervisorConfig = {
        SupervisorConfig(
          jvmArgs = values(0).asInstanceOf[Iterable[String]],
          autoStart = values(1).asInstanceOf[Option[Boolean]],
          appArgs = values(2).asInstanceOf[Iterable[String]],
          mainClass = values(3).asInstanceOf[String],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): SupervisorConfig = {
        val value =
          SupervisorConfig(
            jvmArgs = values.next().asInstanceOf[Iterable[String]],
            autoStart = values.next().asInstanceOf[Option[Boolean]],
            appArgs = values.next().asInstanceOf[Iterable[String]],
            mainClass = values.next().asInstanceOf[String],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(jvmArgs: Iterable[String], autoStart: Option[Boolean], appArgs: Iterable[String], mainClass: String): SupervisorConfig =
        SupervisorConfig(jvmArgs, autoStart, appArgs, mainClass)
    
    }
    
    
    lazy val typeName = "SupervisorConfig"
  
  }
  
  
  
  
  trait MxCaddyConfig {
  
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[CaddyConfig,a8.shared.json.ast.JsObj] =
      a8.shared.json.JsonObjectCodecBuilder(generator)
        .addField(_.domainName)
        .build
    
    implicit val catsEq: cats.Eq[CaddyConfig] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[CaddyConfig,parameters.type] =  {
      val constructors = Constructors[CaddyConfig](1, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val domainName: CaseClassParm[CaddyConfig,DomainName] = CaseClassParm[CaddyConfig,DomainName]("domainName", _.domainName, (d,v) => d.copy(domainName = v), None, 0)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): CaddyConfig = {
        CaddyConfig(
          domainName = values(0).asInstanceOf[DomainName],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): CaddyConfig = {
        val value =
          CaddyConfig(
            domainName = values.next().asInstanceOf[DomainName],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(domainName: DomainName): CaddyConfig =
        CaddyConfig(domainName)
    
    }
    
    
    lazy val typeName = "CaddyConfig"
  
  }
  
  
  
  
  trait MxApplicationDescriptor {
  
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[ApplicationDescriptor,a8.shared.json.ast.JsObj] =
      a8.shared.json.JsonObjectCodecBuilder(generator)
        .addField(_.name)
        .addField(_.organization)
        .addField(_.artifact)
        .addField(_.version)
        .addField(_.description)
        .addField(_.listenPort)
        .addField(_.javaVersion)
        .addField(_.supervisorConfig)
        .addField(_.caddyConfig)
        .addField(_.stopServerCommand)
        .addField(_.startServerCommand)
        .addField(_.webappExplode)
        .build
    
    implicit val catsEq: cats.Eq[ApplicationDescriptor] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[ApplicationDescriptor,parameters.type] =  {
      val constructors = Constructors[ApplicationDescriptor](12, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val name: CaseClassParm[ApplicationDescriptor,ApplicationName] = CaseClassParm[ApplicationDescriptor,ApplicationName]("name", _.name, (d,v) => d.copy(name = v), None, 0)
      lazy val organization: CaseClassParm[ApplicationDescriptor,Organization] = CaseClassParm[ApplicationDescriptor,Organization]("organization", _.organization, (d,v) => d.copy(organization = v), None, 1)
      lazy val artifact: CaseClassParm[ApplicationDescriptor,Artifact] = CaseClassParm[ApplicationDescriptor,Artifact]("artifact", _.artifact, (d,v) => d.copy(artifact = v), None, 2)
      lazy val version: CaseClassParm[ApplicationDescriptor,Version] = CaseClassParm[ApplicationDescriptor,Version]("version", _.version, (d,v) => d.copy(version = v), None, 3)
      lazy val description: CaseClassParm[ApplicationDescriptor,Option[String]] = CaseClassParm[ApplicationDescriptor,Option[String]]("description", _.description, (d,v) => d.copy(description = v), Some(()=> None), 4)
      lazy val listenPort: CaseClassParm[ApplicationDescriptor,Option[ListenPort]] = CaseClassParm[ApplicationDescriptor,Option[ListenPort]]("listenPort", _.listenPort, (d,v) => d.copy(listenPort = v), Some(()=> None), 5)
      lazy val javaVersion: CaseClassParm[ApplicationDescriptor,Option[JavaVersion]] = CaseClassParm[ApplicationDescriptor,Option[JavaVersion]]("javaVersion", _.javaVersion, (d,v) => d.copy(javaVersion = v), Some(()=> None), 6)
      lazy val supervisorConfig: CaseClassParm[ApplicationDescriptor,Option[SupervisorConfig]] = CaseClassParm[ApplicationDescriptor,Option[SupervisorConfig]]("supervisorConfig", _.supervisorConfig, (d,v) => d.copy(supervisorConfig = v), Some(()=> None), 7)
      lazy val caddyConfig: CaseClassParm[ApplicationDescriptor,Option[CaddyConfig]] = CaseClassParm[ApplicationDescriptor,Option[CaddyConfig]]("caddyConfig", _.caddyConfig, (d,v) => d.copy(caddyConfig = v), Some(()=> None), 8)
      lazy val stopServerCommand: CaseClassParm[ApplicationDescriptor,Iterable[String]] = CaseClassParm[ApplicationDescriptor,Iterable[String]]("stopServerCommand", _.stopServerCommand, (d,v) => d.copy(stopServerCommand = v), Some(()=> Seq.empty), 9)
      lazy val startServerCommand: CaseClassParm[ApplicationDescriptor,Iterable[String]] = CaseClassParm[ApplicationDescriptor,Iterable[String]]("startServerCommand", _.startServerCommand, (d,v) => d.copy(startServerCommand = v), Some(()=> Seq.empty), 10)
      lazy val webappExplode: CaseClassParm[ApplicationDescriptor,Boolean] = CaseClassParm[ApplicationDescriptor,Boolean]("webappExplode", _.webappExplode, (d,v) => d.copy(webappExplode = v), Some(()=> true), 11)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): ApplicationDescriptor = {
        ApplicationDescriptor(
          name = values(0).asInstanceOf[ApplicationName],
          organization = values(1).asInstanceOf[Organization],
          artifact = values(2).asInstanceOf[Artifact],
          version = values(3).asInstanceOf[Version],
          description = values(4).asInstanceOf[Option[String]],
          listenPort = values(5).asInstanceOf[Option[ListenPort]],
          javaVersion = values(6).asInstanceOf[Option[JavaVersion]],
          supervisorConfig = values(7).asInstanceOf[Option[SupervisorConfig]],
          caddyConfig = values(8).asInstanceOf[Option[CaddyConfig]],
          stopServerCommand = values(9).asInstanceOf[Iterable[String]],
          startServerCommand = values(10).asInstanceOf[Iterable[String]],
          webappExplode = values(11).asInstanceOf[Boolean],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): ApplicationDescriptor = {
        val value =
          ApplicationDescriptor(
            name = values.next().asInstanceOf[ApplicationName],
            organization = values.next().asInstanceOf[Organization],
            artifact = values.next().asInstanceOf[Artifact],
            version = values.next().asInstanceOf[Version],
            description = values.next().asInstanceOf[Option[String]],
            listenPort = values.next().asInstanceOf[Option[ListenPort]],
            javaVersion = values.next().asInstanceOf[Option[JavaVersion]],
            supervisorConfig = values.next().asInstanceOf[Option[SupervisorConfig]],
            caddyConfig = values.next().asInstanceOf[Option[CaddyConfig]],
            stopServerCommand = values.next().asInstanceOf[Iterable[String]],
            startServerCommand = values.next().asInstanceOf[Iterable[String]],
            webappExplode = values.next().asInstanceOf[Boolean],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(name: ApplicationName, organization: Organization, artifact: Artifact, version: Version, description: Option[String], listenPort: Option[ListenPort], javaVersion: Option[JavaVersion], supervisorConfig: Option[SupervisorConfig], caddyConfig: Option[CaddyConfig], stopServerCommand: Iterable[String], startServerCommand: Iterable[String], webappExplode: Boolean): ApplicationDescriptor =
        ApplicationDescriptor(name, organization, artifact, version, description, listenPort, javaVersion, supervisorConfig, caddyConfig, stopServerCommand, startServerCommand, webappExplode)
    
    }
    
    
    lazy val typeName = "ApplicationDescriptor"
  
  }
}
