package pme123.form.shared

import pme123.form.shared.BaseElement.ident
import pme123.form.shared.ElementType.SPACER
import pme123.form.shared.TextType.{LABEL, PLACEHOLDER, TOOLTIP}
import pme123.form.shared.services.Language

case class ElementTexts(label: Option[ElementText] = None,
                        placeholder: Option[ElementText] = None,
                        tooltip: Option[ElementText] = None) {

  val hasTexts: Boolean = label.nonEmpty || placeholder.nonEmpty || tooltip.nonEmpty

  def textForLabel(lang: Language): String = label.map(_.textFor(lang)).getOrElse("")

  def textForPlaceholder(lang: Language): String = placeholder.map(_.textFor(lang)).getOrElse("")

  def textForTooltip(lang: Language): String = tooltip.map(_.textFor(lang)).getOrElse("")

}

object ElementTexts {
  def apply(defaultLabel: String)(implicit supportedLangs: Seq[Language]): ElementTexts = {
    ElementTexts(
      Some(ElementText.label(supportedLangs.map(_ -> defaultLabel).toMap)),
    )
  }

  def label(texts: Map[Language, String])(implicit supportedLangs: Seq[Language]): ElementTexts = {
    ElementTexts(
      Some(ElementText.label(texts)),
    )
  }

  def placeholder(texts: Map[Language, String])(implicit supportedLangs: Seq[Language]): ElementTexts = {
    ElementTexts(
      placeholder = Some(ElementText(PLACEHOLDER, texts)),
    )
  }

  def apply(elementType: ElementType)(implicit supportedLangs: Seq[Language]): ElementTexts = elementType match {
    case SPACER => ElementTexts()
    case _ => ElementTexts(ident(elementType))
  }
}

case class ElementText(textType: TextType, texts: Map[Language, String]) {

  def textFor(lang: Language): String =
    texts.getOrElse(lang, texts
      .values
      .headOption
      .getOrElse(""))

}

object ElementText {

  def label(texts: Map[Language, String])(implicit supportedLangs: Seq[Language]): ElementText =
    ElementText(LABEL, texts)

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
}



