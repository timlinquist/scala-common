package org.mulesoft.common.io

import org.mulesoft.common.io.JsBaseFile._

import scala.concurrent.ExecutionContext

/**
  * Implementation of a AsyncFile for the JavaScript
  * * @todo better handling of errors, Real async mode
  */
protected class JsSyncFile(fs: JsServerFileSystem, path: String) extends JsBaseFile(fs, path) with SyncFile {

  override def list: Array[String]                                  = JsFs.readdirSync(path).toArray
  override def mkdir(implicit ctx: ExecutionContext = global): Unit = JsFs.mkdirSync(path)
  override def read(encoding: String)(implicit ctx: ExecutionContext = global): CharSequence =
    JsFs.readFileSync(path, encoding)
  override def write(data: CharSequence, encoding: String)(implicit ctx: ExecutionContext = global): Unit =
    JsFs.writeFileSync(path, data.toString, encoding)

  override def delete: Unit =
    if (exists) if (isDirectory) JsFs.rmdirSync(path) else JsFs.unlinkSync(path)

  override def exists(implicit ctx: ExecutionContext = global): Boolean      = stat.isDefined
  override def isDirectory(implicit ctx: ExecutionContext = global): Boolean = checkStats(stat, _.isDirectory())
  override def isFile(implicit ctx: ExecutionContext = global): Boolean      = checkStats(stat, _.isFile())

  private def stat: Option[Stats] = {
    try {
      Some(JsFs.statSync(path))
    } catch {
      case _: Throwable => None
    }
  }
}
