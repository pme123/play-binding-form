package pme123.form.client

import com.thoughtworks.binding.Binding.{Constants, Var}
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.{Event, HTMLElement}
import org.scalajs.jquery.jQuery
import pme123.form.client.BaseElementDiv.InputAttr
import pme123.form.client.form.UIFormElem.ChangeEvent
import pme123.form.client.form._
import pme123.form.client.services.{SemanticUI, UIStore}
import pme123.form.shared.ElementType
import pme123.form.shared.ElementType._
import pme123.form.shared.ExtraProp.{CHECKBOX_TYPE, CLEARABLE, INPUT_TYPE, SIZE, SIZE_CLASS}
import pme123.form.shared.services.Language

sealed abstract class BaseElementDiv {

  protected def changeEvent(inputAttr: InputAttr): Unit = {
    changeEvent(inputAttr, jQuery(s"#${inputAttr.ident}").value().toString)
  }

  protected def changeEvent(inputAttr: InputAttr, newValue: String): Unit = {
    //noinspection UnitInMap
    inputAttr.changeEvent
      .map(ce =>
        ce(newValue))
      .getOrElse(
        inputAttr.valueVar.value =
          if (newValue.nonEmpty)
            Some(newValue)
          else
            None
      )
    SemanticUI.initElements()
  }
}

object BaseElementDiv {

  @dom
  def apply(uiFormElem: UIFormElem): Binding[HTMLElement] = {
    val activeLanguage = UIStore.activeLanguage.bind
    val elementType = uiFormElem.elementTypeVar.bind
    val texts = uiFormElem.textsVar.bind
    val labelStr = texts.textForLabel(activeLanguage).bind
    val inline = uiFormElem.inlineVar.bind
    val required = uiFormElem.requiredVar.bind
    val readOnly = uiFormElem.readOnlyVar.bind
    val ident = uiFormElem.identVar.bind
    val extras = uiFormElem.extrasVar.bind
    val placeholder = texts.textForPlaceholder(activeLanguage).bind
    val inlineClass = if (inline) "inline" else ""
    val input = createInput(InputAttr(
      elementType,
      uiFormElem.valueVar,
      activeLanguage,
      ident,
      readOnly,
      placeholder,
      labelStr,
      extras,
      uiFormElem.elemEntriesVar,
      uiFormElem.changeEvent,
    )
    )
    <div class="content">
      {if (elementType.readOnly) {
      <div>
        {input.bind}
      </div>
    } else {
      <div class={s"$inlineClass field"}>
        {label(texts, activeLanguage, required).bind //
        }{input.bind}
      </div>
    }}
    </div>
  }

  @dom
  private def label(elementTexts: UIElementTexts, activeLanguage: Language, required: Boolean): Binding[HTMLElement] = {
    val requiredStr = if (required) "*" else ""
    val labelTxt = elementTexts.textForLabel(activeLanguage).bind
    val tooltipTxt = elementTexts.textForTooltip(activeLanguage).bind
    if (labelTxt.nonEmpty)
      <label class="">
        {labelTxt + requiredStr}&nbsp;{//
        if (tooltipTxt.nonEmpty)
          <i class="question circle icon ui tooltip"
             title={tooltipTxt}></i>
        else <span/>}
      </label>
    else <span/>

  }

  @dom
  private def createInput(inputAttr: InputAttr): Binding[HTMLElement] = {
    <span class="">
      {inputAttr.elementType match {
      case DROPDOWN =>
        DropdownDiv.create(inputAttr).bind
      case TEXTFIELD =>
        TextFieldDiv.create(inputAttr).bind
      case TEXTAREA =>
        TextAreaDiv.create(inputAttr).bind
      case CHECKBOX =>
        CheckboxDiv.create(inputAttr).bind
      case TITLE =>
        TitleDiv.create(inputAttr).bind
      case DIVIDER =>
        DividerDiv.create(inputAttr).bind
      case SPACER =>
        SpacerDiv.create(inputAttr).bind
      case _ =>
        <div>
          {inputAttr.ident}
        </div>
    }}
    </span>
  }

  case class InputAttr(elementType: ElementType,
                       valueVar: Var[Option[String]],
                       activeLanguage: Language,
                       ident: String,
                       readOnly: Boolean,
                       placeholder: String,
                       label: String,
                       extras: UIExtraProperties,
                       elementEntriesVar: Var[UIElementEntries],
                       changeEvent: ChangeEvent,
                      )

}

object DropdownDiv extends BaseElementDiv {

  @dom
  def create(inputAttr: InputAttr): Binding[HTMLElement] = {
    val clearable = inputAttr.extras.valueFor(CLEARABLE).bind
    val clearableClass = if (clearable.contains("true")) "clearable" else ""
    val value = inputAttr.valueVar.bind
    val elemEntries = inputAttr.elementEntriesVar.bind
    val readOnlyClass = if (inputAttr.readOnly) "disabled" else ""
    <div class={s"ui $clearableClass $readOnlyClass fluid selection dropdown"}>
      <input id={inputAttr.ident}
             name={inputAttr.ident}
             type="hidden"
             readOnly={inputAttr.readOnly}
             value={value.getOrElse("")}
             onchange={_: Event =>
               changeEvent(inputAttr)}/>
      <i class="dropdown icon"></i>
      <div class="default text">
        {value.getOrElse(inputAttr.placeholder)}
      </div>
      <div class="menu">
        {Constants(elemEntries.entries.map(elementEntry(_, inputAttr.activeLanguage)): _*).map(_.bind)}
      </div>
    </div>
  }

  @dom
  private def elementEntry(entry: UIElementEntry, language: Language): Binding[HTMLElement] = {
    val label = entry.label.textFor(language).bind
    val key = entry.key.bind
    <div class="item" data:data-value={key}>
      {label}
    </div>
  }

}

object TextFieldDiv extends BaseElementDiv {

  @dom
  def create(inputAttr: InputAttr): Binding[HTMLElement] = {
    val value = inputAttr.valueVar.value
    val size = inputAttr.extras.valueFor(SIZE).bind
    val inputType = inputAttr.extras.valueFor(INPUT_TYPE).bind
    val readOnlyClass = if (inputAttr.readOnly) "disabled" else ""
    <div class={s"ui $readOnlyClass input"}>
      <input id={inputAttr.ident}
             name={inputAttr.ident}
             size={size.toInt}
             type={inputType}
             readOnly={inputAttr.readOnly}
             placeholder={inputAttr.placeholder}
             value={value.getOrElse("")}
             onblur={_: Event =>
               changeEvent(inputAttr)}/>
    </div>
  }

}

object TextAreaDiv extends BaseElementDiv {

  @dom
  def create(inputAttr: InputAttr): Binding[HTMLElement] = {
    val value = inputAttr.valueVar.value
    val readOnlyClass = if (inputAttr.readOnly) "disabled" else ""
    <div class={s"ui $readOnlyClass input"}>
      <textarea id={inputAttr.ident}
                name={inputAttr.ident}
                placeholder={inputAttr.placeholder}
                value={value.get}
                readOnly={inputAttr.readOnly}
                rows={6}
                onblur={_: Event =>
                  inputAttr.changeEvent}>
      </textarea>
    </div>
  }

}

object CheckboxDiv extends BaseElementDiv {

  @dom
  def create(inputAttr: InputAttr): Binding[HTMLElement] = {
    val value = inputAttr.valueVar.value
    val typeClass = inputAttr.extras.valueFor(CHECKBOX_TYPE).bind
    val checkedClass = if (value.contains("true")) "checked" else ""
    val checked = value.contains("true")
    val readOnlyClass = if (inputAttr.readOnly) "disabled" else ""
    <div class={s"ui $typeClass $readOnlyClass checkbox $checkedClass"}>
      <input id={inputAttr.ident}
             name={inputAttr.ident}
             type="checkbox"
             readOnly={inputAttr.readOnly}
             onchange={_: Event =>
               changeEvent(inputAttr,
                 jQuery(s"#${inputAttr.ident}").is(":checked").toString)}
             checked={checked}/>
    </div>
  }


}

object TitleDiv extends BaseElementDiv {

  @dom
  def create(inputAttr: InputAttr): Binding[HTMLElement] = {
    val sizeClass = inputAttr.extras.valueFor(SIZE_CLASS).bind
    <div class={s"ui $sizeClass header"}>
      {inputAttr.label}
    </div>
  }

}

object DividerDiv extends BaseElementDiv {

  @dom
  def create(inputAttr: InputAttr): Binding[HTMLElement] = {
    val horDivider = if (inputAttr.label.nonEmpty) "horizontal" else ""
    val sizeClass = inputAttr.extras.valueFor(SIZE_CLASS).bind
    <div class={s"ui $horDivider divider"}>
      <div class={s"ui $sizeClass header"}>
        {inputAttr.label}
      </div>
    </div>
  }

}

object SpacerDiv extends BaseElementDiv {

  @dom
  def create(inputAttr: InputAttr): Binding[HTMLElement] = {
    <div>
    </div>
  }

}