package org.mulesoft.common.net
import java.net.{URI, URISyntaxException}

object UriValidator {

  def isUri(uriStr: String, allowRelative: Boolean = true): Boolean = if (uriStr == null) false else {
    try {
      val uri = new URI(uriStr)
      uri.getScheme != null || allowRelative && uriStr.startsWith("//")
    }
    catch {
      case _: URISyntaxException => false
    }

  }

}
