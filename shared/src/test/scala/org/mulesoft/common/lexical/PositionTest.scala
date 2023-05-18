package org.mulesoft.common.lexical

import org.mulesoft.common.client.lexical.Position.ZERO
import org.mulesoft.common.client.lexical.{Position, SourceLocation}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

/**
 * Test Builders
 */
trait PositionTest extends AnyFunSuite with Matchers {

  test("Position by Offset") {
    val a = Position(10)
    val b = Position(12)
    a should be > ZERO
    a should be < b
    List(b, ZERO, a).sorted should contain theSameElementsInOrderAs List(ZERO, a, b)

    List(b, ZERO, a).toString shouldBe "List((0,0)@12, (0,0), (0,0)@10)"
  }

  test("Source Location") {
    val a = SourceLocation("a.c")
    a.sourceName shouldBe "a.c"
  }

}
