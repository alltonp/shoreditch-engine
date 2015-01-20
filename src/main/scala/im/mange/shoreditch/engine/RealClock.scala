package im.mange.shoreditch.engine

import org.joda.time.{DateTime, LocalDateTime, LocalDate}
import org.joda.time.DateTimeZone._

object RealClock extends Clock {
  def localDate = new LocalDate()
  def localDateTime: LocalDateTime = new LocalDateTime()
  def date = new LocalDate(UTC)
  def dateTime = new DateTime(UTC)
}