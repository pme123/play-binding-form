package pme123.form.shared.services

import scala.language.implicitConversions


// add useful extensions here
object SPAExtensions {

  implicit class StringPos(val str: String) extends AnyVal {

    def isBlank: Boolean =
      Option(str).forall(_.trim.isEmpty)

    def nonBlank: Boolean =
      Option(str).exists(_.trim.nonEmpty)

    def splitToSet: Set[String] = str.split(",")
      .map(_.trim).filter(_.nonEmpty).toSet

    def toCamelCase: String =
      str.split("_").toList match {
        case Nil => ""
        case x :: xs =>
          x.toLowerCase +
            xs.foldLeft("")((a, b) => a + s"${b.head}".toUpperCase + b.tail.toLowerCase)
      }
  }

}
