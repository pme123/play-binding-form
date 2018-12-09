package pme123.form.client

import com.thoughtworks.binding.Binding.Constants
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.{Event, HTMLElement}
import org.scalajs.jquery.jQuery
import pme123.form.shared.PropTabType.{PROPERTIES, TEXTS}
import pme123.form.shared.services.Language
import pme123.form.shared._
import com.softwaremill.quicklens._
import pme123.form.shared.ElementType.TEXTFIELD
import pme123.form.shared.TextType.{LABEL, PLACEHOLDER, TOOLTIP}
import pme123.form.shared.services.Language.{DE, EN}
import FormUIStore.supportedLangs

object PropTab {

  @dom
  lazy val create: Binding[HTMLElement] = {
    val activeTab = FormUIStore.uiState.activePropTab.bind
    <div class="ui bottom attached segment">
      {activeTab match {
      case PROPERTIES =>
        PropertiesTab.create.bind
      case TEXTS =>
        TextsTab.create.bind

    }}
    </div>
  }

}

case object PropertiesTab {

  @dom
  lazy val create: Binding[HTMLElement] = {
    <div>
      {//
      elementTypeSelect.bind}{//
      defaultValueInput.bind}{//
      layoutWideSelect.bind}{//
      elementExtras.bind}
    </div>
  }

  @dom
  private lazy val defaultValueInput: Binding[HTMLElement] = {
    <div class="field">
      {BaseElementDiv(
      UIFormElem(BaseElement(
        "elemDefaultValue",
        TEXTFIELD,
        ElementTexts(
          ElementText(LABEL, Map(DE -> "Standart Wert", EN -> "Default Value")),
          ElementText(PLACEHOLDER, Map(DE -> "Standart Wert", EN -> "Default Value")),
          ElementText(TOOLTIP, Map(DE -> "Dieser Wert wird als Startwert angezeigt", EN -> "This value is displayed in the element on start."))
        )),
        Some(PropertyUIStore.changeDefaultValue _)
      )
    ).bind}
    </div>
  }

  @dom
  private lazy val elementTypeSelect: Binding[HTMLElement] = {
    val elem = FormUIStore.uiState.selectedElement.bind
    val elementType = elem.value.elem.elementType

    <div class="field">
      <label>Element Type</label>
      <div class="ui selection dropdown">
        <input type="hidden"
               id="elementTypeId"
               value={elementType.entryName}
               onchange={_: Event =>
                 PropertyUIStore.changeElementType(elementTypeId.value)}/>
        <i class="dropdown icon"></i>
        <div class="default text">
          {elementType.entryName}
        </div>
        <div class="menu">
          {Constants(ElementType.values.map(_.entryName).map(selectItem): _*).map(_.bind)}
        </div>
      </div>
    </div>
  }

  @dom
  private lazy val layoutWideSelect: Binding[HTMLElement] = {
    val elem = FormUIStore.uiState.selectedElement.bind
    val layoutWide = elem.value.elem.layoutWide

    <div class="field">
      <label>Layout Wide</label>
      <div class="ui selection dropdown">
        <input type="hidden"
               id="layoutWideId"
               value={layoutWide.entryName}
               onchange={_: Event =>
                 PropertyUIStore.changeLayoutWide(layoutWideId.value)}/>
        <i class="dropdown icon"></i>
        <div class="default text">
          {layoutWide.entryName}
        </div>
        <div class="menu">
          {Constants(LayoutWide.values.map(_.entryName).map(selectItem): _*).map(_.bind)}
        </div>
      </div>
    </div>
  }

  @dom
  private def selectItem(eType: String): Binding[HTMLElement] = {
    <div class="item" data:data-value={eType}>
      {eType}
    </div>
  }

  @dom
  private lazy val elementExtras: Binding[HTMLElement] = {
    val elem = FormUIStore.uiState.selectedElement.bind
    val extras = elem.bind.extras

    <div>
      {//
      Constants(
        extras
          .map { case (ep, uie) =>
            BaseElementDiv(
              uie.modify(_.changeEvent)
                .setTo(Some(PropertyUIStore.changeExtraProp(ep))))
          }.toSeq: _*)
        .map(_.bind)}
    </div>
  }

}

case object TextsTab {

  @dom
  lazy val create: Binding[HTMLElement] = {
    val uiFormElem = FormUIStore.uiState.selectedElement.bind.bind
    <div>
      {//
      Constants(
        elementTextDiv(uiFormElem.elem.texts.label),
        elementTextDiv(uiFormElem.elem.texts.placeholder),
        elementTextDiv(uiFormElem.elem.texts.tooltip)
      ).map(_.bind)}
    </div>
  }

  @dom
  def elementTextDiv(elementText: ElementText): Binding[HTMLElement] =
    <div>
      <h4>
        {elementText.textType.label}
      </h4>{//
      Constants(elementText.texts
        .map {
          case (l, t) =>
            textDiv(l, t, elementText.textType)
        }.toSeq: _*)
        .map(_.bind)}<br/>
    </div>

  @dom
  def textDiv(lang: Language, text: String, textType: TextType): Binding[HTMLElement] =
    <div class="field">
      <label>
        {lang.label}
      </label>
      <input id={s"${lang.abbreviation}-$textType"}
             type="text"
             value={text}
             onblur={_: Event =>
               val newText = jQuery(s"#${lang.abbreviation}-$textType").value().toString
               PropertyUIStore.changeText(lang, textType, newText)}/>
    </div>

}