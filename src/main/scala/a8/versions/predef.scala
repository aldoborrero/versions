package a8.versions

import com.softwaremill.sttp.HttpURLConnectionBackend
import wvlet.log.Logger

import scala.collection.convert.{AsJavaExtensions, AsScalaExtensions}

object predef extends predef

trait predef
  extends AsJavaExtensions
  with AsScalaExtensions
{

  implicit val backend = HttpURLConnectionBackend()

  type Logging = a8.shared.app.Logging


  type Closable = { def close(): Unit }
  def using[A <: Closable, B](r: => A)(f: A => B)(implicit logger: Logger): B = {
    val resource = r
    try {
      f(resource)
    } finally {
      forceClose(resource)
    }
  }

  def using[A <: Closable, B](l:List[A])(f: => B)(implicit logger: Logger): B = {
    try {
      f
    } finally {
      l.foreach(r=>forceClose(r))
    }
  }

  def forceClose[A <: Closable](closeMe: A)(implicit logger: Logger) =
    try {
      import scala.language.reflectiveCalls
      closeMe.close()
    } catch {
      case th: Throwable => logger.debug(s"swallowing failed close on ${closeMe}", th)
    }

}
