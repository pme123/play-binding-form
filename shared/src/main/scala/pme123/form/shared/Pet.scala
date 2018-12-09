package pme123.form.shared

import enumeratum.{Enum, EnumEntry}
import julienrf.json.derived
import play.api.libs.json.OFormat

import scala.collection.immutable
import pme123.form.shared.services._

case class Forms(
                 formProduct: FormProduct,
                 forms: List[Form] = Nil
               )

object Forms {
  implicit val jsonFormat: OFormat[Forms] = derived.oformat[Forms]()

}

case class Form(
                itemIdent: String,
                descr: String,
                price: Double,
                product: FormProduct,
                status: FormStatus = FormStatus.Available,
                tags: Set[String] = Set.empty,
                photoUrls: Set[String] = Set.empty
              )
  extends Identifiable {

  val link = s"#${Form.name}/${product.category.ident}/${product.productIdent}/$itemIdent"

  val firstPhotoUrl: String = s"images/catalog/${photoUrls.headOption.getOrElse("noimage.png")}"

  val ident: String = itemIdent

  val tagsString: String = tags.mkString(",")
  val photoUrlsString: String = photoUrls.mkString(",")

  val priceAsStr = f"$$ $price%.2f"
}

object Form {

  def name: String = "form"

  def create(
             itemIdent: String,
             descr: String,
             price: Double,
             product: FormProduct,
             status: String,
             tags: String,
             photoUrls: String
           ): Form = new Form(itemIdent, descr, price, product, FormStatus.withNameInsensitive(status), tags.splitToSet, photoUrls.splitToSet)

  implicit val jsonFormat: OFormat[Form] = derived.oformat[Form]()

}

sealed trait FormStatus extends EnumEntry

// see https://github.com/lloydmeta/enumeratum#usage
object FormStatus
  extends Enum[FormStatus] {

  val values: immutable.IndexedSeq[FormStatus] = findValues

  case object Available extends FormStatus

  case object Pending extends FormStatus

  case object Adopted extends FormStatus

  implicit val jsonFormat: OFormat[FormStatus] = derived.oformat[FormStatus]()
}

