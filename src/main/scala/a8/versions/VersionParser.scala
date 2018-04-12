package a8.versions

import java.time.{LocalDate, LocalDateTime, LocalTime, Month}

object VersionParser {

  import fastparse.all._


  val Parser = P(
    Digits ~ "." ~ Digits ~ "." ~ Digits ~ BuildInfo.? ~ End
  ).map { case (major, minor, patch, buildInfo) =>
      Version(
        major,
        minor,
        patch,
        buildInfo,
      )
  }

  val BuildInfo =
    P("-" ~ BuildTimestampP ~ "_" ~ Branch)
      .map { case (ts, br) => Version.BuildInfo(ts, br) }

  val BuildTimestampP: P[BuildTimestamp] = P(
    BuildDate ~ "_" ~ BuildTime
  ).map { case (year, month, day, (hour, minute, second)) =>
    BuildTimestamp(year, month, day, hour, minute, second)
  }

  val BuildDate = P(
    Digit4 ~ Digit2 ~ Digit2
  )

  val BuildTime = P(
    Digit2 ~ Digit2 ~ Digit2.?
  )


  val Branch = P(CharsWhile(_.isLetterOrDigit).!)

  val Digits = P(CharIn('0' to '9')).rep(min=1).!.map(_.toInt)


  val Digit2 = P(Digit.rep(min=2,max=2)).!.map(_.toInt)
  val Digit4 = P(Digit.rep(min=4,max=4)).!.map(_.toInt)

  val Digit = P(CharIn('0' to '9'))

}
