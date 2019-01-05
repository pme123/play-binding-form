package pme123.form.client.form

import com.thoughtworks.binding.Binding.{Constants, Var}
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.{DragEvent, Event, HTMLElement}
import pme123.form.client.services.UIStore.supportedLangs
import pme123.form.client.services.{I18n, SemanticUI}
import pme123.form.client.{BaseElementDiv, BaseElementExtras}
import pme123.form.shared.ElementType.{CHECKBOX, DROPDOWN, TEXTFIELD}
import pme123.form.shared.ExtraProp.SIZE_CLASS
import pme123.form.shared.PropTabType._
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
      inlineCheckbox.bind}{//
      readOnlyCheckbox.bind}{//
      elementExtras.bind}
    </div>
  }

  @dom
  private lazy val defaultValueInput: Binding[HTMLElement] = {
    val selElem = UIFormStore.uiState.selectedElement.bind.bind
    val elementType = selElem.elementTypeVar.bind
    val readOnly = selElem.readOnlyVar.bind
    if (readOnly || elementType.readOnly)
        <span/>
    else {
      val value = selElem.valueVar.bind
      <div class="field">
        {BaseElementDiv(
        UIFormElem(BaseElement(
          "elemDefaultValue",
          TEXTFIELD,
          DataType.STRING,
          ElementTexts(
            Some(ElementText.label(Map(DE -> "Standart Wert", EN -> "Default Value"))),
            Some(ElementText.placeholder(Map(DE -> "Standart Wert", EN -> "Default Value"))),
            Some(ElementText.tooltip(Map(DE -> "Dieser Wert wird als Startwert angezeigt", EN -> "This value is displayed in the element on start.")))
          ),
          value = value,
          extras = ExtraProperties(TEXTFIELD),
        ),
          Some { str =>
            selElem.valueVar.value = if (str.nonEmpty) Some(str) else None
            SemanticUI.initElements()
          }
        )
      ).bind}
      </div>
    }
  }

  @dom
  private lazy val elementTypeSelect: Binding[HTMLElement] = {
    val selElem = UIFormStore.uiState.selectedElement.bind.bind
    val elementType = selElem.elementTypeVar.value
    <div class="field">
      {BaseElementDiv(
      UIFormElem(BaseElement(
        "elementTypeId",
        DROPDOWN,
        DataType.STRING,
        ElementTexts(
          Some(ElementText.label(Map(DE -> "Element Typ", EN -> "Element Type"))),
          None,
          Some(ElementText.tooltip(Map(DE -> "Art des Form-Elementes", EN -> "The kind of the form element.")))
        ),
        elemEntries = ElementEntries(
          ElementType.values.map(et => ElementEntry(et.entryName, ElementText.label(I18n(et.i18nKey))))
        ),
        value = Some(elementType.entryName),
        extras = ExtraProperties(DROPDOWN),
      ),

        Some(UIPropertyStore.changeElementType _)
      )
    ).bind}
    </div>
  }

  @dom
  private lazy val layoutWideSelect: Binding[HTMLElement] = {
    val uiElem = UIFormStore.uiState.selectedElement.bind.bind
    val layoutWide = uiElem.layoutWideVar.value
    <div class="field">
      {BaseElementDiv(
      UIFormElem(BaseElement(
        "layoutWideId",
        DROPDOWN,
        DataType.STRING,
        ElementTexts(
          Some(ElementText.label(Map(DE -> "Layout Breite", EN -> "Layout Wide"))),
          None,
          Some(ElementText.tooltip(Map(DE -> "Die Breite des Elementes innerhalb des Layouts (vier bis sechzehn)", EN -> "The wide of an element within its layout (four to sixteen).")))
        ),
        elemEntries = ElementEntries(
          LayoutWide.values.map(lw => ElementEntry(lw.entryName, ElementText.label(I18n(lw.i18nKey))))
        ),
        value = Some(layoutWide.entryName),
        extras = ExtraProperties(DROPDOWN),
      ),

        Some(UIPropertyStore.changeLayoutWide _)
      )
    ).bind}
    </div>
  }

  @dom
  private lazy val requiredCheckbox: Binding[HTMLElement] = {
    val uiElem = UIFormStore.uiState.selectedElement.bind.bind
    val required = uiElem.requiredVar.value
    <div class="field">
      {BaseElementDiv(
      UIFormElem(BaseElement(
        "requiredId",
        CHECKBOX,
        DataType.BOOLEAN,
        ElementTexts(
          Some(ElementText.label(Map(DE -> "Obligatorisch", EN -> "Required"))),
          None,
          Some(ElementText.tooltip(Map(DE -> "Das Feld ist zwingend auszufüllen.", EN -> "The value of the field is required.")))
        ),
        value = Some(required.toString),
        extras = ExtraProperties(CHECKBOX),
      ),

        Some { required =>
          uiElem.requiredVar.value = required.toBoolean
          SemanticUI.initElements()
        }
      )
    ).bind}
    </div>
  }

  @dom
  private lazy val inlineCheckbox: Binding[HTMLElement] = {
    val uiElem = UIFormStore.uiState.selectedElement.bind.bind
    val elementType = uiElem.elementTypeVar.bind
    if (elementType.readOnly)
        <span/>
    else {
      val inline = uiElem.inlineVar.value
      <div class="field">
        {BaseElementDiv(
        UIFormElem(BaseElement(
          "inlineId",
          CHECKBOX,
          DataType.BOOLEAN,
          ElementTexts(
            Some(ElementText.label(Map(DE -> "Inline", EN -> "Inline"))),
            None,
            Some(ElementText.tooltip(Map(DE -> "Das Label ist auf derselben Zeile.", EN -> "The label is on the same line.")))
          ),
          value = Some(inline.toString),
          extras = ExtraProperties(CHECKBOX),
        ),

          Some { inline =>
            uiElem.inlineVar.value = inline.toBoolean
            SemanticUI.initElements()
          }
        )
      ).bind}
      </div>
    }
  }

  @dom
  private lazy val readOnlyCheckbox: Binding[HTMLElement] = {
    val uiElem = UIFormStore.uiState.selectedElement.bind.bind
    val elementType = uiElem.elementTypeVar.bind
    if (elementType.readOnly)
        <span/>
    else {
      val readOnly = uiElem.readOnlyVar.value
      <div class="field">
        {BaseElementDiv(
        UIFormElem(BaseElement(
          "readOnlyId",
          CHECKBOX,
          DataType.BOOLEAN,
          ElementTexts(
            Some(ElementText.label(Map(DE -> "Read only", EN -> "Read only"))),
            None,
            Some(ElementText.tooltip(Map(DE -> "Das Feld kann nur gelesen werden.", EN -> "The field can only be read.")))
          ),
          value = Some(readOnly.toString),
          extras = ExtraProperties(CHECKBOX),
        ),

          Some { readOnly =>
            uiElem.readOnlyVar.value = readOnly.toBoolean
            SemanticUI.initElements()
          }
        )
      ).bind}
      </div>
    }
  }

  @dom
  private lazy val elementExtras: Binding[HTMLElement] = {
    val uiElem = UIFormStore.uiState.selectedElement.bind.bind
    val extras = uiElem.extrasVar.bind
    val elementType = uiElem.elementTypeVar.bind
    <div>
      {//
      Constants(
        BaseElementExtras.extras(elementType, extras)
          .map(d => BaseElementDiv(d.uiFormElem)): _*
      ).map(_.bind)}
    </div>
  }

}

case object TextsTab {

  @dom
  lazy val create: Binding[HTMLElement] = {
    val uiElem = UIFormStore.uiState.selectedElement.value.bind
    if (uiElem.hasTexts)
      <div class="content">
        {//
        val texts = uiElem.textsVar.bind
        val readOnly = uiElem.readOnlyVar.bind
        Constants(
          texts.label.map(elementTextDiv(_, uiElem.isViewable)).toList ++
            texts.placeholder.map(elementTextDiv(_, !readOnly)).toList ++
            texts.tooltip.map(elementTextDiv(_, !readOnly)).toList
            : _*).map(_.bind)}
      </div>
    else
      <span></span>
  }

  @dom
  def elementTextDiv(elementText: UIElementText, show: Boolean): Binding[HTMLElement] =
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
  def textDiv(lang: Language, textVar: Var[String], textType: TextType): Binding[HTMLElement] = {
    val text = textVar.bind
    <div class="field">
      {BaseElementDiv(
      UIFormElem(BaseElement(
        s"${lang.abbreviation}-$textType",
        TEXTFIELD,
        DataType.STRING,
        ElementTexts.label(I18n(lang.i18nKey)),
        value = Some(text),
        extras = ExtraProperties(TEXTFIELD)
      ),
        Some(str => textVar.value = str)
      )
    ).bind}
    </div>
  }

}

case object EntriesTab {

  @dom
  lazy val create: Binding[HTMLElement] = {
    val uiElem = UIFormStore.uiState.selectedElement.bind.bind
    val elemType = uiElem.elementTypeVar.bind
    if (elemType.hasEntries) {
      val entries = uiElem.elemEntriesVar.bind
      <div class="content">

        {Constants(Seq(head) ++ entries.entries.map(elementEntry): _*).map(_.bind)}
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
  private def elementEntry(elementEntry: UIElementEntry): Binding[HTMLElement] = {
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
  private def entry(elementEntry: UIElementEntry): Binding[HTMLElement] = {
    val ident = elementEntry.ident
    val key = elementEntry.key.value
    <div class="content">
      <p>
        &nbsp;
      </p>{BaseElementDiv(
      UIFormElem(BaseElement(
        s"field_ident_$ident",
        TEXTFIELD,
        DataType.STRING,
        ElementTexts.placeholder(I18n("props.ident")),
        value = Some(key),
        extras = ExtraProperties(TEXTFIELD),
      ),
        Some(str => elementEntry.key.value = str)
      )
    ).bind}<p>
      &nbsp;
    </p>{//
      Constants(elementEntry.label.texts.map(t => entryForLang(ident, t._1, t._2)).toSeq: _*).map(_.bind)}
    </div>
  }

  @dom
  private def entryForLang(ident: String, lang: Language, textVar: Var[String]): Binding[HTMLElement] = {
    val text = textVar.value
    <div class="field">
      {BaseElementDiv(
      UIFormElem(BaseElement(
        s"${lang.abbreviation}-$ident",
        TEXTFIELD,
        DataType.STRING,
        ElementTexts.label(I18n(lang.i18nKey)),
        value = Some(text),
        extras = ExtraProperties(TEXTFIELD),
      ),
        Some(str => textVar.value = str)
      )
    ).bind}
    </div>
  }
}

case object ValidationsTab {

  @dom
  lazy val create: Binding[HTMLElement] = {
    val uiElem = UIFormStore.uiState.selectedElement.value.bind
    if (uiElem.hasValidations) {
      val validations = uiElem.validationsVar.value.rules
      <div class="content">
        <table>
          {Constants(validations.map(validationRule): _*).map(_.bind)}
        </table>
      </div>
    } else <span></span>
  }

  @dom
  private def validationRule(vRule: ValidationRule): Binding[HTMLElement] = {
    <tr>
      <td>
        {BaseElementDiv(
        UIFormElem(BaseElement(
          s"enable-${vRule.validationType}",
          CHECKBOX,
          DataType.BOOLEAN,
          ElementTexts.label(I18n(vRule.validationType.i18nKey)),
          value = Some(vRule.enabled.toString),
          extras = ExtraProperties(TEXTFIELD),
        ),
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
              SIZE_CLASS, s"$size"
            )
          ))),
          Some(UIPropertyStore.changeValidationRule(vRule, param) _)
        )).bind}
      </div>
    </td>
  }

}

