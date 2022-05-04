package org.mulesoft.common.client.lexical

import org.mulesoft.common.client.lexical.Position.ZERO

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

/** Defines a position on an input */
@JSExportAll
@JSExportTopLevel("Position")
case class Position(line: Int, column: Int, offset:Int) extends Ordered[Position] with Comparable[Position] {

  def +(that: Position): Position =
    if (this.isZero) that
    else if (that.isZero) this
    else Position(line + that.line, column + that.column, offset + that.offset)

  /** Return true if position is less than specified position. */
  def lt(o: Position): Boolean = compareTo(o) < 0

  /* Return min position between actual and given. */
  def min(other: Position): Position =
    if (line < other.line || line == other.line && column <= other.column) this else other

  /* Return max position between actual and given. */
  def max(other: Position): Position =
    if (line > other.line || line == other.line && column >= other.column) this else other

  override def compare(that: Position): Int =
    if (offset != that.offset) Integer.compare(offset, that.offset)
    else if (line != that.line) Integer.compare(line, that.line)
    else Integer.compare(column, that.column)

  override def compareTo(o: Position): Int = compare(o)

  override def equals(obj: Any): Boolean = obj match {
    case that: Position => line == that.line && column == that.column && offset == that.offset
    case _              => false
  }

  override def hashCode(): Int = line + 31 * (column + 31 * offset)

  def isZero: Boolean = this == ZERO

  override def toString: String = {
    val lc = s"($line,$column)"
    val offsetPart = if(offset == 0) "" else "@" + offset
    lc + offsetPart
  }
}

object Position {

  val ZERO = new Position(0, 0, 0)

  def FIRST: Position = Position(0, 1, 0)

  def apply(line: Int, column: Int, offset: Int = 0): Position =
    if (line == 0 && column == 0 && offset == 0) ZERO else new Position(line, column, offset)

  def apply(offset: Int): Position =
    if (offset == 0) ZERO else new Position(0, 0, offset)

  def apply(lc: (Int, Int)): Position = Position(lc._1, lc._2, 0)

}

