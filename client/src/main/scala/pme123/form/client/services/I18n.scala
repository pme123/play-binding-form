package pme123.form.client.services

import pme123.form.shared.{ElementEntries, ElementEntry, ElementText, I18nEnum}
import pme123.form.shared.services.Language

object I18n {

  def apply(labelI18NKey: String, params: String*)(implicit supportedLangs: Seq[Language]): Map[Language, String] =
    supportedLangs.map(l => l -> apply(l, labelI18NKey, params: _*)).toMap


  def apply(activeLang: Language, labelI18NKey: String, params: String*): String =
    Messages(activeLang.abbreviation, labelI18NKey, params: _*)

  def enumEntries(enums: Seq[I18nEnum])(implicit supportedLangs: Seq[Language]): ElementEntries = {
    ElementEntries(
      enums.map(enum => ElementEntry(enum.key, ElementText.label(I18n(enum.i18nKey))))
    )
  }
}
