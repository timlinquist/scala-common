package org.mulesoft.common

import java.util.Date

import org.mulesoft.common.time.SimpleDateTime.Epoch
import org.mulesoft.common.time.{SimpleDateTime, SimpleDateTimeTest}

import scala.scalajs.js.{Date => jsDate}

/**
  * Instantiate
  */
class JsSimpleDateTimeTest extends SimpleDateTimeTest {
  test("conversions") {
    val epochDate = new Date(0)
    val jsEpoch   = new jsDate(0)

    Epoch.toJsDate.getTime() shouldBe jsEpoch.getTime()
    Epoch.toDate shouldBe epochDate

    val y2k = SimpleDateTime(2000, 1, 1)

    val jsY2k = new jsDate(2000, 0, 1)

    y2k.toDate shouldBe new Date(jsY2k.getTime().toLong)
    y2k.toJsDate.getTime() shouldBe jsY2k.getTime()

  }
}
