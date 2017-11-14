package org.mulesoft.common.io

import java.io.IOException

import org.mulesoft.common.io.JsBaseFile._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}

/**
  * Implementation of a AsyncFile for the JavaScript
  * * @todo better handling of errors, Real async mode
  */
protected class JsAsyncFile(fileSystem: JsServerFileSystem, path: String)
    extends JsBaseFile[Future](fileSystem, path)
    with AsyncFile {
  protected var stats: Future[Option[Stats]] = _

  override def list: Future[Array[String]] = {
    val promise = Promise[Array[String]]()
    Fs.readdir(path, (err, array) => completeOrFail(promise, array.toArray, err))
    promise.future
  }

  override def mkdir: Future[Unit] = {
    val promise = Promise[Unit]()
    Fs.mkdir(path, err => completeOrFail(promise, (), err))
    promise.future
  }

  override def read(encoding: String): Future[CharSequence] = {
    val promise = Promise[String]()
    Fs.readFile(path, encoding, (err, data) => completeOrFail(promise, data.asInstanceOf[String], err))
    promise.future
  }

  override def write(data: CharSequence, encoding: String): Future[Unit] = {
    val promise = Promise[Unit]()
    Fs.writeFile(path, data.toString, encoding, err => completeOrFail(promise, (), err))
    promise.future
  }

  override def exists: Future[Boolean]      = stat map (_.isDefined)
  override def isDirectory: Future[Boolean] = stat map (checkStats(_, _.isDirectory()))
  override def isFile: Future[Boolean]      = stat map (checkStats(_, _.isFile()))

  private def stat: Future[Option[Stats]] = {
    if (stats == null) stats = readStats
    stats
  }

  private def readStats = {
    val promise = Promise[Option[Stats]]()
    Fs.stat(path, (err, s) => {
      if (err == null) promise.success(Some(s))
      else if (err.code == ENOENT) promise.success(None)
      else promise.failure(new IOException(err.message))
    })
    promise.future
  }
}
