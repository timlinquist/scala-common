package org.mulesoft.common.client.lexical

import org.mulesoft.common.client.lexical.Position.ZERO
import java.lang.Integer.{MAX_VALUE=>IntMax}

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

/** Defines a range on an input */
@JSExportAll
@JSExportTopLevel("Range")
case class PositionRange(start: Position, end: Position) {

  /** Extent range */
  def extent(other: PositionRange): PositionRange = PositionRange(start.min(other.start), end.max(other.end))

  override def toString: String = s"[$start-$end]"

  def contains(other: PositionRange): Boolean = {
    other.start.line >= start.line && other.end.line <= end.line
  }

  val lineFrom: Int = start.line
  val columnFrom: Int = start.column
  val lineTo: Int = end.line
  val columnTo: Int = end.column

  def compareTo(other: PositionRange): Int = {
    val lineDiff = this.lineFrom - other.lineFrom
    if (lineDiff == 0) {
      val columnDiff = this.columnFrom - other.columnFrom
      if (columnDiff == 0) {
        val toLineDiff = this.lineTo - other.lineTo
        if (toLineDiff == 0) this.columnTo - other.columnTo
        else toLineDiff
      } else columnDiff
    }
    else lineDiff
  }
}

object PositionRange {

  object NONE extends PositionRange(Position.ZERO, Position.ZERO)

  val ZERO = PositionRange((1, 0), (1, 0))
  val ALL = PositionRange(1, 0, IntMax, IntMax)

  def apply(lineFrom:Int, columnFrom:Int, lineTo:Int, columnTo:Int):PositionRange =
    if (lineFrom <= 1 && columnFrom <= 0 && lineTo <= 1 && columnTo <= 0) ZERO
    else if (lineFrom <= 1 && columnFrom <= 0 && lineTo == IntMax && lineFrom == IntMax) ALL
    else apply((lineFrom, columnFrom), (lineTo, columnTo))

  def apply(start: Position, delta: Int): PositionRange = new PositionRange(start, Position(start.line, start.column + delta))

  def apply(start: (Int, Int), end: (Int, Int)): PositionRange = new PositionRange(Position(start), Position(end))

  def apply(serialized: String): PositionRange = {
    val Pattern = "\\[\\(([0-9]*),([0-9]*)\\)-\\(([0-9]*),([0-9]*)\\)\\]".r
    serialized match {
      case Pattern(l1, c1, l2, c2) => PositionRange((l1.toInt, c1.toInt), (l2.toInt, c2.toInt))
    }
  }
}
