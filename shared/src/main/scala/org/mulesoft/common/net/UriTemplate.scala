package org.mulesoft.common.net
import org.mulesoft.common.net.UriTemplate._
import org.mulesoft.common.parse.ParseError.formatError
import org.mulesoft.common.parse._
import org.mulesoft.common.core._


class UriTemplate private (val template: String, val parts: List[Part])

object UriTemplate {

  def parse(template: String): Either[ParseError, UriTemplate] = {
    val t     = find(template, 0)
    var open  = t._1
    var close = t._2

    if (open == -1) return Right(new UriTemplate(template, List(Literal(template))))
    if (close == -1) return unclosedBraces(template)

    val result = List.newBuilder[Part]
    var last   = 0
    while (open != -1) {
      if (close == -1) return unclosedBraces(template)
      if (open > last) result += Literal(template.substring(last, open))
      Expression.parse(template.substring(open + 1, close)) match {
        case Right(e) => result += e
        case Left(e)  => return Left(e)
      }
      last = close + 1
      val t = find(template, last)
      open = t._1
      close = t._2
    }
    if (last + 1 < template.length) result += Literal(template.substring(last))

    Right(new UriTemplate(template, result.result()))
  }

  private def find(template: String, fromIndex: Int): (Int, Int) = {
    val open = template.indexOf('{', fromIndex)
    if (open == -1) (-1, -1) else (open, template.indexOf('}', open + 1))
  }

  def apply(template: String): UriTemplate = parse(template).result

  sealed trait Part
  case class Literal(s: String)                                                   extends Part
  case class Expression private (str: String, op: Operator, vars: List[VarSpec]) extends Part

  object Expression {
    def parse(str: String): Either[ParseError, Expression] = {
      if (str.isNullOrEmpty) formatError(str, "Empty expression")
      else
        Operator.parse(str) flatMap { op =>
          parseVars(if (op.isEmpty) str else str.tail) map (new Expression(str, op, _))
        }
    }

    private def parseVars(str: String): Either[ParseError, List[VarSpec]] = {
      val result = List.newBuilder[VarSpec]
      for (v <- str.split(",")) v match {
        case VarSpecRegex(name, prefix, explode) =>
          result += VarSpec(name, if (prefix == null) Int.MaxValue else prefix.toInt, explode != null)
        case _ =>
          return formatError(v, "Illegal Variable Specification")
      }
      Right(result.result())
    }

    def apply(str: String): Expression = parse(str).result
  }

  class Operator private (val op: Char) {
    def isEmpty: Boolean = op == ' '

    override def equals(obj: Any): Boolean = obj match {
      case o: Operator => o.op == op
      case _           => false
    }
    override def toString: String = if (op == ' ') "" else op.toString
  }
  object Operator {
    final val empty = new Operator(' ')
    def parse(s: String): Either[ParseError, Operator] =
      if (validOperators.indexOf(s(0)) != -1) Right(new Operator(s(0)))
      else
        s match {
          case VarcharRegex() => Right(empty)
          case _              => formatError(s, "Illegal operator")
        }

    def apply(s: String): Operator = parse(s).result

  }

  case class VarSpec(name: String, prefix: Int, explode: Boolean)

  private def unclosedBraces(s: String) = formatError(s, "Unclosed braces")

  // $COVERAGE-OFF$
  private final val validOperators = "+#./;?&=,!@"
  private final val Varchar      = "(?:[A-Za-z0-9_]|%[0-9A-Fa-f]{2})"
  private final val VarcharRegex = (Varchar + ".*").r
  private final val Prefix       = "(?::([1-9][0-9]{0,2}))"
  private final val Explode      = "(\\*)"
  private final val VarName      = s"($Varchar(?:\\.?$Varchar)*)"
  private final val VarSpecRegex = s"$VarName(?:$Prefix|$Explode)?".r

}
