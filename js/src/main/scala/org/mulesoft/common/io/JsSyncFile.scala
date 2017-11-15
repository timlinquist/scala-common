package org.mulesoft.common.io

import org.mulesoft.common.io.JsBaseFile._

/**
  * Implementation of a AsyncFile for the JavaScript
  * * @todo better handling of errors, Real async mode
  */
protected class JsSyncFile(fs: JsServerFileSystem, path: String) extends JsBaseFile(fs, path) with SyncFile {

  override def list: Array[String]                               = Fs.readdirSync(path).toArray
  override def mkdir: Unit                                       = Fs.mkdirSync(path)
  override def read(encoding: String): CharSequence              = Fs.readFileSync(path, encoding)
  override def write(data: CharSequence, encoding: String): Unit = Fs.writeFileSync(path, data.toString, encoding)

  override def delete: Unit =
    if (exists) if (isDirectory) Fs.rmdirSync(path) else Fs.unlinkSync(path)

  override def exists: Boolean      = stat.isDefined
  override def isDirectory: Boolean = checkStats(stat, _.isDirectory())
  override def isFile: Boolean      = checkStats(stat, _.isFile())

  private def stat: Option[Stats] = {
    try {
      Some(Fs.statSync(path))
    } catch {
      case _: Throwable => None
    }
  }
}
