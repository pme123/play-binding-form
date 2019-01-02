package pme123.form.shared

import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json._
import pme123.form.shared.BaseElement.ident
import pme123.form.shared.ElementType.SPACER
import pme123.form.shared.TextType.{LABEL, PLACEHOLDER, TOOLTIP}
import pme123.form.shared.services.Language

import scala.language.implicitConversions

case class ElementTexts(label: Option[ElementText] = None,
                        placeholder: Option[ElementText] = None,
                        tooltip: Option[ElementText] = None) {


}

object ElementTexts {

  def apply(defaultLabel: String)(implicit supportedLangs: Seq[Language]): ElementTexts = {
    ElementTexts(
      Some(ElementText.label(supportedLangs.map(_ -> defaultLabel).toMap)),
      Some(ElementText.emptyPlaceholder),
      Some(ElementText.emptyTooltip),
    )
  }

  def label(texts: Map[Language, String])(implicit supportedLangs: Seq[Language]): ElementTexts = {
    ElementTexts(
      Some(ElementText.label(texts)),
    )
  }

  def placeholder(texts: Map[Language, String])(implicit supportedLangs: Seq[Language]): ElementTexts = {
    ElementTexts(
      placeholder = Some(ElementText.placeholder(texts)),
    )
  }

  def apply(elementType: ElementType)(implicit supportedLangs: Seq[Language]): ElementTexts = elementType match {
    case SPACER => ElementTexts()
    case _ => ElementTexts(ident(elementType))
  }

  implicit val jsonFormat: OFormat[ElementTexts] = Json.format[ElementTexts]

}

case class ElementText(textType: TextType, texts: Map[Language, String]) {

}

object ElementText {

  def simple(simpleLabel:String)(implicit supportedLangs: Seq[Language]): ElementText =
    ElementText(LABEL, supportedLangs.map(l => l -> simpleLabel).toMap)

  def label(texts: Map[Language, String])(implicit supportedLangs: Seq[Language]): ElementText =
    ElementText(LABEL, texts)

  def placeholder(texts: Map[Language, String])(implicit supportedLangs: Seq[Language]): ElementText =
    ElementText(PLACEHOLDER, texts)

  def tooltip(texts: Map[Language, String])(implicit supportedLangs: Seq[Language]): ElementText =
    ElementText(TOOLTIP, texts)

  def emptyLabel(implicit supportedLangs: Seq[Language]): ElementText = {
    ElementText(LABEL, empty)
  }

  def emptyPlaceholder(implicit supportedLangs: Seq[Language]): ElementText = {
    ElementText(PLACEHOLDER, empty)
  }

  def emptyTooltip(implicit supportedLangs: Seq[Language]): ElementText = {
    ElementText(TOOLTIP, empty)
  }

  private def empty(implicit supportedLangs: Seq[Language]) = {
    supportedLangs.map(l => l -> "").toMap
  }

  implicit def jsonToStringMap(texts: Map[Language, String]): Map[String, String] =
    texts.map { case (k, v) => k.entryName -> v }


  implicit val mapReads: Reads[Map[Language, String]] = (jv: JsValue) =>
    JsSuccess(jv.as[Map[String, String]].map { case (k, v) =>
      Language.withNameInsensitive(k) -> v
    })

  implicit val mapWrites: Writes[Map[Language, String]] = (map: Map[Language, String]) =>
    Json.obj(map.map { case (s, o) =>
      val ret: (String, JsValueWrapper) = s.entryName -> JsString(o)
      ret
    }.toSeq: _*)

  implicit val jsonMapFormat: Format[Map[Language, String]] = Format(mapReads, mapWrites)

  implicit val jsonFormat: OFormat[ElementText] = Json.format[ElementText]
  
}



