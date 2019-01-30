package pme123.form.server.control.services

import pme123.form.server.control.GuiceAcceptanceSpec

class UserDBSpec
  extends GuiceAcceptanceSpec {

  private lazy val userDBRepo = inject[UserDBRepo]

  "UserDBSpec" should {
    "initialize the User Table" in {
      userDBRepo.selectUsers()
        .map(users =>
          assert(users.size >= 3))
    }

  }
}


