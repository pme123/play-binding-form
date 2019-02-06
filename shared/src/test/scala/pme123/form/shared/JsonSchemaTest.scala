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

import JsonSchemaTest._
  "a JsonSchema" should {
    "be encoded and decoded" in {
      val schemaRoot = JsonSchema.fromJson(simpleJson)
      val json = Json.toJson(schemaRoot)
      info(Json.prettyPrint(json))
    }
  }


}

object JsonSchemaTest {
  val simpleJson = """{
    "title": "Example Schema",
    "type": "object",
    "definitions" : {
      "Address": {
      "type": "object",
      "properties": {
      "number" : { "type": "integer" },
      "street" : { "type": "string" }
    }
    },
      "ErdosNumber": {
      "type": "integer"
    }
    },
    "properties": {
      "name": {
      "type": "array",
      "items": { "type": "string" }
    },
      "age": {
      "description": "Age in years",
      "type": "integer"
    },
      "address" : { "$ref" : "#/definitions/Address" },
      "erdosNumber" : { "$ref" : "#/definitions/ErdosNumber" }
    }
  }"""

}