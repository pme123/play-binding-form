package pme123.form.server.entity

import pme123.form.shared.{MockContainer, MockEntry}

class UrlMatcherTest
  extends UnitTest {

  "A UrlMatcher" should {
    val mock = MockContainer("swapi",
      Seq(
        MockEntry(12, "url", 200, "body"),
        MockEntry(14, "url/{{param1}}", 200, "body for '{{param1}}'"),
        MockEntry(13, "url/{{param1}}/{{param2}}", 200, "body for '{{param1}}' '{{param2}}'"),
      ))
    "return 200 and the body for a simple Url" in {
      val resp = UrlMatcher.getResponse(mock, "url")
      resp.status shouldBe 200
      resp.body shouldBe "body"
    }

    "return 404 for a simple Url that has no url" in {
      val resp = UrlMatcher.getResponse(mock, "badUrl")
      resp.status shouldBe 404
      resp.body shouldBe "There is no matching Url in the Service 'swapi'"
    }

    "return 200 and the body for a Url with Parameter" in {
      val resp = UrlMatcher.getResponse(mock, "url/peter/meier")
      resp.status shouldBe 200
      resp.body shouldBe "body for 'peter' 'meier'"
    }

    "return 200 and the body for a Url with only one Parameter" in {
      val resp = UrlMatcher.getResponse(mock, "url/peter")
      resp.status shouldBe 200
      resp.body shouldBe "body for 'peter'"
    }

    "return 404 for a Url that has more than 2 params" in {
      val resp = UrlMatcher.getResponse(mock, "url/peter/meier/muller")
      resp.status shouldBe 404
      resp.body shouldBe "There is no matching Url in the Service 'swapi'"
    }
  }

}
