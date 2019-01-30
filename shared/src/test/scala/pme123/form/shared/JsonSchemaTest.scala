package pme123.form.shared

import play.api.libs.json.Json
import pme123.form.shared.services.{Logging, UnitTest}

/**
  * Created by pascal.mengelt on 05.03.2015.
  *
  */
class JsonSchemaTest
  extends UnitTest
    with Logging {


  "a JsonSchema" should {
    "be encoded and decoded" in {
      val schemaRoot = JsonSchema.fromResource("simple.json")
      val json = Json.toJson(schemaRoot)
      info(Json.prettyPrint(json))
    }
  }


}
