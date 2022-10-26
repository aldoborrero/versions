package io.accur8.neodeploy

import a8.shared.Meta.{CaseClassParm, Constructors, Generator}

/**

  WARNING THIS IS GENERATED CODE.  DO NOT EDIT.

  The only manually maintained code is the code between the //==== (normally where you add your imports)

*/

//====
import a8.shared.json.ast.JsDoc
import io.accur8.neodeploy.model._
import io.accur8.neodeploy.model.Install.{FromRepo, Manual}

//====


object Mxmodel {
  
  trait MxFromRepo {
  
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[FromRepo,a8.shared.json.ast.JsObj] =
      a8.shared.json.JsonObjectCodecBuilder(generator)
        .addField(_.organization)
        .addField(_.artifact)
        .addField(_.version)
        .addField(_.webappExplode)
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
  
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[ApplicationDescriptor,a8.shared.json.ast.JsObj] =
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
        .addField(_.trigger)
        .build
    
    implicit val catsEq: cats.Eq[ApplicationDescriptor] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[ApplicationDescriptor,parameters.type] =  {
      val constructors = Constructors[ApplicationDescriptor](13, unsafe.iterRawConstruct)
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
      lazy val javaVersion: CaseClassParm[ApplicationDescriptor,Option[JavaVersion]] = CaseClassParm[ApplicationDescriptor,Option[JavaVersion]]("javaVersion", _.javaVersion, (d,v) => d.copy(javaVersion = v), Some(()=> None), 8)
      lazy val stopServerCommand: CaseClassParm[ApplicationDescriptor,Option[Command]] = CaseClassParm[ApplicationDescriptor,Option[Command]]("stopServerCommand", _.stopServerCommand, (d,v) => d.copy(stopServerCommand = v), Some(()=> None), 9)
      lazy val startServerCommand: CaseClassParm[ApplicationDescriptor,Option[Command]] = CaseClassParm[ApplicationDescriptor,Option[Command]]("startServerCommand", _.startServerCommand, (d,v) => d.copy(startServerCommand = v), Some(()=> None), 10)
      lazy val domainName: CaseClassParm[ApplicationDescriptor,Option[DomainName]] = CaseClassParm[ApplicationDescriptor,Option[DomainName]]("domainName", _.domainName, (d,v) => d.copy(domainName = v), None, 11)
      lazy val trigger: CaseClassParm[ApplicationDescriptor,JsDoc] = CaseClassParm[ApplicationDescriptor,JsDoc]("trigger", _.trigger, (d,v) => d.copy(trigger = v), Some(()=> JsDoc.empty), 12)
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
          javaVersion = values(8).asInstanceOf[Option[JavaVersion]],
          stopServerCommand = values(9).asInstanceOf[Option[Command]],
          startServerCommand = values(10).asInstanceOf[Option[Command]],
          domainName = values(11).asInstanceOf[Option[DomainName]],
          trigger = values(12).asInstanceOf[JsDoc],
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
            javaVersion = values.next().asInstanceOf[Option[JavaVersion]],
            stopServerCommand = values.next().asInstanceOf[Option[Command]],
            startServerCommand = values.next().asInstanceOf[Option[Command]],
            domainName = values.next().asInstanceOf[Option[DomainName]],
            trigger = values.next().asInstanceOf[JsDoc],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(name: ApplicationName, install: Install, jvmArgs: Iterable[String], autoStart: Option[Boolean], appArgs: Iterable[String], mainClass: String, user: String, listenPort: Option[ListenPort], javaVersion: Option[JavaVersion], stopServerCommand: Option[Command], startServerCommand: Option[Command], domainName: Option[DomainName], trigger: JsDoc): ApplicationDescriptor =
        ApplicationDescriptor(name, install, jvmArgs, autoStart, appArgs, mainClass, user, listenPort, javaVersion, stopServerCommand, startServerCommand, domainName, trigger)
    
    }
    
    
    lazy val typeName = "ApplicationDescriptor"
  
  }
}
