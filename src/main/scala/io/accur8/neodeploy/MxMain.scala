package io.accur8.neodeploy

/**

  WARNING THIS IS GENERATED CODE.  DO NOT EDIT.

  The only manually maintained code is the code between the //==== (normally where you add your imports)

*/

//====
import io.accur8.neodeploy.SyncServer.Config
import io.accur8.neodeploy.model.{AppsRootDirectory, CaddyDirectory, DomainName, GitServerDirectory, SupervisorDirectory}

//====

import a8.shared.Meta.{CaseClassParm, Generator, Constructors}



object MxMain {
  
  trait MxConfig {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[Config,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[Config,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[Config,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.supervisorDirectory)
          .addField(_.caddyDirectory)
          .addField(_.appsRootDirectory)
          .addField(_.gitServerDirectory)
          .addField(_.serverName)
      )
      .build
    
    implicit val zioEq: zio.prelude.Equal[Config] = zio.prelude.Equal.default
    
    implicit val catsEq: cats.Eq[Config] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[Config,parameters.type] =  {
      val constructors = Constructors[Config](5, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val supervisorDirectory: CaseClassParm[Config,SupervisorDirectory] = CaseClassParm[Config,SupervisorDirectory]("supervisorDirectory", _.supervisorDirectory, (d,v) => d.copy(supervisorDirectory = v), None, 0)
      lazy val caddyDirectory: CaseClassParm[Config,CaddyDirectory] = CaseClassParm[Config,CaddyDirectory]("caddyDirectory", _.caddyDirectory, (d,v) => d.copy(caddyDirectory = v), None, 1)
      lazy val appsRootDirectory: CaseClassParm[Config,AppsRootDirectory] = CaseClassParm[Config,AppsRootDirectory]("appsRootDirectory", _.appsRootDirectory, (d,v) => d.copy(appsRootDirectory = v), None, 2)
      lazy val gitServerDirectory: CaseClassParm[Config,GitServerDirectory] = CaseClassParm[Config,GitServerDirectory]("gitServerDirectory", _.gitServerDirectory, (d,v) => d.copy(gitServerDirectory = v), None, 3)
      lazy val serverName: CaseClassParm[Config,DomainName] = CaseClassParm[Config,DomainName]("serverName", _.serverName, (d,v) => d.copy(serverName = v), None, 4)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): Config = {
        Config(
          supervisorDirectory = values(0).asInstanceOf[SupervisorDirectory],
          caddyDirectory = values(1).asInstanceOf[CaddyDirectory],
          appsRootDirectory = values(2).asInstanceOf[AppsRootDirectory],
          gitServerDirectory = values(3).asInstanceOf[GitServerDirectory],
          serverName = values(4).asInstanceOf[DomainName],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): Config = {
        val value =
          Config(
            supervisorDirectory = values.next().asInstanceOf[SupervisorDirectory],
            caddyDirectory = values.next().asInstanceOf[CaddyDirectory],
            appsRootDirectory = values.next().asInstanceOf[AppsRootDirectory],
            gitServerDirectory = values.next().asInstanceOf[GitServerDirectory],
            serverName = values.next().asInstanceOf[DomainName],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(supervisorDirectory: SupervisorDirectory, caddyDirectory: CaddyDirectory, appsRootDirectory: AppsRootDirectory, gitServerDirectory: GitServerDirectory, serverName: DomainName): Config =
        Config(supervisorDirectory, caddyDirectory, appsRootDirectory, gitServerDirectory, serverName)
    
    }
    
    
    lazy val typeName = "Config"
  
  }
}
