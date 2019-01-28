package pme123.form.server.entity

import org.fusesource.scalate.{Template, TemplateEngine}
import pme123.form.shared.{MockContainer, MockEntry}

object UrlMatcher {

  private val engine = new TemplateEngine()

  private val groupRegex =
    """(?<=\{\{).+?(?=\})""".r

  def getResponse(mock: MockContainer, path: String): HttpResponse = {
    mock.mocks.find { me =>
      path.matches(regexUrl(me.url))
    }.map { me =>
      createBody(path, me)
    }.getOrElse(HttpResponse(404, s"There is no matching Url in the Service '${mock.ident}'"))
  }

  private def extractParams(mockUrl: String, path: String) = {
    val groups: Seq[String] = groupRegex.findAllIn(mockUrl).toList
    val matched: Map[String, String] = regexUrl(mockUrl).r
      .findFirstMatchIn(path)
      .map { matched =>
        groups.map(name => name -> matched.group(name)).toMap
      }.getOrElse(Map.empty)
    matched
  }

  private def regexUrl(url: String) = {
    "^" + url
      .replace("{{", "(?<")
      .replace("}}", ">.+)") + "$"
  }

  private def createBody(path: String, me: MockEntry) = {
    val matched = extractParams(me.url, path)
    val templ: Template = engine.compileMoustache(me.content)
    HttpResponse(200, engine.layout("notused", templ, matched))
  }

}
