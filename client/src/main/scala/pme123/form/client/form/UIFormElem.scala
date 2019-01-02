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
                       textsVar: Var[UIElementTexts],
                       extrasVar: Var[ExtraProperties],
                       valueVar: Var[Option[String]],
                       requiredVar: Var[Boolean],
                       inlineVar: Var[Boolean],
                       readOnlyVar: Var[Boolean],
                       layoutWideVar: Var[LayoutWide],
                       elemEntriesVar: Var[UIElementEntries],
                       validationsVar: Var[Validations],
                       changeEvent: ChangeEvent,
                     ) {

  val hasTexts: Boolean = textsVar.value.hasTexts
  val hasExtras: Boolean = extrasVar.value.hasExtras
  val hasValidations: Boolean = validationsVar.value.hasValidations

  def toBaseElement: BaseElement = BaseElement(
    identVar.value,
    elementTypeVar.value,
    dataTypeVar.value,
    textsVar.value.toTexts,
    extrasVar.value,
    valueVar.value,
    requiredVar.value,
    inlineVar.value,
    readOnlyVar.value,
    layoutWideVar.value,
    elemEntriesVar.value.toElementEntries,
    validationsVar.value,
  )


  def isViewable: Boolean = elementTypeVar.value match {
    case SPACER => false
    case _ => true
  }
}

object UIFormElem {

  type ChangeEvent = Option[String => Unit]

  def apply(elem: BaseElement = BaseElement(TEXTFIELD),
            changeEvent: ChangeEvent = None): UIFormElem = {
    UIFormElem(
      Var(elem.ident),
      Var(elem.elementType),
      Var(elem.dataType),
      Var(UIElementTexts(elem.texts)),
      Var(elem.extras),
      Var(elem.value),
      Var(elem.required),
      Var(elem.inline),
      Var(elem.readOnly),
      Var(elem.layoutWide),
      Var(UIElementEntries(elem.elemEntries)),
      Var(elem.validations),
      changeEvent,
    )
  }

}
