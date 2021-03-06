package pme123.form.server.control.auth

import javax.inject.Inject
import pme123.form.server.control.services.UserDBRepo
import pme123.form.server.entity.AuthUser
import pme123.form.shared.services.User.UserId

import scala.concurrent.{ExecutionContext, Future}

class IdentityService @Inject()(userDBRepo: UserDBRepo)
                               (implicit ec: ExecutionContext) {

  def isValidUser(username: UserId, pwd: String): Future[Boolean] =
    userDBRepo.containsAuthUser(username) // no password required

  def getUser(username: UserId): Future[AuthUser] =
    userDBRepo.selectAuthUser(username)
}
