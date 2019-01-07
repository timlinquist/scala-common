package org.mulesoft.common.time
import java.time._
import java.util._

import org.mulesoft.common.time.SimpleDateTime.Epoch
import org.scalatest.Matchers

/**
  * Jvm Test
  */
class JvmSimpleDateTimeTest extends SimpleDateTimeTest {
  test("conversions") {
    val epochDate          = new Date(0)
    val zonedDateTimeEpoch = ZonedDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC)
    val epochCalendar      = GregorianCalendar.from(zonedDateTimeEpoch)
    epochCalendar.setTime(epochDate)
    epochCalendar.setTimeZone(new SimpleTimeZone(0, "UTC"))

    Epoch.toInstant shouldBe Instant.EPOCH
    Epoch.toCalendar.getTime shouldBe epochCalendar.getTime
    Epoch.toDate shouldBe epochDate
    Epoch.toLocalDate shouldBe LocalDate.ofEpochDay(0)

    val y2k = SimpleDateTime(2000, 1, 1)

    y2k.toLocalDate shouldBe LocalDate.of(2000, 1, 1)
    y2k.toLocalDateTime shouldBe LocalDateTime.of(2000, 1, 1, 0, 0)

  }

  test("toDate precision") {
    "1970-01-01T00:00:00.1Z" match {
      case SimpleDateTime(s) => s.toDate.getTime should be(100)
    }
  }
}
