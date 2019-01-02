package pme123.form.client.form

import com.thoughtworks.binding.Binding.Constants
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.{Event, HTMLElement}
import pme123.form.client.services.{ClientUtils, I18n, UIStore}
import pme123.form.shared.PropTabType
import pme123.form.shared.PropTabType.{ENTRIES, TEXTS, VALIDATIONS}

private[client] object PropertyMenu
  extends ClientUtils {

  // 1. level of abstraction
  // **************************
  @dom
  private[client] lazy val create: Binding[HTMLElement] = {
      <div class="ui segment">
        <form class="ui form">
          {propHeader.bind}{//
          menu.bind}{//
          PropTab.create.bind}
        </form>
      </div>
  }

  // 2. level of abstraction
  // **************************

  @dom
  private lazy val propHeader =
    <h4 class="ui dividing header">
      <i class={s"edit outline icon"}></i>
      Element Properties</h4>

  @dom
  private lazy val menu: Binding[HTMLElement] = {
    val uiElem = UIFormStore.uiState.selectedElement.bind.bind
    val elemType = uiElem.elementTypeVar.bind
    val texts = uiElem.textsVar.bind
    val validations = uiElem.validationsVar.bind
    <div class="ui top attached tabular menu">
      {Constants(
      PropTabType.values
        .filterNot(p => {
          p == ENTRIES && !elemType.hasEntries
        })
        .filterNot(p => {
          p == TEXTS && !texts.hasTexts
        })
        .filterNot(p => {
          p == VALIDATIONS && !validations.hasValidations
        })
        .map(menuItem): _*)
      .map(_.bind)}
    </div>
  }


  @dom
  private def menuItem(propTabType: PropTabType): Binding[HTMLElement] = {
    val activeLang = UIStore.activeLanguage.bind
    val activeType = UIFormStore.uiState.activePropTab.bind
    <a class={s"${activePropTab(propTabType, activeType)} item"}
       onclick={_: Event =>
         UIFormStore.changeActivePropTab(propTabType)}>
      {I18n(activeLang, propTabType.i18nKey)}
    </a>
  }

  private def activePropTab(propTabType: PropTabType, activeType: PropTabType) =
    if (propTabType == activeType)
      "active"
    else
      ""

}
