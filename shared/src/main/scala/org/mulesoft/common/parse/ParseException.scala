package org.mulesoft.common.parse

case class ParseException(parseError: ParseError) extends RuntimeException {
  override def getMessage: String = parseError.message
}

sealed trait ParseError { def message: String }

case class FormatError(value: String, msg: String = "") extends ParseError {
  override def message: String = {
    val m = if (msg.isEmpty) "" else " (" + msg + ")"
    s"Format Error$m in '$value'"
  }
}

case class RangeError(value: Int) extends ParseError {
  override def message: String = s"Value '$value' out of range"
}

object ParseError {
  def formatError[T](value: String, msg: String = ""): Either[FormatError, T] = Left(FormatError(value, msg))
  def rangeError[T](value: Int): Either[RangeError, T] = Left(RangeError(value))
}
