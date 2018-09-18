package org.mulesoft.common.net
import org.mulesoft.common.core._

object DomainValidator {

  def isValid(domain: String, allowLocal: Boolean = false): Boolean =
    if (domain == null) false
    else {
      val asciiDomain = domain.toPunnycode
      asciiDomain.length <= MaxDomainLength && (asciiDomain match {
        case DomainRegex(top)              => true
        case TopLabelRegex() if allowLocal => true
        case _                             => false
      })
    }
  private final val LabelTail   = s"(?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?"
  private final val DomainLabel = "[a-zA-Z0-9]" + LabelTail
  private final val TopLabel    = "[a-zA-Z]" + LabelTail

  private final val TopLabelRegex = TopLabel.r
  private final val DomainRegex   = s"""^(?:$DomainLabel\\.)+($TopLabel)\\.?$$""".r

  // $COVERAGE-OFF$
  private final val MaxDomainLength = 253
}
