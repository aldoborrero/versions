package io.accur8.neodeploy.systemstate

/**

  WARNING THIS IS GENERATED CODE.  DO NOT EDIT.

  The only manually maintained code is the code between the //==== (normally where you add your imports)

*/

//====
import io.accur8.neodeploy.Sync.SyncName
import io.accur8.neodeploy.systemstate.SystemStateModel._
//====

import a8.shared.Meta.{CaseClassParm, Generator, Constructors}



object MxSystemStateModel {
  
  trait MxPreviousState {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[PreviousState,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[PreviousState,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[PreviousState,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.resolvedName)
          .addField(_.syncName)
          .addField(_.value)
      )
      .build
    
    implicit val zioEq: zio.prelude.Equal[PreviousState] = zio.prelude.Equal.default
    
    implicit val catsEq: cats.Eq[PreviousState] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[PreviousState,parameters.type] =  {
      val constructors = Constructors[PreviousState](3, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val resolvedName: CaseClassParm[PreviousState,String] = CaseClassParm[PreviousState,String]("resolvedName", _.resolvedName, (d,v) => d.copy(resolvedName = v), None, 0)
      lazy val syncName: CaseClassParm[PreviousState,SyncName] = CaseClassParm[PreviousState,SyncName]("syncName", _.syncName, (d,v) => d.copy(syncName = v), None, 1)
      lazy val value: CaseClassParm[PreviousState,SystemState] = CaseClassParm[PreviousState,SystemState]("value", _.value, (d,v) => d.copy(value = v), None, 2)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): PreviousState = {
        PreviousState(
          resolvedName = values(0).asInstanceOf[String],
          syncName = values(1).asInstanceOf[SyncName],
          value = values(2).asInstanceOf[SystemState],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): PreviousState = {
        val value =
          PreviousState(
            resolvedName = values.next().asInstanceOf[String],
            syncName = values.next().asInstanceOf[SyncName],
            value = values.next().asInstanceOf[SystemState],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(resolvedName: String, syncName: SyncName, value: SystemState): PreviousState =
        PreviousState(resolvedName, syncName, value)
    
    }
    
    
    lazy val typeName = "PreviousState"
  
  }
}
