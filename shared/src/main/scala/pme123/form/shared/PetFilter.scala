package pme123.form.shared

import julienrf.json.derived
import play.api.libs.json.OFormat

case class FormFilter(formDescr: Option[String] = None,
                     product: Option[String] = None,
                     categories: Seq[FormFilter.Category] = Nil,
                     formTags: Seq[FormFilter.FormTag] = Nil,
                     productTags: Seq[FormFilter.ProductTag] = Nil) {

  val nonEmpty: Boolean =
    formDescr.nonEmpty || product.nonEmpty || categories.nonEmpty || formTags.nonEmpty || productTags.nonEmpty


  def withFormDescr(formDescr: Option[String]): FormFilter = {
    copy(formDescr = formDescr)
  }

  def withProduct(product: Option[String]): FormFilter = {
    copy(product = product)
  }

  def withCategories(categories: String): FormFilter = {
    if (categories.nonEmpty) {
      copy(categories = categories.split(","))
    } else copy(categories = Nil)
  }

  def withFormTags(tags: String): FormFilter = {
    if (tags.nonEmpty) {
      copy(formTags = tags.split(","))
    } else copy(formTags = Nil)
  }

  def withProductTags(tags: String): FormFilter = {
    if (tags.nonEmpty) {
      copy(productTags = tags.split(","))
    } else copy(productTags = Nil)
  }

}

object FormFilter {

  type Category = String
  type FormTag = String
  type ProductTag = String

  implicit val jsonFormat: OFormat[FormFilter] = derived.oformat[FormFilter]()


}
