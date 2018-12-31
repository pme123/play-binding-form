package pme123.form.server.control

import doobie.implicits._
import doobie.util.fragment.Fragment
import javax.inject.Inject
import play.api.libs.json.{JsError, JsSuccess, Json}
import pme123.form.server.control.services.DoobieDB
import pme123.form.server.entity.JsonParseException
import pme123.form.shared.DataContainer

import scala.concurrent.{ExecutionContext, Future}


class DataDBRepo @Inject()()
                          (implicit val ec: ExecutionContext)
  extends DoobieDB {


  def persist(data: DataContainer): Future[DataContainer] = {
    selectMaybeData(data.ident)
      .flatMap {
        case Some(sData) =>
          updateData(data)
        case None =>
          insertData(data)
      }.map(_ => data)
  }

  def idents(): Future[List[String]] = {
    selectDatas()
      .map(_.map(_.ident))
  }

  def insertData(dataCont: DataContainer): Future[Int] = {
    val dataContent = Json.toJson(dataCont).toString()
    update(
      sql"""insert into data (ident, content)
             values (${dataCont.ident}, $dataContent)""")
  }

  def updateData(dataCont: DataContainer): Future[Int] = {
    val dataContent = Json.toJson(dataCont).toString()
    update(
      sql"""update data
              set content = $dataContent
             where ident = ${dataCont.ident}""")
  }

  def selectData(ident: String): Future[DataContainer] =
    selectMaybeData(ident).map(_.get)

  def selectMaybeData(ident: String): Future[Option[DataContainer]] =
    selectDatas(fr"where d.ident = $ident")
      .map(_.headOption)

  private def selectDatas(where: Fragment = fr""): Future[List[DataContainer]] =
    select((fr"""select d.ident, d.content
                     from data d
         """ ++ where)
      .query[(String, String)]
      .map { case (_, content) =>
        Json.parse(content)
          .validate[DataContainer] match {
          case JsSuccess(value, _) =>
            value
          case error: JsError =>
            throw JsonParseException(error)
        }
      }
    )


}
