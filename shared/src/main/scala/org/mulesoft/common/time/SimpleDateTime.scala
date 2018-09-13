package org.mulesoft.common.time

import java.lang.Integer.parseInt

import org.mulesoft.common.core._
import org.mulesoft.common.parse.ParseError.formatError
import org.mulesoft.common.parse._
import org.mulesoft.common.time.SimpleDateTime.validateDate
import org.mulesoft.common.time.TimeOfDay.validateTimeOfDay

/**
  * A simple container for DateTime elements that does not depends on the platform (JVM/JS/etc)
  */
case class SimpleDateTime(year: Int,
                          month: Int,
                          day: Int,
                          timeOfDay: Option[TimeOfDay] = None,
                          zoneOffset: Option[Int] = None) {
  validateDate(year, month, day)
}

case class TimeOfDay(hour: Int, minute: Int = 0, second: Int = 0, nano: Int = 0) {
  validateTimeOfDay(hour, minute, second, nano)
}

/**
  * Some utility functions for DateTimes
  */
object SimpleDateTime {
  val ZeroTime              = TimeOfDay(0)
  val Epoch: SimpleDateTime = SimpleDateTime(1970, 1, 1, Some(ZeroTime), Some(0))

  /*
   * Compacted in an ugly strings because the JS backend does not support comments
   */

  /*
      DatePattern

          (\d{4})                  # year
          -(\d\d?)                 # month
          -(\d\d?)                 # day
   */
  private val datePattern = """(\d{4})-(\d\d?)-(\d\d?)"""
  /*
      Time Pattern

          (\d\d?)              # hours
          :(\d\d?)             # minutes
          (?::(\d\d?))?        # optional seconds
          (?:\.(\d{0,9}))?     # optional seconds fraction
   */
  private val timePattern =
    """(\d\d?):(\d\d?)(?::(\d\d?))?(?:\.(\d{0,9}))?"""
  /*
      Time Zone Pattern
          (?:                  # optional Time Zone
            (?:[\ \t]*)?       # optional time zone separation
            (
             Z                 # UTC
            |
              ([-+]\d\d?)      # offset hours
              (?: :(\d\d?) )?  # offset minutes
            )
          )?

   */

  private val timeZonePattern = """(?:(?:[\ \t]*)?(Z|([-+]\d\d?)(?::(\d\d?))?))?"""

  private val timeSeparation = """(?:[Tt]|[\ \t]+)"""

  private val dateRegex        = datePattern.r
  private val dateTimeRegex    = (datePattern + "(?:" + timeSeparation + timePattern + timeZonePattern + ")?").r
  private val partialTimeRegex = timePattern.r
  private val fullTimeRegex    = (timePattern + timeZonePattern).r

  private def toInt(s: String): Int = if (s.isNullOrEmpty) 0 else parseInt(s)

  /** Build from day parts */
  def apply(year: Int, month: Int, day: Int): SimpleDateTime = new SimpleDateTime(year, month, day, None, None)

  /** Build from day and time but not time zone */
  def apply(year: Int, month: Int, day: Int, timeOfDay: TimeOfDay): SimpleDateTime =
    new SimpleDateTime(year, month, day, Some(timeOfDay), None)

  /** Build from day and time parts and zone offset in  minutes */
  def apply(year: Int, month: Int, day: Int, timeOfDay: TimeOfDay, zoneOffset: Int): SimpleDateTime =
    new SimpleDateTime(year, month, day, Some(timeOfDay), Some(zoneOffset * 60))

  /**
    * Unapply Extractor
    */
  def unapply(arg: String): Option[SimpleDateTime] = parse(arg).toOption

  /**
    * Try to convert an String to a Zoned Date Time return None if the String does not match
    * (The syntax is compatible with the Yaml 1.2 timestamp)
    */
  def parse(str: String): Either[ParseError, SimpleDateTime] = str match {
    case dateTimeRegex(year, month, day, hours, minutes, seconds, nanos, z, offsetHours, offsetMinutes) =>
      either(
          SimpleDateTime(
              toInt(year),
              toInt(month),
              toInt(day),
              if (hours == null) None else Some(buildTimeOfDay(hours, minutes, seconds, nanos)),
              buildTimeZone(z, offsetHours, offsetMinutes)
          ))
    case _ =>
      formatError(str)

  }

  /**
    * Try to convert an String to a Date
    */
  def parseDate(str: String): Either[ParseError, SimpleDateTime] = str match {
    case dateRegex(year, month, day) => either(SimpleDateTime(toInt(year), toInt(month), toInt(day)))
    case _                           => formatError(str)
  }

  /**
    * Try to convert an String to a FullTime
    */
  def parseFullTime(str: String): Either[ParseError, (TimeOfDay, Option[Int])] = str match {
    case fullTimeRegex(hours, minutes, seconds, nanos, z, offsetHours, offsetMinutes) =>
      either((buildTimeOfDay(hours, minutes, seconds, nanos), buildTimeZone(z, offsetHours, offsetMinutes)))
    case _ => formatError(str)
  }

  /**
    * Try to convert an String to a partial Time
    */
  def parsePartialTime(str: String): Either[ParseError, TimeOfDay] = str match {
    case partialTimeRegex(hours, minutes, seconds, nanos) => either(buildTimeOfDay(hours, minutes, seconds, nanos))
    case _                                                => formatError(str)
  }

  private def validateDate(year: Int, month: Int, day: Int): Unit = {
    val offending =
      if (year == 0) year
      else if (month < 1 || month > 12) month
      else if (day < 1 || day > 31) day
      else if (day == 31 && (month == 4 || month == 6 || month == 9 || month == 11)) day
      else if (month == 2 && (day > 29 || day == 29 && (year % 4 != 0 || year >= 1600 && year % 400 != 0))) day
      else return
    throw ParseException(RangeError(offending))
  }

  private def buildTimeOfDay(hours: String, minutes: String, seconds: String, nanos: String) =
    TimeOfDay(parseInt(hours),
              parseInt(minutes),
              toInt(seconds),
              if (nanos == null) 0 else parseInt(nanos + "0" * (9 - nanos.length)))

  private def buildTimeZone(z: String, offsetHours: String, offsetMinutes: String) =
    if (z == null) None
    else if (offsetHours == null) Some(0)
    else {
      val oh = toInt(offsetHours)
      val om = toInt(offsetMinutes)
      if (oh < -24 || oh > 24) throw ParseException(RangeError(oh))
      if (om < 0 || om > 60) throw ParseException(RangeError(om))
      Some(oh * 3600 + (if (oh < 0) -om else om) * 60)
    }

  private def either[T](r: => T): Either[ParseError, T] =
    try Right(r)
    catch {
      case ParseException(e) => Left(e)
    }
}

object TimeOfDay {
  private def validateTimeOfDay(hour: Int, minute: Int, second: Int, nano: Int): Unit = {
    val offending =
      if (hour < 0 || hour > 24) hour
      else if (minute < 0 || minute > 60 || hour == 24 && minute > 0) minute
      else if (second < 0 || second > 60 || hour == 24 && second > 0) second
      else if (nano < 0 || nano > 999999999 || hour == 24 && nano > 0) nano
      else return
    throw ParseException(RangeError(offending))
  }
}
