package pme123.form.shared

import julienrf.json.derived
import play.api.libs.json.OFormat

case class FormCategories(
                          categories: List[FormCategory] = Nil
                        ) {

}

object FormCategories {
  implicit val jsonFormat: OFormat[FormCategories] = derived.oformat[FormCategories]()

}

case class FormCategory(ident: String, name: String, subTitle: String)
  extends Identifiable {

  def styleName: String = ident

  def identPrefix: String = ident.take(3).toUpperCase

  val link: String = s"#${FormCategory.name}/$ident"

}

object FormCategory {

  def name: String = "category"

  implicit val jsonFormat: OFormat[FormCategory] = derived.oformat[FormCategory]()
}

