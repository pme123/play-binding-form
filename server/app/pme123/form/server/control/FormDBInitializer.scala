package pme123.form.server.control

import cats.implicits._
import doobie._
import doobie.implicits._
import javax.inject.{Inject, Singleton}
import pme123.form.server.control.services.DoobieDB

import scala.concurrent.ExecutionContext

@Singleton
class FormDBInitializer @Inject()()
                                 (implicit val ec: ExecutionContext)
  extends DoobieDB {

  val initForm: Int = initTable(
    sql"""
        DROP TABLE IF EXISTS form
      """,
    sql"""
        CREATE TABLE form (
          id   SERIAL,
          formId VARCHAR NOT NULL UNIQUE,
          content VARCHAR
        )"""
  )

  val initUser: Int = initTable(
    sql"""
        DROP TABLE IF EXISTS users
      """,
    sql"""
        CREATE TABLE users (
          id   SERIAL,
          username VARCHAR NOT NULL UNIQUE,
          groups VARCHAR,
          firstName VARCHAR,
          lastName VARCHAR,
          email VARCHAR,
          avatar VARCHAR,
          language VARCHAR
         )"""
  )

  private def initTable(drop: Fragment, create: Fragment): Int = {
    (drop.update.run, create.update.run)
      .mapN(_ + _).transact(xa).unsafeRunSync
  }


}