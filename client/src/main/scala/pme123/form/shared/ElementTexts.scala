package pme123.form.shared

import pme123.form.client.services.Messages
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

  def label(labelI18NKey: String)(implicit supportedLangs: Seq[Language]): ElementTexts = {
    ElementTexts(
      ElementText.label(labelI18NKey),
      ElementText.emptyPlaceholder,
      ElementText.emptyTooltip
    )
  }
  def placeholder(labelI18NKey: String)(implicit supportedLangs: Seq[Language]): ElementTexts = {
    ElementTexts(
      ElementText.emptyLabel,
      ElementText.placeholder(labelI18NKey),
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

  private def texts(labelI18NKey: String)(implicit supportedLangs: Seq[Language]) =
    supportedLangs.map(l => l -> Messages(l.abbreviation, labelI18NKey)).toMap

  def label(labelI18NKey: String)(implicit supportedLangs: Seq[Language]): ElementText = {

    ElementText(LABEL, texts(labelI18NKey))
  }

  def placeholder(labelI18NKey: String)(implicit supportedLangs: Seq[Language]): ElementText = {

    ElementText(PLACEHOLDER, texts(labelI18NKey))
  }

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



