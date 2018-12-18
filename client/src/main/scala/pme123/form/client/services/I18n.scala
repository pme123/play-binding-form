package pme123.form.client.services

import pme123.form.shared.services.Language

object I18n {

  def apply(labelI18NKey: String, params: String*)(implicit supportedLangs: Seq[Language]): Map[Language, String] =
    supportedLangs.map(l => l -> Messages(l.abbreviation, labelI18NKey, params: _*)).toMap


  def apply(activeLang: Language, labelI18NKey: String, params: String*): String =
    Messages(activeLang.abbreviation, labelI18NKey, params: _*)

}
