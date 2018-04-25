package a8.versions

import a8.common.JsonAssist
import com.softwaremill.sttp.HttpURLConnectionBackend
import play.api.libs.json.Writes

import scala.collection.convert.{DecorateAsJava, DecorateAsScala}


object predef extends predef

trait predef
  extends DecorateAsJava
  with DecorateAsScala
  with m3.predef
{

  implicit val backend = HttpURLConnectionBackend()

  def toJsonPrettyStr[A : Writes](a: A): String = {
    // because the json play pretty print is not that good
    val jsonStr = JsonAssist.toJsonStr(a)
    val jv = m3.json.JsonAssist.parseJson(jsonStr)
    m3.json.JsonAssist.prettyPrint(jv)
  }
}
