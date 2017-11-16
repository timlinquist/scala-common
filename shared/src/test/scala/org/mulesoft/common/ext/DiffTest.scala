package org.mulesoft.common.ext

import org.scalatest.{Assertions, FunSuite, Matchers}

trait DiffTest extends FunSuite with Assertions with Matchers {

  test("Case Insensitive Diff") {
    val deltas = Diff.caseInsensitive.diff(first, second)

    val out2 = "1,3c1\n" +
      "<   The Way that can be told of is not the eternal Way;\n" +
      "<     The name that can be named is not the eternal name.\n" +
      "<         The Nameless is the origin of Heaven and Earth;\n" +
      "---\n" +
      ">     The Nameless is the origin of Heaven and Earth;\n" +
      "4a3\n" +
      "> \n" +
      "11a11,13\n" +
      ">     They both may be called deep and profound.\n" +
      ">     Deeper and more profound\n" +
      ">     The door of all subtleties!\n"

    assertResult(out2) {
      Diff.makeString(deltas)
    }

    deltas.head.toString should startWith("1,3c1\n")

    assertResult(out2) {
      val deltas = Diff[String](_ equalsIgnoreCase _).diff(first, second)
      Diff.makeString(deltas)
    }
    assertResult(out2) {
      val deltas = Diff.stringDiffer(_ equalsIgnoreCase _).diff(first, second)
      Diff.makeString(deltas)
    }
  }
  test("Ignore space") {
    Diff.ignoreAllSpace.diff("""a a a
            |
            | b
          """.stripMargin,
                             """aaa
            |b
          """.stripMargin) should be(empty)
    val deltas = Diff.ignoreAllSpace.diff("a a a\n\n bx\n", "aaa\nb")
      Diff.makeString(deltas) shouldBe "2,3c2\n< \n<  bx\n---\n> b\n"

  }
  test("Trimming") {
    val deltas = Diff.trimming.diff("  aa a ", "aa a")
    deltas should be(empty)
  }

  test("Case Insensitive Diff Strings By Line") {
    val deltas: List[Diff.Delta[String]] = Diff.caseInsensitive.diff("Hello\nWorld", "HELLO\nWORLD")
    deltas shouldBe empty
  }

    test("error") {
        Diff.PathNode.diffNode(0,0,null).prev shouldBe null
    }
  test("Case Sensitive Diff") {
    val deltas: List[Diff.Delta[String]] = Diff.caseSensitive.diff(first, second)
    val out1: String = "1,4c1,3\n" +
      "<   The Way that can be told of is not the eternal Way;\n" +
      "<     The name that can be named is not the eternal name.\n" +
      "<         The Nameless is the origin of Heaven and Earth;\n" +
      "<     The Named is the mother of all things.\n" +
      "---\n" +
      ">     The Nameless is the origin of Heaven and Earth;\n" +
      ">     The named is the mother of all things.\n" +
      "> \n" +
      "11a11,13\n" +
      ">     They both may be called deep and profound.\n" +
      ">     Deeper and more profound\n" +
      ">     The door of all subtleties!\n"
    Diff.makeString(deltas) shouldEqual out1
  }

  test("Diff Strings By Line") {
    val deltas: List[Diff.Delta[String]] = Diff.caseSensitive.diff("Hello\nWorld", "")
    deltas.size shouldEqual 1

    deltas.head.toString shouldEqual "1,2d0\n< Hello\n< World\n"
  }

  //~ Static Fields ................................................................................................................................

  val first = List(
      "  The Way that can be told of is not the eternal Way;",
      "    The name that can be named is not the eternal name.",
      "        The Nameless is the origin of Heaven and Earth;",
      "    The Named is the mother of all things.",
      "    Therefore let there always be non-being,",
      "      so we may see their subtlety,",
      "    And let there always be being,",
      "      so we may see their outcome.",
      "    The two are the same,",
      "    But after they are produced,",
      "      they have different names."
  )

  val second = List(
      "    The Nameless is the origin of Heaven and Earth;",
      "    The named is the mother of all things.",
      "",
      "    Therefore let there always be non-being,",
      "      so we may see their subtlety,",
      "    And let there always be being,",
      "      so we may see their outcome.",
      "    The two are the same,",
      "    But after they are produced,",
      "      they have different names.",
      "    They both may be called deep and profound.",
      "    Deeper and more profound",
      "    The door of all subtleties!"
  )
}
