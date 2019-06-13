package org.mulesoft.common.io

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

/**
  * Implementation of the file system for node.js platform
  */
class JsServerFileSystem private[io]() extends FileSystem {

  /** The prefix length for a path */
  def prefixLength(path: String): Int = if (path.length == 0) 0 else if (path.charAt(0) == separatorChar) 1 else 0
  def separatorChar: Char             = JsPath.sep.charAt(0)

  override def syncFile(path: String): SyncFile   = if (path == null) null else new JsSyncFile(this, path)
  override def asyncFile(path: String): AsyncFile = if (path == null) null else new JsAsyncFile(this, path)
}

/** Path */
@js.native
@JSImport("path", JSImport.Namespace, "path")
object JsPath extends js.Object {
  /** Returns the operating system's separator char. */
  val sep: String = js.native
}

object Fs extends JsServerFileSystem
