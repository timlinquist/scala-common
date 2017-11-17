package org.mulesoft.common.io

import org.mulesoft.common.io.JsBaseFile._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}

/**
  * Implementation of a AsyncFile for the JavaScript
  * * @todo better handling of errors, Real async mode
  */
protected class JsAsyncFile(fs: JsServerFileSystem, path: String) extends JsBaseFile(fs, path) with AsyncFile {

  override def delete: Future[Unit] = {
    val promise = Promise[Unit]()
    JsFs.stat(path,
            (err, s) =>
              if (err != null) promise.success(())
              else {
                val rmOp = if (s.isDirectory()) JsFs.rmdir _ else JsFs.unlink _
                rmOp(path, completeOrFail(promise, (), _))
            })
    promise.future
  }

  override def list: Future[Array[String]] = {
    val promise = Promise[Array[String]]()
    JsFs.readdir(path, (err, array) => completeOrFail(promise, array.toArray, err))
    promise.future
  }

  override def mkdir: Future[Unit] = {
    val promise = Promise[Unit]()
    JsFs.mkdir(path, completeOrFail(promise, (), _))
    promise.future
  }

  override def read(encoding: String): Future[CharSequence] = {
    val promise = Promise[String]()
    JsFs.readFile(path, encoding, (err, data) => completeOrFail(promise, data.asInstanceOf[String], err))
    promise.future
  }

  override def write(data: CharSequence, encoding: String): Future[Unit] = {
    val promise = Promise[Unit]()
    JsFs.writeFile(path, data.toString, encoding, completeOrFail(promise, (), _))
    promise.future
  }

  override def exists: Future[Boolean]      = stat map (_.isDefined)
  override def isDirectory: Future[Boolean] = stat map (checkStats(_, _.isDirectory()))
  override def isFile: Future[Boolean]      = stat map (checkStats(_, _.isFile()))

  private def stat: Future[Option[Stats]] = {
    val promise = Promise[Option[Stats]]()
    JsFs.stat(path, (err, s) => if (err == null) promise.success(Some(s)) else promise.success(None))
    promise.future
  }
}
