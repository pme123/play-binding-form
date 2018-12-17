package pme123.form.client

import com.thoughtworks.binding.Binding.Constants
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.{Event, HTMLElement}
import pme123.form.client.services.{ClientUtils, Messages, UIStore}
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
    val selElemV = UIFormStore.uiState.activePropElement.bind
    val selElem = selElemV
    <div class="ui top attached tabular menu">
      {Constants(
      PropTabType.values
        .filterNot(p => p == ENTRIES && !selElem.elem.hasEntries)
        .filterNot(p => p == TEXTS && !selElem.elem.hasTexts)
        .filterNot(p => p == VALIDATIONS && !selElem.elem.hasValidations)
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
      {Messages(activeLang.entryName, propTabType.i18nKey)}
    </a>
  }

  private def activePropTab(propTabType: PropTabType, activeType: PropTabType) =
    if (propTabType == activeType)
      "active"
    else
      ""

}
