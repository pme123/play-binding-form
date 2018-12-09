package pme123.form.client

import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.HTMLElement
import pme123.form.client.services.ClientUtils
import pme123.form.shared.{FormCategory, FormProduct}

trait MainView
  extends ClientUtils {

  def link: String

  def create(): Binding[HTMLElement]

}

