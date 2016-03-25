package common

import java.sql.{Timestamp,Date,Time}
import org.joda.time.{DateTime,LocalDate,LocalTime,DateTimeZone}
import org.joda.time.format._

class DateHelper {
  def dateTimeToSqlTimestamp: DateTime => Timestamp = { dt => new Timestamp(dt.getMillis) }
  def sqlTimestampToDateTime: Timestamp => DateTime = { ts => new DateTime(ts.getTime) }
  def localDateToSqlDate: LocalDate => Date = { ld => new Date(ld.toDateTimeAtStartOfDay(DateTimeZone.UTC).getMillis) }
  def sqlDateToLocalDate: Date => LocalDate = { d  => new LocalDate(d.getTime) }
  def localTimeToSqlTime: LocalTime => Time = { lt => new Time(lt.toDateTimeToday.getMillis) }
  def sqlTimeToLocalTime: Time => LocalTime = { t  => new LocalTime(t, DateTimeZone.UTC) }
}