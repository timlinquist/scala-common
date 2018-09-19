package org.mulesoft.common.io
import java.io.Writer

/**
  * An Output is a type class for defining classes to ouput to like [[java.io.Writer]] or [[java.io.OutputStream]]
  *
  */
trait Output[W] {
  def append(w: W, s: String): Unit
  def append(w: W, c: Char): Unit = append(w, c.toString)

  def write(w: W, cbuf: Array[Char], off: Int, len: Int): Unit = append(w, new String(cbuf, off, len))
  def write(w: W, cbuf: Array[Char]): Unit                     = write(w, cbuf, 0, cbuf.length)

  def flush(w: W): Unit = {}
  def close(w: W): Unit
}

object Output {

  /** OutputOps implements operations on W:Output mostly by delegation to the Output[W] trait */
  implicit class OutputOps[W](val w: W) extends AnyVal {

    def append(s: String)(implicit o: Output[W]): Unit = o.append(w, s)
    def append(c: Char)(implicit o: Output[W]): Unit   = o.append(w, c)

    def write(w: W, cbuf: Array[Char], off: Int, len: Int)(implicit o: Output[W]): Unit = o.write(w, cbuf, off, len)
    def write(w: W, cbuf: Array[Char])(implicit o: Output[W]): Unit                     = o.write(w, cbuf)

    def close(implicit o: Output[W]): Unit = o.close(w)
    def flush(implicit o: Output[W]): Unit = o.flush(w)
  }

  implicit object OutputWriter extends Output[Writer] {

    override def append(writer: Writer, string: String): Unit = writer.append(string)
    override def append(writer: Writer, chr: Char): Unit      = writer.append(chr)

    override def write(w: Writer, cbuf: Array[Char], off: Int, len: Int): Unit = w.write(cbuf, off, len)
    override def write(w: Writer, cbuf: Array[Char]): Unit                     = w.write(cbuf)

    override def flush(writer: Writer): Unit = writer.flush()
    override def close(writer: Writer): Unit = writer.close()
  }

  implicit def outputWriter[W <: Writer]: Output[W] = OutputWriter.asInstanceOf[Output[W]]
}
