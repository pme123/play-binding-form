package pme123.form.shared

import pme123.form.client.services.Messages
import pme123.form.shared.TextType.{LABEL, PLACEHOLDER, TOOLTIP}
import pme123.form.shared.services.Language

case class ElementTexts(label: ElementText,
                        placeholder: ElementText,
                        tooltip: ElementText) {

}

object ElementTexts {
  def apply(supportedLangs: Seq[Language], defaultLabel: String): ElementTexts = {
    ElementTexts(
      ElementText(LABEL, supportedLangs.map(_ -> defaultLabel).toMap),
      ElementText(PLACEHOLDER, supportedLangs.map(_ -> "").toMap),
      ElementText(TOOLTIP, supportedLangs.map(_ -> "").toMap)
    )
  }

  def label(labelI18NKey: String)(implicit supportedLangs: Seq[Language]): ElementTexts = {
    ElementTexts(
      ElementText.label(labelI18NKey),
      ElementText(PLACEHOLDER, supportedLangs.map(_ -> "").toMap),
      ElementText(TOOLTIP, supportedLangs.map(_ -> "").toMap)
    )
  }
  def placeholder(labelI18NKey: String)(implicit supportedLangs: Seq[Language]): ElementTexts = {
    ElementTexts(
      ElementText(LABEL, supportedLangs.map(_ -> "").toMap),
      ElementText.placeholder(labelI18NKey),
      ElementText(TOOLTIP, supportedLangs.map(_ -> "").toMap)
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

  def empty(supportedLangs: Seq[Language]): ElementText = {

    ElementText(LABEL, supportedLangs.map(l => l -> "").toMap)
  }
}



