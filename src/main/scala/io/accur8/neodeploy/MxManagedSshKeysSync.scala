package io.accur8.neodeploy

/**

  WARNING THIS IS GENERATED CODE.  DO NOT EDIT.

  The only manually maintained code is the code between the //==== (normally where you add your imports)

*/

//====
import io.accur8.neodeploy.ManagedSshKeysSync.State

//====

import a8.shared.Meta.{CaseClassParm, Generator, Constructors}



object MxManagedSshKeysSync {
  
  trait MxState {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[State,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[State,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[State,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.publicKeyValue)
          .addField(_.publicKeyFile)
          .addField(_.privateKeyFile)
      )
      .build
    
    implicit val catsEq: cats.Eq[State] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[State,parameters.type] =  {
      val constructors = Constructors[State](3, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val publicKeyValue: CaseClassParm[State,String] = CaseClassParm[State,String]("publicKeyValue", _.publicKeyValue, (d,v) => d.copy(publicKeyValue = v), None, 0)
      lazy val publicKeyFile: CaseClassParm[State,String] = CaseClassParm[State,String]("publicKeyFile", _.publicKeyFile, (d,v) => d.copy(publicKeyFile = v), None, 1)
      lazy val privateKeyFile: CaseClassParm[State,String] = CaseClassParm[State,String]("privateKeyFile", _.privateKeyFile, (d,v) => d.copy(privateKeyFile = v), None, 2)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): State = {
        State(
          publicKeyValue = values(0).asInstanceOf[String],
          publicKeyFile = values(1).asInstanceOf[String],
          privateKeyFile = values(2).asInstanceOf[String],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): State = {
        val value =
          State(
            publicKeyValue = values.next().asInstanceOf[String],
            publicKeyFile = values.next().asInstanceOf[String],
            privateKeyFile = values.next().asInstanceOf[String],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(publicKeyValue: String, publicKeyFile: String, privateKeyFile: String): State =
        State(publicKeyValue, publicKeyFile, privateKeyFile)
    
    }
    
    
    lazy val typeName = "State"
  
  }
}
