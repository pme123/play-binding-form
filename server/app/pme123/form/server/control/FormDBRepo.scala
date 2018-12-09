package pme123.form.server.control

import doobie.Fragment
import doobie.implicits._
import javax.inject.Inject
import pme123.form.server.control.services.DoobieDB
import pme123.form.shared.services._
import pme123.form.shared.{Form, FormCategory, FormProduct}

import scala.concurrent.{ExecutionContext, Future}


class FormDBRepo @Inject()()
                         (implicit val ec:ExecutionContext)
  extends DoobieDB {

  def insertCategory(cat: FormCategory): Future[Int] =
    insert(
      sql"""insert into form_categories (ident, name, sub_title)
             values (${cat.ident}, ${cat.name}, ${cat.subTitle})""")

  def selectCategories(where: Fragment = fr""): Future[List[FormCategory]] =
    select(
      (fr"select ident, name, sub_title from form_categories" ++ where)
        .query[FormCategory]
    )

  def selectCategory(ident: String): Future[FormCategory] =
    selectCategories(fr"where ident = $ident")
      .map(_.head)

  def insertProduct(prod: FormProduct): Future[Int] =
    insert(
      sql"""insert into form_products (ident, name, category, tags)
             values (${prod.ident}, ${prod.name}, ${prod.category.ident}, ${prod.tagsString})"""
    )

  def selectProducts(where: Fragment = fr""): Future[List[FormProduct]] =
    select(
      (fr"""select p.ident, p.name, p.tags, c.ident, c.name, c.sub_title
               from form_products p
               left join form_categories c
               on p.category = c.ident
         """ ++ where)
        .query[(String, String, String, FormCategory)]
        .map { case (ident, name, tags, cat) => FormProduct.apply(ident, name, cat, tags) }
    )

  def selectProduct(ident: String): Future[FormProduct] =
    selectProducts(fr"where p.ident = $ident")
      .map(_.head)

  def selectProductTags(): Future[Set[String]] =
    select(
      sql"""select p.tags
               from form_products p
         """
        .query[String]
    ).map(_.toSet
      .flatMap((s: String) => s.splitToSet))

  def insertForm(form: Form): Future[Int] =
    insert(
      sql"""insert into forms (ident, descr, price, product, status, tags, photo_urls)
             values (${form.ident}, ${form.descr}, ${form.price}, ${form.product.ident}, ${form.status.entryName}, ${form.tagsString}, ${form.photoUrlsString})"""
    )

  def selectForms(where: Fragment = fr""): Future[List[Form]] =
    select((fr"""select p.ident, p.descr, p.price, p.status, p.tags, p.photo_urls,
                        pp.ident, pp.name, pp.tags,
                        c.ident, c.name, c.sub_title
                     from forms p
                     left join form_products pp
                     on p.product = pp.ident
                     left join form_categories c
                     on pp.category = c.ident
         """ ++ where)
      .query[(String, String, Double, String, String, String,
      String, String, String, FormCategory)]
      .map { case (pIdent, pDescr, pPrice, pStatus, pTags, pPhotoUrls, ident, name, tags, cat) =>
        Form.create(pIdent, pDescr, pPrice, FormProduct.apply(ident, name, cat, tags), pStatus, pTags, pPhotoUrls)
      }
    )

  def selectForm(ident: String): Future[Form] =
    selectForms(fr"where p.ident = $ident")
      .map(_.head)

  def selectFormTags(): Future[Set[String]] =
    select(
      sql"""select p.tags
               from forms p
         """
        .query[String]
    ).map(_.toSet
      .flatMap((s: String) => s.splitToSet))

}
