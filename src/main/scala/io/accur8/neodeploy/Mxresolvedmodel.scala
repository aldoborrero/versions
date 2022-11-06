package io.accur8.neodeploy

/**

  WARNING THIS IS GENERATED CODE.  DO NOT EDIT.

  The only manually maintained code is the code between the //==== (normally where you add your imports)

*/

//====
import a8.shared.json.ast.JsDoc
import io.accur8.neodeploy.resolvedmodel.StoredSyncState

//====

import a8.shared.Meta.{CaseClassParm, Generator, Constructors}



object Mxresolvedmodel {
  
  trait MxStoredSyncState {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[StoredSyncState,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[StoredSyncState,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[StoredSyncState,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.name)
          .addField(_.descriptor)
          .addField(_.states)
      )
      .build
    
    implicit val catsEq: cats.Eq[StoredSyncState] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[StoredSyncState,parameters.type] =  {
      val constructors = Constructors[StoredSyncState](3, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val name: CaseClassParm[StoredSyncState,String] = CaseClassParm[StoredSyncState,String]("name", _.name, (d,v) => d.copy(name = v), None, 0)
      lazy val descriptor: CaseClassParm[StoredSyncState,JsDoc] = CaseClassParm[StoredSyncState,JsDoc]("descriptor", _.descriptor, (d,v) => d.copy(descriptor = v), None, 1)
      lazy val states: CaseClassParm[StoredSyncState,JsDoc] = CaseClassParm[StoredSyncState,JsDoc]("states", _.states, (d,v) => d.copy(states = v), None, 2)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): StoredSyncState = {
        StoredSyncState(
          name = values(0).asInstanceOf[String],
          descriptor = values(1).asInstanceOf[JsDoc],
          states = values(2).asInstanceOf[JsDoc],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): StoredSyncState = {
        val value =
          StoredSyncState(
            name = values.next().asInstanceOf[String],
            descriptor = values.next().asInstanceOf[JsDoc],
            states = values.next().asInstanceOf[JsDoc],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(name: String, descriptor: JsDoc, states: JsDoc): StoredSyncState =
        StoredSyncState(name, descriptor, states)
    
    }
    
    
    lazy val typeName = "StoredSyncState"
  
  }
}
