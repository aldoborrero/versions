package a8.versions

import com.softwaremill.sttp.HttpURLConnectionBackend

import scala.collection.convert.{DecorateAsJava, DecorateAsScala}


object predef extends predef

trait predef
  extends DecorateAsJava
  with DecorateAsScala
  with m3.predef
{

  implicit val backend = HttpURLConnectionBackend()

}
