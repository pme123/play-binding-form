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

  def apply(labelI18NKey: String, supportedLangs: Seq[Language]): ElementTexts = {
    ElementTexts(
      ElementText.label(labelI18NKey, supportedLangs),
      ElementText(PLACEHOLDER, supportedLangs.map(_ -> "").toMap),
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

  def label(labelI18NKey: String, supportedLangs: Seq[Language]): ElementText = {

      ElementText(LABEL, supportedLangs.map(l => l -> Messages(l.abbreviation, labelI18NKey)).toMap)
  }
}



