package pme123.form.shared

import pme123.form.shared.services.SPAExtensions._
import julienrf.json.derived
import play.api.libs.json.OFormat

case class FormProducts(
                        category: FormCategory,
                        products: List[FormProduct] = Nil
                      ) {

}

object FormProducts {
  implicit val jsonFormat: OFormat[FormProducts] = derived.oformat[FormProducts]()

}


case class FormProduct(
                       productIdent: String,
                       name: String,
                       category: FormCategory,
                       tags: Set[String] = Set.empty
                     )
  extends Identifiable {

  val ident: String = productIdent

  val tagsString: String = tags.mkString(",")

  val link: String = s"#${FormProduct.name}/${category.ident}/$productIdent"

  def identPrefix: String = name.take(3).toUpperCase

}

object FormProduct {

  val name: String = "product"

  def apply(
             productIdent: String,
             name: String,
             category: FormCategory,
             tags: String
           ): FormProduct = new FormProduct(productIdent, name, category, tags.splitToSet)

  implicit val jsonFormat: OFormat[FormProduct] = derived.oformat[FormProduct]()

}
