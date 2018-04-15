package a8.versions


import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import a8.versions.Version.BuildInfo
import fastparse.core.Parsed

import scala.util.Try
import shapeless._
import shapeless.syntax.std.product._


object Version {

  def parse(v: String): Try[Version] = Try {
    VersionParser.Parser.parse(v) match {
      case Parsed.Success(v, _) =>
        v
      case f: Parsed.Failure[_,_] =>
        throw new RuntimeException(s"unable to parse version ${v} -- ${f.msg}")
    }
  }

  implicit val orderingByBuildInfo =
    Ordering.by[BuildInfo,BuildTimestamp](_.buildTimestamp)

  case class BuildInfo(
    buildTimestamp: BuildTimestamp,
    branch: String,
  ) {
    override def toString = s"${buildTimestamp}_${branch}"
  }

  implicit val orderingByMajorMinorPathBuildTimestamp =
    Ordering.by[Version,(Int, Int, Int, Option[BuildInfo])](_.tupled)


}

case class Version(
  major: Int,
  minor: Int,
  patch: Int,
  buildInfo: Option[BuildInfo],
) {

  lazy val tupled: (Int, Int, Int, Option[BuildInfo]) = {
    (major, minor, patch, buildInfo)
  }

  override def toString() =
    s"${major}.${minor}.${patch}${buildInfo.map("-" + _).getOrElse("")}"

}

