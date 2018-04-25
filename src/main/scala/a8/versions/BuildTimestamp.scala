package a8.versions




object BuildTimestamp {

  val ordering =
    Ordering.by[BuildTimestamp,(Int, Int, Int, Int, Int, Option[Int])](_.tupled)

  def now(): BuildTimestamp = {
    val n = java.time.LocalDateTime.now()
    BuildTimestamp(
      year = n.getYear,
      month = 1+n.getMonth.ordinal,
      day = n.getDayOfMonth,
      hour = n.getHour,
      minute = n.getMinute,
      second = Some(n.getSecond),
    )
  }

}





case class BuildTimestamp(
  year: Int,
  month: Int,
  day: Int,
  hour: Int,
  minute: Int,
  second: Option[Int]
) extends Ordered[BuildTimestamp] {

  lazy val tupled = (year, month, day, hour, minute, second)

  override def compare(that: BuildTimestamp): Int = {
    BuildTimestamp.ordering.compare(this, that)
  }

  override def toString() = {
    f"${year}${month}%02d${day}%02d_${hour}%02d${minute}%02d${second.map(s => f"${s}%02d").getOrElse("")}"
  }

}
