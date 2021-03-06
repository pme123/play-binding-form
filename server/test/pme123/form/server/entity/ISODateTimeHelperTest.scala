package pme123.form.server.entity

import java.time.ZoneId

class ISODateTimeHelperTest
  extends UnitTest
    with ISODateTimeHelper {

  val timezone: ZoneId = ZoneId.of("Europe/Zurich")


  "An ISO DateTime String" should {
    "be the same after creating a LocalDateTime" in {
      val isoDate = "2018-01-13T12:33"
      toISODateTimeString(
        toLocalDateTime(isoDate)
      ) should be(isoDate)
    }
  }
}
