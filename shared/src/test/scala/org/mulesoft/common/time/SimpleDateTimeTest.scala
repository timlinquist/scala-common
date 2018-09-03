package org.mulesoft.common.time

import org.mulesoft.common.time.SimpleDateTime._
import org.scalatest.{FunSuite, Matchers, OptionValues}

/**
  * Check SimpleDateTime
  */
trait SimpleDateTimeTest extends FunSuite with Matchers with OptionValues {
  test("singletons") {
    Epoch.day shouldBe 1
    Epoch.month shouldBe 1
    Epoch.year shouldBe 1970
    Epoch.timeOfDay shouldBe Some(ZeroTime)
    ZeroTime.hour shouldBe 0
    ZeroTime.minute shouldBe 0
    ZeroTime.second shouldBe 0
    ZeroTime.nano shouldBe 0
  }
  test("parse") {
    "1970-1-1 0:0:0Z" match { case SimpleDateTime(s)             => s shouldBe Epoch }
    "1970-01-01T00:00:00.0000+00" match { case SimpleDateTime(s) => s shouldBe Epoch }
    "2010-10-31  13:40-03:30" match {
      case SimpleDateTime(s) => s shouldBe SimpleDateTime(2010, 10, 31, TimeOfDay(13, 40), -210)
    }
    "2010-10-31" match {
      case SimpleDateTime(s) => s shouldBe SimpleDateTime(2010, 10, 31)
    }
    "2010-10-31 13:00" match {
      case SimpleDateTime(s) => s shouldBe SimpleDateTime(2010, 10, 31, TimeOfDay(13))
    }
    "2010-10-31 13:00Z" match {
      case SimpleDateTime(s) => s shouldBe SimpleDateTime(2010, 10, 31, TimeOfDay(13), 0)
    }
    "12010-10-31 13:00Z" match {
      case SimpleDateTime(s) => fail("Should not match")
      case _                 =>
    }
    SimpleDateTime.parse("2015-02-28T11:00:00.123456789Z").value shouldBe SimpleDateTime(2015,
                                                                                         2,
                                                                                         28,
                                                                                         TimeOfDay(11, 0, 0, 123456789),
                                                                                         0)

    SimpleDateTime.parse("2015-02-28T11:00:00.1234567890Z") shouldBe empty
  }
}
