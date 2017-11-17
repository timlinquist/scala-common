package org.mulesoft.common.io

import java.io.{File => JFile}

/**
  * A Basic Jvm Filesystem implementation
  */
object Fs extends FileSystem {
  override def syncFile(path: String): SyncFile   = if (path == null) null else new JvmSyncFile(this, path)
  override def asyncFile(path: String): AsyncFile = if (path == null) null else syncFile(path).async
  override def separatorChar: Char                = JFile.separatorChar
}

