package pme123.form.shared

trait I18nEnum {
  def entryName: String

  def key: String = entryName.toLowerCase

  def i18nKey: String

}
