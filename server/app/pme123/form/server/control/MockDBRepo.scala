package pme123.form.server.control

import doobie.implicits._
import doobie.util.fragment.Fragment
import javax.inject.Inject
import play.api.libs.json.{JsError, JsSuccess, Json}
import pme123.form.server.control.services.DoobieDB
import pme123.form.server.entity.JsonParseException
import pme123.form.shared.MockContainer

import scala.concurrent.{ExecutionContext, Future}


class MockDBRepo @Inject()(val formConf: FormConfiguration)
                          (implicit val ec: ExecutionContext)
  extends DoobieDB {


  def persist(mock: MockContainer): Future[MockContainer] = {
    selectMaybeMock(mock.ident)
      .flatMap {
        case Some(_) =>
          updateMock(mock)
        case None =>
          insertMock(mock)
      }.map(_ => mock)
  }

  def idents(): Future[List[String]] = {
    selectMocks()
      .map(_.map(_.ident))
  }

  def insertMock(mockCont: MockContainer): Future[Int] = {
    val mockContent = Json.toJson(mockCont).toString()
    update(
      sql"""insert into mock (ident, content)
             values (${mockCont.ident}, $mockContent)""")
  }

  def updateMock(mockCont: MockContainer): Future[Int] = {
    val mockContent = Json.toJson(mockCont).toString()
    update(
      sql"""update mock
              set content = $mockContent
             where ident = ${mockCont.ident}""")
  }

  def selectMock(ident: String): Future[MockContainer] =
    selectMaybeMock(ident).map(_.get)

  def selectMaybeMock(ident: String): Future[Option[MockContainer]] =
    selectMocks(fr"where m.ident = $ident")
      .map(_.headOption)

  private def selectMocks(where: Fragment = fr""): Future[List[MockContainer]] =
    select((fr"""select m.ident, m.content
                     from mock m
         """ ++ where)
      .query[(String, String)]
      .map { case (_, content) =>
        Json.parse(content)
          .validate[MockContainer] match {
          case JsSuccess(value, _) =>
            value
          case error: JsError =>
            throw JsonParseException(error)
        }
      }
    )

}
