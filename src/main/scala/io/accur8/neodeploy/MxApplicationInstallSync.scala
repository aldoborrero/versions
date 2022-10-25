package io.accur8.neodeploy

import a8.shared.Meta.{CaseClassParm, Generator, Constructors}

/**

  WARNING THIS IS GENERATED CODE.  DO NOT EDIT.

  The only manually maintained code is the code between the //==== (normally where you add your imports)

*/

//====
import io.accur8.neodeploy.ApplicationInstallSync.State
import io.accur8.neodeploy.model.Version

//====


object MxApplicationInstallSync {
  
  trait MxState {
  
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[State,a8.shared.json.ast.JsObj] =
      a8.shared.json.JsonObjectCodecBuilder(generator)
        .addField(_.version)
        .build
    
    implicit val catsEq: cats.Eq[State] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[State,parameters.type] =  {
      val constructors = Constructors[State](1, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val version: CaseClassParm[State,Version] = CaseClassParm[State,Version]("version", _.version, (d,v) => d.copy(version = v), None, 0)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): State = {
        State(
          version = values(0).asInstanceOf[Version],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): State = {
        val value =
          State(
            version = values.next().asInstanceOf[Version],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(version: Version): State =
        State(version)
    
    }
    
    
    lazy val typeName = "State"
  
  }
}
