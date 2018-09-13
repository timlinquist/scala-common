package org.mulesoft.common.net
import java.lang.Integer.parseInt

import org.mulesoft.common.parse.ParseError
import org.mulesoft.common.parse.ParseError.{formatError, rangeError}

case class InetAddress private (ipv6: Boolean, groups: List[Int])

object InetAddress {

  def parse(address: String): Either[ParseError, InetAddress] = {
    val r = parseAsIPv4(address)
    if (r.isRight) r
    else if (address.indexOf(':') == -1) r
    else parseAsIPv6(address)
  }

  /** Parse as an Ipv4 address */
  def parseAsIPv4(address: String): Either[ParseError, InetAddress] = address match {
    case Ipv4Regex(groups @ _*) =>
      val r = List.newBuilder[Int]
      for (group <- groups) {
        val g = group.toInt
        if (g > Ipv4MaxOctetValue) return rangeError(g)
        r += g
      }
      Right(InetAddress(ipv6 = false, r.result()))

    case _ =>
      formatError(address)
  }

  /** Parse an IPv6 address */
  def parseAsIPv6(address: String): Either[ParseError, InetAddress] = {

    def split(s: String) = if (s.isEmpty) Array("0") else s.split(':')

    // Check double colon (Compressed Zeroes)
    val doubleColon = address.indexOf("::")
    // Ensure there is only one
    if (doubleColon != -1 && address.indexOf("::", doubleColon + 1) != -1) return formatError(address)

    // check if it has an embedded ipv4 Address
    val lastColon = address.lastIndexOf(':')
    if (lastColon == -1) return formatError(address)

    val ipv4N = if (address.indexOf('.', lastColon) == -1) 0 else 1

    // Split the groups
    val groups =
      if (doubleColon == -1) address.split(':')
      else {
        val head = split(address.substring(0, doubleColon))
        val tail = split(address.substring(doubleColon + 2))
        head ++ Array.fill(Ipv6Groups - head.length - tail.length - ipv4N)("0") ++ tail
      }

    if (groups.length + ipv4N != Ipv6Groups) return formatError(address, "Wrong number of groups")

    val r = List.newBuilder[Int]
    for (i <- 0 until groups.length - ipv4N) {
      val g = groups(i)
      try {
        val v = parseInt(g, 16)
        if (v > Ipv6MaxGroupValue) return rangeError(v)
        r += v
      } catch {
        case _: Exception => return formatError(g)
      }
    }
    if (ipv4N == 1) parseAsIPv4(groups.last) match {
      case Right(ia) =>
        r += (ia.groups(0) << 8) + ia.groups(1)
        r +=( ia.groups(2) << 8) + ia.groups(3)
      case e @ _ =>
        return e
    }
    Right(InetAddress(ipv6 = true, r.result()))
  }

  /** Create an ipv4 address */
  def ipv4(address: String): InetAddress = parseAsIPv4(address).result

  /** Create an ipv6 address */
  def ipv6(address: String): InetAddress = parseAsIPv6(address).result

  /** Create an InetAddress */
  def apply(address: String): InetAddress = parse(address).result

  // $COVERAGE-OFF$
  private final val Ipv4Group         = "(0|[1-9]\\d{0,2})"
  private final val Ipv4Regex         = s"""^$Ipv4Group.$Ipv4Group.$Ipv4Group.$Ipv4Group$$""".r
  private final val Ipv4MaxOctetValue = 255

  private final val Ipv6Groups        = 8
  private final val Ipv6MaxGroupValue = 0xFFFF
}
