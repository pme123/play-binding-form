package pme123.form.client.form

import com.thoughtworks.binding.Binding.Var
import pme123.form.client.form.UIFormElem.ChangeEvent
import pme123.form.client.services.UIStore.supportedLangs
import pme123.form.shared.ElementType.{SPACER, TEXTFIELD}
import pme123.form.shared._


case class UIFormElem(
                       varIdent: Var[String],
                       elementTypeVar: Var[ElementType],
                       textsVar: Var[UIElementTexts],
                       extrasVar: Var[UIExtraProperties],
                       valueVar: Var[Option[String]],
                       requiredVar: Var[Boolean],
                       inlineVar: Var[Boolean],
                       readOnlyVar: Var[Boolean],
                       layoutWideVar: Var[LayoutWide],
                       elemEntriesVar: Var[UIElementEntries],
                       validationsVar: Var[UIValidations],
                       changeEvent: ChangeEvent,
                     ) {

  val hasTexts: Boolean = textsVar.value.hasTexts

  def toBaseElement: BaseElement = BaseElement(
    varIdent.value,
    elementTypeVar.value,
    textsVar.value.toTexts,
    extrasVar.value.toExtraProperties,
    valueVar.value,
    requiredVar.value,
    inlineVar.value,
    readOnlyVar.value,
    layoutWideVar.value,
    elemEntriesVar.value.toElementEntries,
    validationsVar.value.toValidations,
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
      Var(UIElementTexts(elem.texts)),
      Var(UIExtraProperties(elem.extras)),
      Var(elem.value),
      Var(elem.required),
      Var(elem.inline),
      Var(elem.readOnly),
      Var(elem.layoutWide),
      Var(UIElementEntries(elem.elemEntries)),
      Var(UIValidations(elem.validations)),
      changeEvent,
    )
  }

}
