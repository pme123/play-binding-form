package pme123.form.shared

import pme123.form.shared.TextType.{LABEL, PLACEHOLDER, TOOLTIP}
import pme123.form.shared.services.Language

case class ElementTexts(label: ElementText,
                        placeholder: ElementText,
                        tooltip: ElementText) {

}

object ElementTexts {
  def apply(defaultLabel: String)(implicit supportedLangs: Seq[Language]): ElementTexts = {
    ElementTexts(
      ElementText(LABEL, supportedLangs.map(_ -> defaultLabel).toMap),
      ElementText.emptyPlaceholder,
      ElementText.emptyTooltip
    )
  }

  def label(texts: Map[Language, String])(implicit supportedLangs: Seq[Language]): ElementTexts = {
    ElementTexts(
      ElementText.label(texts),
      ElementText.emptyPlaceholder,
      ElementText.emptyTooltip
    )
  }

  def placeholder(texts: Map[Language, String])(implicit supportedLangs: Seq[Language]): ElementTexts = {
    ElementTexts(
      ElementText.emptyLabel,
      ElementText(PLACEHOLDER, texts),
      ElementText.emptyTooltip
    )
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



