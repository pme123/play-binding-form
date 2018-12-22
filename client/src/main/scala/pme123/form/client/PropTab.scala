package pme123.form.client

import com.softwaremill.quicklens._
import com.thoughtworks.binding.Binding.Constants
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.{DragEvent, Event, HTMLElement}
import pme123.form.client.services.I18n
import pme123.form.client.services.UIStore.supportedLangs
import pme123.form.shared.ElementType.{CHECKBOX, DROPDOWN, TEXTFIELD}
import pme123.form.shared.ExtraProp.SIZE
import pme123.form.shared.PropTabType._
import pme123.form.shared.TextType.{LABEL, PLACEHOLDER, TOOLTIP}
import pme123.form.shared._
import pme123.form.shared.services.Language
import pme123.form.shared.services.Language.{DE, EN}

object PropTab {

  @dom
  lazy val create: Binding[HTMLElement] = {
    val activeTab = UIFormStore.uiState.activePropTab.bind
    <div class="ui bottom attached segment card">
      {activeTab match {
      case PROPERTIES =>
        PropertiesTab.create.bind
      case TEXTS =>
        TextsTab.create.bind
      case ENTRIES =>
        EntriesTab.create.bind
      case VALIDATIONS =>
        ValidationsTab.create.bind

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
      requiredCheckbox.bind}{//
      elementExtras.bind}
    </div>
  }

  @dom
  private lazy val defaultValueInput: Binding[HTMLElement] = {
    val selElem = UIFormStore.uiState.activePropElement.bind

    if (selElem.isEditable)
      <div class="field">
        {BaseElementDiv(
        UIFormElem(BaseElement(
          "elemDefaultValue",
          TEXTFIELD,
          DataType.STRING,
          ElementTexts(
            Some(ElementText(LABEL, Map(DE -> "Standart Wert", EN -> "Default Value"))),
              Some(ElementText(PLACEHOLDER, Map(DE -> "Standart Wert", EN -> "Default Value"))),
                Some(ElementText(TOOLTIP, Map(DE -> "Dieser Wert wird als Startwert angezeigt", EN -> "This value is displayed in the element on start.")))
          )),
          Some(UIPropertyStore.changeDefaultValue _)
        )
      ).bind}
      </div>
    else
      <span></span>
  }

  @dom
  private lazy val elementTypeSelect: Binding[HTMLElement] = {
    val uiElem = UIFormStore.uiState.activePropElement.bind
    val elementType = uiElem.elem.elementType
    <div class="field">
      {BaseElementDiv(
      UIFormElem(BaseElement(
        "elementTypeId",
        DROPDOWN,
        DataType.STRING,
        ElementTexts(
          Some(ElementText(LABEL, Map(DE -> "Element Typ", EN -> "Element Type"))),
          None,
            Some(ElementText(TOOLTIP, Map(DE -> "Art des Form-Elementes", EN -> "The kind of the form element.")))
        ),
        elemEntries = ElementEntries(
          ElementType.values.map(et => ElementEntry(et.entryName, ElementText.label(I18n(et.i18nKey))))
        ),
        value = Some(elementType.entryName)
      ),

        Some(UIPropertyStore.changeElementType _)
      )
    ).bind}
    </div>
  }

  @dom
  private lazy val layoutWideSelect: Binding[HTMLElement] = {
    val uiElem = UIFormStore.uiState.activePropElement.bind
    val layoutWide = uiElem.elem.layoutWide
    <div class="field">
      {BaseElementDiv(
      UIFormElem(BaseElement(
        "layoutWideId",
        DROPDOWN,
        DataType.STRING,
        ElementTexts(
          Some(ElementText(LABEL, Map(DE -> "Layout Breite", EN -> "Layout Wide"))),
          None,
            Some(ElementText(TOOLTIP, Map(DE -> "Die Breite des Elementes innerhalb des Layouts (vier bis sechzehn)", EN -> "The wide of an element within its layout (four to sixteen).")))
        ),
        elemEntries = ElementEntries(
          LayoutWide.values.map(lw => ElementEntry(lw.entryName, ElementText.label(I18n(lw.i18nKey))))
        ),
        value = Some(layoutWide.entryName)
      ),

        Some(UIPropertyStore.changeLayoutWide _)
      )
    ).bind}
    </div>
  }

  @dom
  private lazy val requiredCheckbox: Binding[HTMLElement] = {
    val uiElem = UIFormStore.uiState.activePropElement.bind
    val required = uiElem.elem.required
    <div class="field">
      {BaseElementDiv(
      UIFormElem(BaseElement(
        "requiredId",
        CHECKBOX,
        DataType.BOOLEAN,
        ElementTexts(
          Some(ElementText.label(Map(DE -> "Obligatorisch", EN -> "Required"))),
          None,
          Some(ElementText(TOOLTIP, Map(DE -> "Das Feld ist zwingend auszufüllen.", EN -> "The value of the field is required.")))
        ),
        value = Some(required.toString)
      ),

        Some(UIPropertyStore.changeRequired _)
      )
    ).bind}
    </div>
  }

  @dom
  private lazy val elementExtras: Binding[HTMLElement] = {
    val elem = UIFormStore.uiState.activePropElement.bind
    val extras = elem.extras

    <div>
      {//
      Constants(
        extras
          .map { case (ep, uie) =>
            BaseElementDiv(
              uie.modify(_.changeEvent)
                .setTo(Some(UIPropertyStore.changeExtraProp(ep))))
          }.toSeq: _*)
        .map(_.bind)}
    </div>
  }

}

case object TextsTab {

  @dom
  lazy val create: Binding[HTMLElement] = {
    val uiFormElem = UIFormStore.uiState.activePropElement.bind
    if (uiFormElem.elem.hasTexts)
      <div class="content">
        {//
        val texts = uiFormElem.elem.texts
        Constants(
          elementTextDiv(texts.label.get, uiFormElem.isViewable),
          elementTextDiv(texts.placeholder.get, uiFormElem.isEditable),
          elementTextDiv(texts.tooltip.get, uiFormElem.isEditable)
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
        DataType.STRING,
        ElementTexts.label(I18n(lang.i18nKey)),
        value = Some(text)),
        Some(UIPropertyStore.changeText(lang, textType) _)
      )
    ).bind}
    </div>
  }

}

case object EntriesTab {

  @dom
  lazy val create: Binding[HTMLElement] = {
    val uiFormElem = UIFormStore.uiState.activePropElement.bind
    if (uiFormElem.elem.hasEntries) {
      val entries = uiFormElem.elem.elemEntries.entries
      <div class="content">

        {Constants(Seq(head) ++ entries.map(elementEntry): _*).map(_.bind)}
      </div>
    } else <span></span>
  }

  @dom
  private def head: Binding[HTMLElement] =
    <div class="ui borderless menu">
      <div class="ui right item">
        <button class="mini ui circular blue icon button"
                data:data-tooltip="Add Element Entry"
                onclick={_: Event =>
                  UIPropertyStore.addEntry()}>
          <i class="add icon"></i>
        </button>
      </div>
    </div>

  @dom
  private def elementEntry(elementEntry: ElementEntry): Binding[HTMLElement] = {
    <div class={s"ui card"}
         draggable="true"
         ondragstart={_: DragEvent =>
           ElemEntryDragDrop.drag(elementEntry)}
         ondrop={ev: DragEvent =>
           ElemEntryDragDrop.drop(elementEntry)(ev)}
         ondragover={ev: DragEvent =>
           ElemEntryDragDrop.allowDrop(elementEntry)(ev)}>
      {entry(elementEntry).bind}<div class="extra content">
      <div class="right floated">

        <button class="mini ui circular grey icon button"
                data:data-tooltip="Copy Element Entry"
                onclick={_: Event =>
                  UIPropertyStore.copyEntry(elementEntry)}>
          <i class="copy icon"></i>
        </button>
        <button class="mini ui circular grey icon button"
                data:data-tooltip="Move Element Entry (use drag'n'drop)">
          <i class="hand spock icon"></i>
        </button>
        <button class="mini ui circular red icon button"
                data:data-tooltip="Delete Element Entry"
                onclick={_: Event =>
                  UIPropertyStore.deleteEntry(elementEntry)}>
          <i class="trash icon"></i>
        </button>
      </div>
    </div>
    </div>
  }

  @dom
  private def entry(elementEntry: ElementEntry): Binding[HTMLElement] = {
    val ident = elementEntry.ident
    <div class="content">
      <p>
        &nbsp;
      </p>{BaseElementDiv(
      UIFormElem(BaseElement(
        s"field_ident_$ident",
        TEXTFIELD,
        DataType.STRING,
        ElementTexts.placeholder(I18n("props.ident")),
        value = Some(elementEntry.key)),
        Some(UIPropertyStore.changeEntryKey(ident) _)
      )
    ).bind}<p>
      &nbsp;
    </p>{Constants(elementEntry.label.texts.map(t => entryForLang(ident, t._1, t._2)).toSeq: _*).map(_.bind)}
    </div>
  }

  @dom
  private def entryForLang(ident: String, lang: Language, text: String): Binding[HTMLElement] =
    <div class="field">
      {BaseElementDiv(
      UIFormElem(BaseElement(
        s"${lang.abbreviation}-$ident",
        TEXTFIELD,
        DataType.STRING,
        ElementTexts.label(I18n(lang.i18nKey)),
        value = Some(text)),
        Some(UIPropertyStore.changeEntryText(lang, ident) _)
      )
    ).bind}
    </div>
}

case object ValidationsTab {

  @dom
  lazy val create: Binding[HTMLElement] = {
    val uiFormElem = UIFormStore.uiState.activePropElement.bind
    if (uiFormElem.elem.hasValidations) {
      val validations = uiFormElem.elem.validations.rules
      <div class="content">
        <table>
          {Constants(validations.map(validationRule): _*).map(_.bind)}
        </table>
      </div>
    } else <span></span>
  }

  @dom
  private def validationRule(vRule: ValidationRule): Binding[HTMLElement] = {
    println(s"vRule: $vRule")
    <tr>
      <td>
        {BaseElementDiv(
        UIFormElem(BaseElement(
          s"enable-${vRule.validationType}",
          CHECKBOX,
          DataType.BOOLEAN,
          ElementTexts.label(I18n(vRule.validationType.i18nKey)),
          value = Some(vRule.enabled.toString)),
          Some(UIPropertyStore.changeValidationEnabled(vRule) _)
        )
      ).bind}
      </td>{//
      validationField(vRule).bind}
    </tr>
  }

  @dom
  private def validationField(vRule: ValidationRule): Binding[HTMLElement] = {

    vRule.params match {
      case ValidationParams(Some(p), _, _) =>
        <td>
          {validationParam(vRule, "p", p.toString, 20).bind}
        </td>
      case ValidationParams(_, Some(p1), Some(p2)) =>
        <td>
          <table>
            <tr>
              {Constants(validationParam(vRule, "p1", p1.toString),
              validationParam(vRule, "p2", p2.toString))
              .map(_.bind)}
            </tr>
          </table>
        </td>
      case ValidationParams(_, Some(p1), _) =>
        <td>
          {validationParam(vRule, "p1", p1.toString, 20).bind}
        </td>
      case _ => <span/>
    }
  }

  @dom
  private def validationParam(vRule: ValidationRule, param: String, paramValue: String, size: Int = 6): Binding[HTMLElement] = {
    val valType = vRule.validationType
    <td>
      <div class="ui input">
        {BaseElementDiv(
        UIFormElem(BaseElement(
          s"$param-${valType.entryName}",
          TEXTFIELD,
          DataType.STRING,
          ElementTexts.label(I18n(valType.i18nKey(param))),
          value = Some(paramValue.toString),
          extras = ExtraProperties(Seq(
            ExtraPropValue(
              SIZE,Some(s"$size")
            )
          ))),
          Some(UIPropertyStore.changeValidationRule(vRule, param) _)
        )).bind}
      </div>
    </td>
  }

}

