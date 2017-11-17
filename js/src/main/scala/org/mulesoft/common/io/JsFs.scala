package org.mulesoft.common.io

import org.mulesoft.common.js.SysError

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

// Exclude from coverage because native functions are not tracked
// $COVERAGE-OFF$
@js.native
private[io] trait JsFs extends js.Object {
  type Fs1    = js.Function1[SysError, Any]
  type Fs2[T] = js.Function2[SysError, T, Any]

  /** Asynchronous mkdir(2) */
  def mkdir(path: String, callback: Fs1): Unit = js.native

  /** Synchronous mkdir. */
  def mkdirSync(path: String): Unit = js.native

  /** Asynchronous reads the contents of a directory. */
  def readdir(path: String, callback: Fs2[js.Array[String]]): Unit = js.native

  /** Asynchronous reads the contents of a directory. */
  def readdirSync(path: String): js.Array[String] = js.native

  /** Asynchronously reads the entire contents of a file. */
  def readFile(file: String, encoding: String, callback: Fs2[Any]): Unit = js.native

  /**
    * Synchronously reads the entire contents of a file.
    */
  def readFileSync(file: String, encoding: String): String = js.native

  /** Asynchronous rmdir */
  def rmdir(path: String, callback: Fs1): Unit = js.native

  /** Synchronous rmdir */
  def rmdirSync(path: String): Unit = js.native

  /** Asynchronous stat. The callback gets two arguments (err, stats) where stats is a [[Stats]] object. */
  def stat(path: String, callback: Fs2[Stats]): Stats = js.native

  /** Synchronous stat(2). Returns an instance of [[Stats]]. */
  def statSync(path: String): Stats = js.native

  /** Asynchronous unlink */
  def unlink(path: String, callback: Fs1): Unit = js.native

  /** Synchronous unlink */
  def unlinkSync(path: String): Unit = js.native

  /** Asynchronously writes an String to a file, replacing the file if it already exists. */
  def writeFile(file: String, data: String, encoding: String, callback: Fs1): Unit = js.native

  /** Synchronously writes an String to a file, replacing the file if it already exists. */
  def writeFileSync(file: String, data: String, encoding: String): Unit = js.native

}

@js.native
@JSImport("fs", JSImport.Namespace)
object JsFs extends JsFs
