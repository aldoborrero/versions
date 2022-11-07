package io.accur8.neodeploy

import a8.shared.Meta.{CaseClassParm, Generator, Constructors}

/**

  WARNING THIS IS GENERATED CODE.  DO NOT EDIT.

  The only manually maintained code is the code between the //==== (normally where you add your imports)

*/

//====
import io.accur8.neodeploy.Main.Config
import io.accur8.neodeploy.model.{GitRootDirectory, ServerName}

//====


object MxMain {
  
  trait MxConfig {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[Config,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[Config,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[Config,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.gitRootDirectory)
          .addField(_.serverName)
      )
      .build
    
    implicit val catsEq: cats.Eq[Config] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[Config,parameters.type] =  {
      val constructors = Constructors[Config](2, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val gitRootDirectory: CaseClassParm[Config,GitRootDirectory] = CaseClassParm[Config,GitRootDirectory]("gitRootDirectory", _.gitRootDirectory, (d,v) => d.copy(gitRootDirectory = v), None, 0)
      lazy val serverName: CaseClassParm[Config,ServerName] = CaseClassParm[Config,ServerName]("serverName", _.serverName, (d,v) => d.copy(serverName = v), None, 1)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): Config = {
        Config(
          gitRootDirectory = values(0).asInstanceOf[GitRootDirectory],
          serverName = values(1).asInstanceOf[ServerName],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): Config = {
        val value =
          Config(
            gitRootDirectory = values.next().asInstanceOf[GitRootDirectory],
            serverName = values.next().asInstanceOf[ServerName],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(gitRootDirectory: GitRootDirectory, serverName: ServerName): Config =
        Config(gitRootDirectory, serverName)
    
    }
    
    
    lazy val typeName = "Config"
  
  }
}
