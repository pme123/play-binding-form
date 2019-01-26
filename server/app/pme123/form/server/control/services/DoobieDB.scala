package pme123.form.server.control.services

import cats.effect.IO
import doobie.Fragment
import doobie.implicits._
import doobie.util.query.Query0
import doobie.util.transactor.Transactor
import doobie.util.transactor.Transactor.Aux
import pme123.form.server.control.FormConfiguration

import scala.concurrent.{ExecutionContext, Future, blocking}

trait DoobieDB {

  implicit def ec: ExecutionContext

  def formConf: FormConfiguration

  protected val xa: Aux[IO, Unit] = Transactor.fromDriverManager[IO](
    formConf.dbDriver,
    formConf.dbUrl,
    formConf.dbUsername,
    formConf.dbPassword,
  )

  protected def update(sql: Fragment): Future[Int] =
    Future(
      blocking(
        sql
          .update
          .run
          .transact(xa)
          .unsafeRunSync
      ))

  protected def select[T](query: Query0[T]): Future[List[T]] =
    Future(
      blocking(
        query
          .stream
          .transact(xa)
          .take(50)
          .compile
          .toList
          .unsafeRunSync
      ))

}
