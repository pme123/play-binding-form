package pme123.form.client

import com.softwaremill.quicklens._
import com.thoughtworks.binding.Binding.Constants
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.{Event, HTMLElement}
import pme123.form.client.services.UIStore.supportedLangs
import pme123.form.shared.ElementType.{DROPDOWN, TEXTFIELD}
import pme123.form.shared.PropTabType.{ENTRIES, PROPERTIES, TEXTS}
import pme123.form.shared.TextType.{LABEL, PLACEHOLDER, TOOLTIP}
import pme123.form.shared._
import pme123.form.shared.services.Language
import pme123.form.shared.services.Language.{DE, EN}

object PropTab {

  @dom
  lazy val create: Binding[HTMLElement] = {
    val activeTab = FormUIStore.uiState.activePropTab.bind
    <div class="ui bottom attached segment card">
      {activeTab match {
      case PROPERTIES =>
        PropertiesTab.create.bind
      case TEXTS =>
        TextsTab.create.bind
      case ENTRIES =>
        EntriesTab.create.bind

    }}
    </div>
  }

}

case object PropertiesTab {

  @dom
  lazy val create: Binding[HTMLElement] = {
    <div class="content">
      {//
      elementTypeSelect.bind}{//
      defaultValueInput.bind}{//
      layoutWideSelect.bind}{//
      elementExtras.bind}
    </div>
  }

  @dom
  private lazy val defaultValueInput: Binding[HTMLElement] = {
    val selElem = FormUIStore.uiState.selectedElement.bind.bind

    if (selElem.isEditable)
      <div class="field">
        {BaseElementDiv(
        UIFormElem(BaseElement(
          "elemDefaultValue",
          TEXTFIELD,
          Some(ElementTexts(
            ElementText(LABEL, Map(DE -> "Standart Wert", EN -> "Default Value")),
            ElementText(PLACEHOLDER, Map(DE -> "Standart Wert", EN -> "Default Value")),
            ElementText(TOOLTIP, Map(DE -> "Dieser Wert wird als Startwert angezeigt", EN -> "This value is displayed in the element on start."))
          ))),
          Some(PropertyUIStore.changeDefaultValue _)
        )
      ).bind}
      </div>
    else
      <span></span>
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
        Some(ElementTexts(
          ElementText(LABEL, Map(DE -> "Element Typ", EN -> "Element Type")),
          ElementText.emptyPlaceholder,
          ElementText(TOOLTIP, Map(DE -> "Art des Form-Elementes", EN -> "The kind of the form element."))
        )),
        elemEntries = Some(ElementEntries(
          ElementType.values.map(et => ElementEntry(et.entryName, ElementText.label(et.i18nKey)))
        )),
        value = Some(elementType.entryName)
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
        Some(ElementTexts(
          ElementText(LABEL, Map(DE -> "Layout Breite", EN -> "Layout Wide")),
          ElementText.emptyPlaceholder,
          ElementText(TOOLTIP, Map(DE -> "Die Breite des Elementes innerhalb des Layouts (vier bis sechzehn)", EN -> "The wide of an element within its layout (four to sixteen)."))
        )),
        elemEntries = Some(ElementEntries(
          LayoutWide.values.map(lw => ElementEntry(lw.entryName, ElementText.label(lw.i18nKey)))
        )),
        value = Some(layoutWide.entryName)
      ),

        Some(PropertyUIStore.changeLayoutWide _)
      )
    ).bind}
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
    if (uiFormElem.elem.hasTexts)
      <div class="content">
        {//
        val texts = uiFormElem.elem.texts.get
        Constants(
          elementTextDiv(texts.label, uiFormElem.isViewable),
          elementTextDiv(texts.placeholder, uiFormElem.isEditable),
          elementTextDiv(texts.tooltip, uiFormElem.isEditable)
        ).map(_.bind)}
      </div>
    else
      <span></span>
  }

  @dom
  def elementTextDiv(elementText: ElementText, show: Boolean): Binding[HTMLElement] =
    if (show)
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
    else
      <span></span>

  @dom
  def textDiv(lang: Language, text: String, textType: TextType): Binding[HTMLElement] = {
    <div class="field">
      {BaseElementDiv(
      UIFormElem(BaseElement(
        s"${lang.abbreviation}-$textType",
        TEXTFIELD,
        Some(ElementTexts.label(lang.i18nKey)),
        value = Some(text)),
        Some(PropertyUIStore.changeText(lang, textType) _)
      )
    ).bind}
    </div>
  }

}

case object EntriesTab {

  @dom
  lazy val create: Binding[HTMLElement] = {
    val uiFormElem = FormUIStore.uiState.selectedElement.bind.bind
    if (uiFormElem.elem.hasEntries) {
      val entries = uiFormElem.elem.elemEntries.get.entries
      <div class="content">

        {Constants(entries.zipWithIndex.map(e => entry(e._2, e._1)) :+ head: _*).map(_.bind)}
      </div>
    } else <span></span>
  }

  @dom
  private def head: Binding[HTMLElement] =
    <div class="">
      <br/>
      <div class="right floated author">

        <button class="floated right mini ui circular blue icon button"
                data:data-tooltip="Add Element Entry"
                onclick={_: Event =>
                  PropertyUIStore.addElementEntry()}>
          <i class="add icon"></i>
        </button>
      </div>
    </div>

  @dom
  private def entry(pos: Int, elementEntry: ElementEntry): Binding[HTMLElement] = {
    <div class="field">
      <p>
        &nbsp;
      </p>{BaseElementDiv(
      UIFormElem(BaseElement(
        s"field_ident_$pos",
        TEXTFIELD,
        Some(ElementTexts.placeholder("props.ident")),
        value = Some(elementEntry.ident)),
        Some(PropertyUIStore.changeEntryIdent(pos) _)
      )
    ).bind}<p>
      &nbsp;
    </p>{Constants(elementEntry.label.texts.map(t => entryForLang(pos, t._1, t._2)).toSeq: _*).map(_.bind)}
    </div>
  }

  @dom
  private def entryForLang(pos: Int, lang: Language, text: String): Binding[HTMLElement] =
    <div class="field">
      {BaseElementDiv(
      UIFormElem(BaseElement(
        s"${lang.abbreviation}-$pos",
        TEXTFIELD,
        Some(ElementTexts.label(lang.i18nKey)),
        value = Some(text)),
        Some(PropertyUIStore.changeEntry(lang, pos) _)
      )
    ).bind}
    </div>
}


