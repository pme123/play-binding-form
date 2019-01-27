package pme123.form.server.entity

object UrlMatcher extends App {

  val urlPattern = """http://sapi.co/{{firstname}}/{{lastname}}"""
  val pattern = "^" + urlPattern
    .replace("{{", "(?<")
    .replace("}}", ">.+)") + "$"

  //val pattern = pattern2.r //"""^http://sapi.co/(?<firstname>.+)/(?<lastname>.+)+$""".r
  val groupRegex =
    """(?<=\{\{).+?(?=\})""".r

  println(groupRegex.findAllIn(urlPattern).toSeq)

  val groups = groupRegex.findAllIn(urlPattern).toList
  //.map(_.replace("{", "").replace("}", ""))
  println(groups)

  def matchAll(str: String): Option[Map[String, String]] = pattern.r
    .findFirstMatchIn(str)
    .map { matched =>
      groups.map(name => name -> matched.group(name)).toMap
    }

  println(matchAll("http://sapi.co/peter/muller"))
}
