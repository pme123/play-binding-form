package pme123.form.client

import pme123.form.client.FormUIStore.supportedLangs
import pme123.form.client.UIFormElem.ChangeEvent
import pme123.form.shared.ElementType.TEXTFIELD
import pme123.form.shared.{BaseElement, ExtraProp}

case class UIFormElem(
                       elem: BaseElement = BaseElement(TEXTFIELD, supportedLangs),
                       extras: Map[ExtraProp, UIFormElem] = Map.empty,
                       changeEvent: ChangeEvent = None,
                     ) {
  lazy val wideClass: String = elem.layoutWide.entryName.toLowerCase
}

object UIFormElem {

  type ChangeEvent = Option[String => Unit]

  def apply(elem: BaseElement,
            changeEvent: ChangeEvent): UIFormElem = {

        UIFormElem(
          elem,
          elem.extras
            .map{case (ep, be) => (ep, UIFormElem(be))},
          changeEvent
        )
  }

}
