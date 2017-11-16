package org.mulesoft.common.io

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FunSuite, Matchers}

import scala.language.higherKinds

/**
  * IO Tests
  */
trait IoTest extends FunSuite with BaseIoTest {

  test("read") {
    val dataDir = fs.syncFile(dirName)
    dataDir.exists shouldBe true
    dataDir.isDirectory shouldBe true

    val hello = dataDir / helloFileName
    hello.isFile shouldBe true

    hello.read().toString shouldBe helloString

    (hello withExt ".iso" read LatinEncoding).toString shouldBe helloString
  }

  test("write") {
    val targetDir = fs.asyncFile(targetDirName).sync
    targetDir.delete
    targetDir.mkdir
    val hello   = targetDir / helloFileName
    val isoFile = targetDir / helloIsoFileName

    an[Exception] should be thrownBy {
      hello read ()
    }
    hello write helloString
    hello.read().toString shouldBe helloString

    isoFile.write(helloString, LatinEncoding)
    isoFile.read(Utf8).toString should not be helloString
    isoFile.read(LatinEncoding).toString shouldBe helloString

    targetDir.list should contain theSameElementsAs dirList
    isoFile.delete
    isoFile.exists shouldBe false
    hello.delete
    hello.exists shouldBe false
    hello.isDirectory shouldBe false
    hello.delete
  }
  test("File parts") {
    runTest("/home/john/dir", List("/", "home", "john", "dir", "file.x"))
    runTest("dir", List("dir", "file.x"))
    val f = fs syncFile ""
    f.path shouldBe ""
    f.name shouldBe ""
    f.parent shouldBe null
  }
  test("errors") {
    an[Exception] should be thrownBy (fs.syncFile(targetDirName) / helloFileName).read()
  }

  private def runTest(parent2: String, parts: List[String]) = {
    val name2 = parent2 + "/file.x"
    testParts(fs.syncFile(name2), name2, parent2, parts)
    testParts(fs.asyncFile(name2), name2, parent2, parts)
  }

  private def testParts(file: File, name: String, parent: String, parts: List[String]): Any = {
    file.path shouldBe name
    file.toString shouldBe name
    file.name shouldBe "file.x"

    file.parent shouldBe parent

    var f               = file
    var l: List[String] = Nil
    while (f != null) {
      l = (if (f.name.isEmpty) f.path else f.name) :: l
      f = f.parentFile
    }
    l shouldBe parts
  }

}
trait BaseIoTest extends Matchers with ScalaFutures {
  def fs: FileSystem

  val dirName          = "shared/src/test/data"
  val targetDirName    = "target/test"
  val helloFileName    = "helloWorld.txt"
  val helloIsoFileName = "helloWorld.iso"
  val dirList          = List(helloIsoFileName, helloFileName)

  val helloString   = "Hello World!\n¡Hola Mundo!\n"
  val LatinEncoding = "latin1"
}
