package org.mulesoft.common.io

import org.scalatest.AsyncFunSuite

import scala.concurrent.ExecutionContext

/**
  * IO Tests
  */
trait IoAsyncTest extends AsyncFunSuite with BaseIoTest {
  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  test("read") {
    val dataDir = fs.asyncFile(dirName)
    val hello   = dataDir / "helloWorld.txt"

    for {
      b <- dataDir.exists
      if b
      d <- dataDir.isDirectory
      if d
      f <- hello.isFile
      if f
      s1 <- hello.read()
      s2 <- dataDir / "helloWorld.iso" read "latin1"
    } yield (s1.toString, s2.toString) shouldBe (helloString, helloString)
  }
}
