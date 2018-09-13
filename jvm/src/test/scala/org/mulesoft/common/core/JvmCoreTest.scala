package org.mulesoft.common.core

/**
  * Instantiate the test
  */
class JvmCoreTest extends CoreTest {
  test("punycode") {
    "hello".toPunnycode shouldBe "hello"
    "Ñandú".toPunnycode shouldBe "xn--and-6ma2c"
    "büücher".toPunnycode shouldBe "xn--bcher-kvaa"
  }
}
