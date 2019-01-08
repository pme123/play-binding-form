package pme123.form.client.form

import com.thoughtworks.binding.Binding.{Constants, Var}
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.{DragEvent, Event, HTMLElement}
import pme123.form.client.services.UIStore.supportedLangs
import pme123.form.client.services.{I18n, SemanticUI}
import pme123.form.client.{BaseElementDiv, BaseElementExtras}
import pme123.form.shared.ElementType.{CHECKBOX, DROPDOWN, TEXTFIELD}
import pme123.form.shared.ExtraProp.SIZE
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
      identInput.bind}{//
      defaultValueInput.bind}{//
      layoutWideSelect.bind}{//
      requiredCheckbox.bind}{//
      inlineCheckbox.bind}{//
      readOnlyCheckbox.bind}{//
      elementExtras.bind}
    </div>
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

        Some { eType =>
          selElem.elementTypeVar.value = ElementType.withNameInsensitive(eType)
          SemanticUI.initElements()
        }
      )
    ).bind}
    </div>
  }

  @dom
  private lazy val identInput: Binding[HTMLElement] = {
    val selElem = UIFormStore.uiState.selectedElement.bind.bind
    val elementType = selElem.elementTypeVar.bind
    val readOnly = selElem.readOnlyVar.bind
    if (readOnly || elementType.readOnly)
        <span/>
    else {
      val ident = selElem.identVar.bind
      <div class="field">
        {BaseElementDiv(
        UIFormElem(BaseElement(
          "elemIdent",
          TEXTFIELD,
          ElementTexts(
            Some(ElementText.label(Map(DE -> "Identität", EN -> "Identity"))),
            Some(ElementText.placeholder(Map(DE -> "Identität", EN -> "Identity"))),
            Some(ElementText.tooltip(Map(DE -> "Ein eindeutiger Wert, welchen wir als Referenz nutzen", EN -> "Unique value that we use as a reference.")))
          ),
          value = Some(ident),
          extras = ExtraProperties(TEXTFIELD),
        ),
          Some { str =>
            selElem.identVar.value = str
            SemanticUI.initElements()
          }
        )
      ).bind}
      </div>
    }
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
  private lazy val layoutWideSelect: Binding[HTMLElement] = {
    val uiElem = UIFormStore.uiState.selectedElement.bind.bind
    val layoutWide = uiElem.layoutWideVar.value
    <div class="field">
      {BaseElementDiv(
      UIFormElem(BaseElement(
        "layoutWideId",
        DROPDOWN,
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

        Some { layoutWide =>
          uiElem.layoutWideVar.value = LayoutWide.withNameInsensitive(layoutWide)
          SemanticUI.initElements()
        }
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
    val uiElem = UIFormStore.uiState.selectedElement.bind.bind
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
                  UIElemEntriesStore.addEntry()}>
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
                  UIElemEntriesStore.copyEntry(elementEntry)}>
          <i class="copy icon"></i>
        </button>
        <button class="mini ui circular grey icon button"
                data:data-tooltip="Move Element Entry (use drag'n'drop)">
          <i class="hand spock icon"></i>
        </button>
        <button class="mini ui circular red icon button"
                data:data-tooltip="Delete Element Entry"
                onclick={_: Event =>
                  UIElemEntriesStore.deleteEntry(elementEntry)}>
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
    val uiElem = UIFormStore.uiState.selectedElement.bind.bind
    val elementType = uiElem.elementTypeVar.bind
    if (elementType.hasValidations) {
      val validations = uiElem.validationsVar.bind
      <div class="content">
        <table>
          {for (rule <- validations.rules) yield validationRule(rule).bind}
        </table>
      </div>
    } else <span></span>
  }

  @dom
  private def validationRule(vRule: UIValidationRule): Binding[HTMLElement] = {
    val enabled = vRule.enabled.bind
    <tr>
      <td>
        {BaseElementDiv(
        UIFormElem(BaseElement(
          s"enable-${vRule.validationType}",
          CHECKBOX,
          ElementTexts.label(I18n(vRule.validationType.i18nKey)),
          value = Some(enabled.toString),
          extras = ExtraProperties(TEXTFIELD),
        ),
          Some(enabled =>
            vRule.enabled.value = enabled.toBoolean)
        )
      ).bind}
      </td>{//
      validationField(vRule.validationType, vRule.params).bind}
    </tr>
  }

  @dom
  private def validationField(valType: ValidationType, params: UIValidationParams): Binding[HTMLElement] = {
    val pStr = params.stringParam.bind
    val pInt1 = params.intParam1.bind
    val pInt2 = params.intParam2.bind

    (pStr, pInt1, pInt2) match {
      case (Some(p), _, _) =>
        <td>
          {validationParam(valType, "p", p.toString, str => params.stringParam.value = Some(str), 20).bind}
        </td>
      case (_, Some(p1), Some(p2)) =>
        <td>
          <table>
            <tr>
              {Constants(validationParam(valType, "p1", p1.toString, n => params.intParam1.value = Some(n.toInt)),
              validationParam(valType, "p2", p2.toString, n => params.intParam2.value = Some(n.toInt)))
              .map(_.bind)}
            </tr>
          </table>
        </td>
      case (_, Some(p1), _) =>
        <td>
          {validationParam(valType, "p1", p1.toString, n => params.intParam1.value = Some(n.toInt), 20).bind}
        </td>
      case _ => <span/>
    }
  }

  @dom
  private def validationParam(valType: ValidationType,
                              param: String,
                              paramValue: String,
                              changeEvent: String => Unit,
                              size: Int = 6): Binding[HTMLElement] = {
    <td>
      <div class="ui input">
        {BaseElementDiv(
        UIFormElem(BaseElement(
          s"$param-${valType.key}",
          TEXTFIELD,
          ElementTexts.label(I18n(valType.i18nKey(param))),
          value = Some(paramValue.toString),
          inline = true,
          extras = ExtraProperties(Seq(
            ExtraPropValue(
              SIZE, s"$size"
            )
          ))),
          Some(changeEvent)
        )).bind}
      </div>
    </td>
  }

}

