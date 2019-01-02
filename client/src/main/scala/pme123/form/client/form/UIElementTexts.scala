package pme123.form.client.form

import com.thoughtworks.binding.Binding.Var
import pme123.form.shared.services.Language
import pme123.form.shared.{ElementText, ElementTexts, TextType}

case class UIElementTexts(label: Option[UIElementText] = None,
                          placeholder: Option[UIElementText] = None,
                          tooltip: Option[UIElementText] = None) {

  def toTexts: ElementTexts = ElementTexts(
    label.map(_.toText),
    placeholder.map(_.toText),
    tooltip.map(_.toText),
  )

  val hasTexts: Boolean = label.nonEmpty || placeholder.nonEmpty || tooltip.nonEmpty

  def textForLabel(lang: Language): Var[String] = label.map(_.textFor(lang)).getOrElse(Var(""))

  def textForPlaceholder(lang: Language): Var[String] = placeholder.map(_.textFor(lang)).getOrElse(Var(""))

  def textForTooltip(lang: Language): Var[String] = tooltip.map(_.textFor(lang)).getOrElse(Var(""))

}

object UIElementTexts {
  def apply(elementTexts: ElementTexts): UIElementTexts =
    UIElementTexts(elementTexts.label.map(UIElementText.apply),
      elementTexts.placeholder.map(UIElementText.apply),
      elementTexts.tooltip.map(UIElementText.apply))


}

case class UIElementText(textType: TextType, texts: Map[Language, Var[String]]) {

  def textFor(lang: Language): Var[String] =
    texts.getOrElse(lang,
     texts
        .values
        .headOption
        .getOrElse(Var("")))

  def toText: ElementText =
    ElementText(textType,
      texts.map { case (k, v) => k -> v.value })

}

object UIElementText {
  def apply(elementText: ElementText): UIElementText = {
    UIElementText(elementText.textType,
      elementText.texts
        .map { case (k, v) => k -> Var(v) }
    )
  }
}