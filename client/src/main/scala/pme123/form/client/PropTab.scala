package pme123.form.client

import com.softwaremill.quicklens._
import com.thoughtworks.binding.Binding.Constants
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.HTMLElement
import pme123.form.client.FormUIStore.supportedLangs
import pme123.form.shared.ElementType.{DROPDOWN, TEXTFIELD}
import pme123.form.shared.PropTabType.{PROPERTIES, TEXTS}
import pme123.form.shared.TextType.{LABEL, PLACEHOLDER, TOOLTIP}
import pme123.form.shared._
import pme123.form.shared.services.Language
import pme123.form.shared.services.Language.{DE, EN}

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
      {BaseElementDiv(
      UIFormElem(BaseElement(
        "elementTypeId",
        DROPDOWN,
        ElementTexts(
          ElementText(LABEL, Map(DE -> "Element Typ", EN -> "Element Type")),
          ElementText(PLACEHOLDER, supportedLangs.map(_ -> "").toMap),
          ElementText(TOOLTIP, Map(DE -> "Art des Form-Elementes", EN -> "The kind of the form element."))
        ),
        elemEntries = ElementEntries(
          ElementType.values.map(et => ElementEntry(et.entryName, ElementText(LABEL, et.labels)))
        ),
        value = elementType.entryName
      ),

        Some(PropertyUIStore.changeElementType _)
      )
    ).bind}
    </div>
  }

  @dom
  private lazy val layoutWideSelect: Binding[HTMLElement] = {
    val elem = FormUIStore.uiState.selectedElement.bind
    val layoutWide = elem.value.elem.layoutWide
    <div class="field">
      {BaseElementDiv(
      UIFormElem(BaseElement(
        "layoutWideId",
        DROPDOWN,
        ElementTexts(
          ElementText(LABEL, Map(DE -> "Layout Breite", EN -> "Layout Wide")),
          ElementText(PLACEHOLDER, supportedLangs.map(_ -> "").toMap),
          ElementText(TOOLTIP, Map(DE -> "Die Breite des Elementes innerhalb des Layouts (vier bis sechzehn)", EN -> "The wide of an element within its layout (four to sixteen)."))
        ),
        elemEntries = ElementEntries(
          LayoutWide.values.map(lw => ElementEntry(lw.entryName, ElementText(LABEL, lw.labels)))
        ),
        value = layoutWide.entryName
      ),

        Some(PropertyUIStore.changeLayoutWide _)
      )
    ).bind}
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
        {BaseElementDiv(
        UIFormElem(BaseElement(
          s"${lang.abbreviation}-$textType",
          TEXTFIELD,
          ElementTexts(
            ElementText(LABEL, Map.empty),
            ElementText(PLACEHOLDER, supportedLangs.map(_ -> "").toMap),
            ElementText(TOOLTIP, supportedLangs.map(_ -> "").toMap)),
          value = text),

          Some(PropertyUIStore.changeText(lang, textType) _)
        )
      ).bind}
      </div>

}



