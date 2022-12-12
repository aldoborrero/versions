package io.accur8.neodeploy

import a8.shared.json.ast.JsVal
import a8.shared.json.{JsonCodec, JsonReadOptions, JsonTypedCodec, ReadError, ast}

object LazyJsonCodec {

  def apply[A,B <: JsVal](codecThunk: =>JsonTypedCodec[A,B]): JsonTypedCodec[A,B] =
    new JsonTypedCodec[A,B] {

      lazy val initializedCodec = codecThunk

      override def write(a: A): B =
        initializedCodec.write(a)

      override def read(doc: ast.JsDoc)(implicit readOptions: JsonReadOptions): Either[ReadError, A] =
        initializedCodec.read(doc)

    }

}
