package pme123.form.server.control

import doobie.implicits._
import doobie.util.fragment.Fragment
import javax.inject.Inject
import play.api.libs.json.{JsError, JsSuccess, Json}
import pme123.form.server.control.services.DoobieDB
import pme123.form.server.entity.JsonParseException
import pme123.form.shared.MappingContainer

import scala.concurrent.{ExecutionContext, Future}


class MappingDBRepo @Inject()()
                             (implicit val ec: ExecutionContext)
  extends DoobieDB {


  def persist(mapping: MappingContainer): Future[MappingContainer] = {
    selectMaybeMapping(mapping.ident)
      .flatMap {
        case Some(_) =>
          updateMapping(mapping)
        case None =>
          insertMapping(mapping)
      }.map(_ => mapping)
  }

  def idents(): Future[List[String]] = {
    selectMappings()
      .map(_.map(_.ident))
  }

  def insertMapping(mappingCont: MappingContainer): Future[Int] = {
    val mappingContent = Json.toJson(mappingCont).toString()
    update(
      sql"""insert into mapping (ident, content)
             values (${mappingCont.ident}, $mappingContent)""")
  }

  def updateMapping(mappingCont: MappingContainer): Future[Int] = {
    val mappingContent = Json.toJson(mappingCont).toString()
    update(
      sql"""update mapping
              set content = $mappingContent
             where ident = ${mappingCont.ident}""")
  }

  def selectMapping(ident: String): Future[MappingContainer] =
    selectMaybeMapping(ident).map(_.get)

  def selectMaybeMapping(ident: String): Future[Option[MappingContainer]] =
    selectMappings(fr"where m.ident = $ident")
      .map(_.headOption)

  private def selectMappings(where: Fragment = fr""): Future[List[MappingContainer]] =
    select((fr"""select m.ident, m.content
                     from mapping m
         """ ++ where)
      .query[(String, String)]
      .map { case (_, content) =>
        Json.parse(content)
          .validate[MappingContainer] match {
          case JsSuccess(value, _) =>
            value
          case error: JsError =>
            throw JsonParseException(error)
        }
      }
    )


}
