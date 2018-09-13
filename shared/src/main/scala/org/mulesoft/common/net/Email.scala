package org.mulesoft.common.net
import org.mulesoft.common.parse.ParseError.formatError
import org.mulesoft.common.parse._

case class Email private (user: String, domain: String)

object Email {

  def parse(user: String, domain: String): Either[ParseError, Email] =
    if (!userIsValid(user)) formatError(user, "Invalid User")
    else if (!domainIsValid(domain)) formatError(domain, "Invalid domain")
    else Right(new Email(user, domain))

  def parse(email: String): Either[ParseError, Email] = {
    if (email != null && !email.endsWith(".")) {
      email match {
        case EmailRegex(user, domain) => return parse(user, domain)
        case _                        =>
      }
    }
    formatError(email)
  }

  def apply(user: String, domain: String): Email = parse(user, domain).result
  def apply(email: String): Email                = parse(email).result

  def unapply(arg: String): Option[Email] = parse(arg).toOption

  private def domainIsValid(domain: String): Boolean = {
    domain match {
      case IpDomainRegex(address) => InetAddress.parse(address).isRight
      case _                      => DomainValidator.isValid(domain)
    }
  }

  protected def userIsValid(user: String): Boolean =
    user != null && user.length <= MaxUserLength && UserRegex.pattern.matcher(user).matches

  // $COVERAGE-OFF$
  private final val EmailRegex    = """^\s*(.+)@(.+?)\s*$""".r
  private final val IpDomainRegex = """^\[(.*)\]$""".r

  private final val ValidChar     = """(\\[^ "\\])|[^\s\x00-\x1F\x7F\(\)<>@,;:'\\\"\.\[\]]"""
  private final val QuotedUser    = """"(\\"|[^"])*""""
  private final val Word          = s"(($ValidChar|')+|$QuotedUser)"
  private final val UserRegex     = s"^\\s*$Word(\\.$Word)*$$".r
  private final val MaxUserLength = 64
}
