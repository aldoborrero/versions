package io.accur8.neodeploy

import a8.shared.Meta.{CaseClassParm, Generator, Constructors}

/**

  WARNING THIS IS GENERATED CODE.  DO NOT EDIT.

  The only manually maintained code is the code between the //==== (normally where you add your imports)

*/

//====
import io.accur8.neodeploy.ConfigFileSync.State

//====


object MxConfigFileSync {
  
  trait MxState {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[State,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[State,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[State,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.filename)
          .addField(_.fileContents)
      )
      .build
    
    implicit val catsEq: cats.Eq[State] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[State,parameters.type] =  {
      val constructors = Constructors[State](2, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val filename: CaseClassParm[State,String] = CaseClassParm[State,String]("filename", _.filename, (d,v) => d.copy(filename = v), None, 0)
      lazy val fileContents: CaseClassParm[State,String] = CaseClassParm[State,String]("fileContents", _.fileContents, (d,v) => d.copy(fileContents = v), None, 1)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): State = {
        State(
          filename = values(0).asInstanceOf[String],
          fileContents = values(1).asInstanceOf[String],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): State = {
        val value =
          State(
            filename = values.next().asInstanceOf[String],
            fileContents = values.next().asInstanceOf[String],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(filename: String, fileContents: String): State =
        State(filename, fileContents)
    
    }
    
    
    lazy val typeName = "State"
  
  }
}
