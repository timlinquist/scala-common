package org.mulesoft.common.net

/**
  * Instantiate the test
  */
class JvmNetTest extends NetTest {
  test("unicode domain name") {
    import DomainValidator._

    isValid("Ñandú¡.com") shouldBe true
    isValid("Ñandú!.com") shouldBe false

  }

  test("email unicode") {
    import Email._

    parse("José@example.com").isRight shouldBe true
    parse("δοκιμή@παράδειγμα.δοκιμή").isRight shouldBe true
    parse("我買@屋企.香港").isRight shouldBe true
    parse("чебурашка@ящик-с-апельсинами.рф").isRight shouldBe true
  }
}
