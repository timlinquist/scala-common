package org.mulesoft.common.io

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FunSuite, Matchers}

/**
  * IO Tests
  */
trait IoTest extends FunSuite with BaseIoTest {

    test("read") {
        val dataDir = fs.syncFile(dirName)
        dataDir.exists shouldBe true
        dataDir.isDirectory shouldBe true

        val hello = dataDir / "helloWorld.txt"
        hello.isFile shouldBe true

        hello.read().toString shouldBe helloString

        (dataDir / "helloWorld.iso" read "latin1").toString shouldBe helloString
    }

}
trait BaseIoTest extends Matchers with ScalaFutures {
    def fs: FileSystem

    val dirName = "shared/src/test/data"
    val helloString = "Hello World!\n¡Hola Mundo!\n"
}