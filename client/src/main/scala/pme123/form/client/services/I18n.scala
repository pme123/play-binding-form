package pme123.form.client.services

import pme123.form.shared.services.Language

object I18n {

  def apply(labelI18NKey: String)(implicit supportedLangs: Seq[Language]): Map[Language, String] =
    supportedLangs.map(l => l -> Messages(l.abbreviation, labelI18NKey)).toMap


  def apply(activeLang: Language, labelI18NKey: String): String =
    Messages(activeLang.abbreviation, labelI18NKey)

}
