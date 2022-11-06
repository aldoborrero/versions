package io.accur8.neodeploy

/**

  WARNING THIS IS GENERATED CODE.  DO NOT EDIT.

  The only manually maintained code is the code between the //==== (normally where you add your imports)

*/

//====
import io.accur8.neodeploy.ApplicationInstallSync.State
import io.accur8.neodeploy.model.ApplicationDescriptor
import io.accur8.neodeploy.model.Install.FromRepo

//====

import a8.shared.Meta.{CaseClassParm, Generator, Constructors}



object MxApplicationInstallSync {
  
  trait MxState {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[State,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[State,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[State,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.appInstallDir)
          .addField(_.fromRepo)
          .addField(_.gitAppDirectory)
          .addField(_.applicationDescriptor)
      )
      .build
    
    implicit val catsEq: cats.Eq[State] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[State,parameters.type] =  {
      val constructors = Constructors[State](4, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val appInstallDir: CaseClassParm[State,String] = CaseClassParm[State,String]("appInstallDir", _.appInstallDir, (d,v) => d.copy(appInstallDir = v), None, 0)
      lazy val fromRepo: CaseClassParm[State,FromRepo] = CaseClassParm[State,FromRepo]("fromRepo", _.fromRepo, (d,v) => d.copy(fromRepo = v), None, 1)
      lazy val gitAppDirectory: CaseClassParm[State,String] = CaseClassParm[State,String]("gitAppDirectory", _.gitAppDirectory, (d,v) => d.copy(gitAppDirectory = v), None, 2)
      lazy val applicationDescriptor: CaseClassParm[State,ApplicationDescriptor] = CaseClassParm[State,ApplicationDescriptor]("applicationDescriptor", _.applicationDescriptor, (d,v) => d.copy(applicationDescriptor = v), None, 3)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): State = {
        State(
          appInstallDir = values(0).asInstanceOf[String],
          fromRepo = values(1).asInstanceOf[FromRepo],
          gitAppDirectory = values(2).asInstanceOf[String],
          applicationDescriptor = values(3).asInstanceOf[ApplicationDescriptor],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): State = {
        val value =
          State(
            appInstallDir = values.next().asInstanceOf[String],
            fromRepo = values.next().asInstanceOf[FromRepo],
            gitAppDirectory = values.next().asInstanceOf[String],
            applicationDescriptor = values.next().asInstanceOf[ApplicationDescriptor],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(appInstallDir: String, fromRepo: FromRepo, gitAppDirectory: String, applicationDescriptor: ApplicationDescriptor): State =
        State(appInstallDir, fromRepo, gitAppDirectory, applicationDescriptor)
    
    }
    
    
    lazy val typeName = "State"
  
  }
}
