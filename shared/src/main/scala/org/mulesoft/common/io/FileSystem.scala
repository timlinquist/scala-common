package org.mulesoft.common.io

/**
  * File System abstraction
  */
trait FileSystem {

  /** Create a Sync view of a file */
  def syncFile(parent: File, name: String): SyncFile = syncFile(parent.path + separatorChar + name)

  /** Create a Sync view of a file */
  def syncFile(path: String): SyncFile

  /** Create a Sync view of a file */
  def asyncFile(parent: File, name: String): AsyncFile = asyncFile(parent.path + separatorChar + name)

  /** Create a Async view of a file */
  def asyncFile(path: String): AsyncFile

  /** The Separator char for this Filesystem */
  def separatorChar: Char
}
