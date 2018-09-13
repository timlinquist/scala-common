package org.mulesoft.common

package object parse {
  implicit class ParseResult[T](val either: Either[ParseError, T]) extends AnyVal {
    def result: T = either match {
      case Right(t) => t
      case Left(e) => throw ParseException(e)
    }
  }
}
