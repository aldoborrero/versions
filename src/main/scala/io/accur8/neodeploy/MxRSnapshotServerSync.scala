package io.accur8.neodeploy

/**

  WARNING THIS IS GENERATED CODE.  DO NOT EDIT.

  The only manually maintained code is the code between the //==== (normally where you add your imports)

*/

//====
import io.accur8.neodeploy.RSnapshotServerSync.{QualifiedClient, State}
import io.accur8.neodeploy.model.{QualifiedUserName, RSnapshotClientDescriptor, RSnapshotServerDescriptor}

//====

import a8.shared.Meta.{CaseClassParm, Generator, Constructors}



object MxRSnapshotServerSync {
  
  trait MxState {
  
    protected def jsonCodecBuilder(builder: a8.shared.json.JsonObjectCodecBuilder[State,parameters.type]): a8.shared.json.JsonObjectCodecBuilder[State,parameters.type] = builder
    
    implicit lazy val jsonCodec: a8.shared.json.JsonTypedCodec[State,a8.shared.json.ast.JsObj] =
      jsonCodecBuilder(
        a8.shared.json.JsonObjectCodecBuilder(generator)
          .addField(_.clients)
          .addField(_.server)
      )
      .build
    
    implicit val catsEq: cats.Eq[State] = cats.Eq.fromUniversalEquals
    
    lazy val generator: Generator[State,parameters.type] =  {
      val constructors = Constructors[State](2, unsafe.iterRawConstruct)
      Generator(constructors, parameters)
    }
    
    object parameters {
      lazy val clients: CaseClassParm[State,Vector[QualifiedClient]] = CaseClassParm[State,Vector[QualifiedClient]]("clients", _.clients, (d,v) => d.copy(clients = v), None, 0)
      lazy val server: CaseClassParm[State,RSnapshotServerDescriptor] = CaseClassParm[State,RSnapshotServerDescriptor]("server", _.server, (d,v) => d.copy(server = v), None, 1)
    }
    
    
    object unsafe {
    
      def rawConstruct(values: IndexedSeq[Any]): State = {
        State(
          clients = values(0).asInstanceOf[Vector[QualifiedClient]],
          server = values(1).asInstanceOf[RSnapshotServerDescriptor],
        )
      }
      def iterRawConstruct(values: Iterator[Any]): State = {
        val value =
          State(
            clients = values.next().asInstanceOf[Vector[QualifiedClient]],
            server = values.next().asInstanceOf[RSnapshotServerDescriptor],
          )
        if ( values.hasNext )
           sys.error("")
        value
      }
      def typedConstruct(clients: Vector[QualifiedClient], server: RSnapshotServerDescriptor): State =
        State(clients, server)
    
    }
    
    
    lazy val typeName = "State"
  
  }
}
