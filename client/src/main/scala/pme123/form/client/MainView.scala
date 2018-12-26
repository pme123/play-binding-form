package pme123.form.client

import com.thoughtworks.binding.Binding
import org.scalajs.dom.raw.HTMLElement
import pme123.form.client.services.ClientUtils

import scala.util.matching.Regex

trait MainView
  extends ClientUtils {

  def link: String

  def icon: String

  lazy val hashRegex: Regex = s"""#$link""".r

  def create(): Binding[HTMLElement]

}

