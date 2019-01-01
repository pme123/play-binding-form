package pme123.form.client.form

import com.thoughtworks.binding.Binding.Var
import pme123.form.client.form.UIFormElem.ChangeEvent
import pme123.form.client.services.UIStore.supportedLangs
import pme123.form.shared.ElementType.{SPACER, TEXTFIELD}
import pme123.form.shared._


case class UIFormElem(
                       identVar: Var[String],
                       elementTypeVar: Var[ElementType],
                       dataTypeVar: Var[DataType],
                       textsVar: Var[ElementTexts],
                       extrasVar: Var[ExtraProperties],
                       valueVar: Var[Option[String]],
                       requiredVar: Var[Boolean],
                       inlineVar: Var[Boolean],
                       readOnlyVar: Var[Boolean],
                       layoutWideVar: Var[LayoutWide],
                       elemEntriesVar: Var[ElementEntries],
                       validationsVar: Var[Validations],
                       changeEvent: ChangeEvent,
                     ) {

  val hasTexts: Boolean = textsVar.value.hasTexts
  val hasExtras: Boolean = extrasVar.value.hasExtras
  val hasEntries: Boolean = elemEntriesVar.value.hasEntries
  val hasValidations: Boolean = validationsVar.value.hasValidations

  def toBaseElement: BaseElement = BaseElement(
    identVar.value,
    elementTypeVar.value,
    dataTypeVar.value,
    textsVar.value,
    extrasVar.value,
    valueVar.value,
    requiredVar.value,
    inlineVar.value,
    readOnlyVar.value,
    layoutWideVar.value,
    elemEntriesVar.value,
    validationsVar.value,
  )


  def isViewable: Boolean = elementTypeVar.value match {
    case SPACER => false
    case _ => true
  }

  lazy val wideClass: String = layoutWideVar.value.entryName.toLowerCase
}

object UIFormElem {

  type ChangeEvent = Option[String => Unit]

  def apply(elem: BaseElement = BaseElement(TEXTFIELD),
            changeEvent: ChangeEvent = None): UIFormElem = {
    UIFormElem(
      Var(elem.ident),
      Var(elem.elementType),
      Var(elem.dataType),
      Var(elem.texts),
      Var(elem.extras),
      Var(elem.value),
      Var(elem.required),
      Var(elem.inline),
      Var(elem.readOnly),
      Var(elem.layoutWide),
      Var(elem.elemEntries),
      Var(elem.validations),
      changeEvent,
    )
  }

}
