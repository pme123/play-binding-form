package pme123.form.server.boundary

import cats.data.NonEmptyList
import doobie._
import doobie.implicits._
import doobie.util.fragment.Fragment
import javax.inject._
import play.api.libs.json._
import play.api.mvc._
import pme123.form.server.boundary.services.{SPAComponents, SPAController}
import pme123.form.server.control.FormDBRepo
import pme123.form.shared._

import scala.concurrent.{ExecutionContext, Future}

/**
  * This class creates the actions and the websocket needed.
  * Original see here: https://github.com/playframework/play-scala-websocket-example
  */
@Singleton
class FormApi @Inject()(formDBRepo: FormDBRepo,
                            val spaComps: SPAComponents)
                           (implicit val ec: ExecutionContext)
  extends SPAController(spaComps) {

  def productTags(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    formDBRepo.selectProductTags()
      .map(tags =>
        Ok(Json.toJson(tags)).as(JSON)
      )
  }

  def formTags(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    formDBRepo.selectFormTags()
      .map(tags =>
        Ok(Json.toJson(tags)).as(JSON)
      )
  }

  def formCategories(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    formDBRepo.selectCategories()
      .map(categories =>
        Ok(Json.toJson(FormCategories(categories))).as(JSON)
      )
  }

  def formProducts(formCategory: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    (for {
      cat <- formDBRepo.selectCategory(formCategory)
      products <- formDBRepo.selectProducts(fr"where p.category = $formCategory")
    } yield FormProducts(cat, products))
      .map(products =>
        Ok(Json.toJson(products)).as(JSON)
      )
  }

  def forms(productIdent: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    (for {prod <- formDBRepo.selectProduct(productIdent)
          forms <- formDBRepo.selectForms(fr"where p.product = $productIdent")
    } yield Forms(prod, forms)
      ).map(products =>
      Ok(Json.toJson(products)).as(JSON)
    )
  }

  def form(formIdent: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    formDBRepo.selectForm(formIdent)
      .map(form =>
        Ok(Json.toJson(form)).as(JSON)
      )
  }

  def filter(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    request.body.asText.map { body =>
      Json.parse(body).validate[FormFilter]
        .map(filter)
        .getOrElse(Future.successful(Seq[Form]()))
        .map(forms =>
          Ok(Json.toJson(forms)).as(JSON)
        )
    }.getOrElse(Future.successful(Ok(JsArray()).as(JSON)))
  }

  private def filter(formFilter: FormFilter): Future[Seq[Form]] =
    if (formFilter.nonEmpty) {
      val filters = Seq(
        formFilter.formDescr.map(f => s"%$f%").map(f => fr"lower(p.descr) like $f"),
        formFilter.product.map(f => s"%$f%").map(f => fr"lower(pp.name) like $f"),
        inFilter(fr"pp.category", formFilter.categories),
        tagsFilter(fr"pp.tags", formFilter.productTags),
        tagsFilter(fr"p.tags", formFilter.formTags)
      )
      val frs = filters
        .filter(_.nonEmpty)
        .map(_.get) match {
        case Nil => fr""
        case x :: Nil => fr"where" ++ x
        case x :: tail => tail.foldLeft(fr"where" ++ x)((a, b) => a ++ fr"AND" ++ b)
      }
      formDBRepo.selectForms(frs)
    } else
      formDBRepo.selectForms() // return one page of animals

  private def inFilter(field: Fragment, ids: Seq[String]): Option[Fragment] =
    ids match {
      case Nil => None
      case x :: tail => Some(Fragments.in(field, NonEmptyList(x, tail)))
    }

  private def tagsFilter(field: Fragment, tags: Seq[String]): Option[Fragment] =
    tags.map(t=>s"%$t%") match {
      case Nil => None
      case x :: tail =>
        Some(tail.foldLeft(field ++ fr"like $x")((a, b) => a ++ fr"OR" ++ field ++ fr"like $b"))
    }
}
