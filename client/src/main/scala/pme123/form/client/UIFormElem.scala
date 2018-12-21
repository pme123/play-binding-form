package pme123.form.client

import pme123.form.client.UIFormElem.ChangeEvent
import pme123.form.client.services.UIStore.supportedLangs
import pme123.form.shared.ElementType.{SPACER, TEXTFIELD}
import pme123.form.shared.{BaseElement, ExtraProp}

case class UIFormElem(
                       elem: BaseElement = BaseElement(TEXTFIELD),
                       extras: Map[ExtraProp, UIFormElem] = Map.empty,
                       changeEvent: ChangeEvent = None,
                     ) {
  // if the value is not set the element is read only like title
  val isEditable: Boolean = elem.value.nonEmpty

  def isViewable: Boolean = elem.elementType match {
    case SPACER => false
    case _ => true
  }

  lazy val wideClass: String = elem.layoutWide.entryName.toLowerCase
}

object UIFormElem {

  type ChangeEvent = Option[String => Unit]

  def apply(elem: BaseElement,
            changeEvent: ChangeEvent): UIFormElem = {
    val f = UIFormElem(
      elem,
      BaseElementExtras.extras(elem.elementType),
      changeEvent
    )
    f
  }

}
