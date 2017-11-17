package org.mulesoft.common.io

/**
  * Implementation of the file system for node.js platform
  */
class JsServerFileSystem private[io]() extends FileSystem {

  /** The prefix length for a path */
  def prefixLength(path: String): Int = if (path.length == 0) 0 else if (path.charAt(0) == '/') 1 else 0
  def separatorChar: Char             = '/'

  override def syncFile(path: String): SyncFile   = if (path == null) null else new JsSyncFile(this, path)
  override def asyncFile(path: String): AsyncFile = if (path == null) null else new JsAsyncFile(this, path)

}

object Fs extends JsServerFileSystem
