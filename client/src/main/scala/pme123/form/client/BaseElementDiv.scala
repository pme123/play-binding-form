package pme123.form.client

import com.thoughtworks.binding.Binding.{Constants, Var}
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.{Event, HTMLElement}
import org.scalajs.jquery.jQuery
import pme123.form.client.BaseElementDiv.InputAttr
import pme123.form.client.form.UIFormElem.ChangeEvent
import pme123.form.client.form.{UIElementEntries, UIElementEntry, UIElementTexts, UIFormElem}
import pme123.form.client.services.UIStore
import pme123.form.shared.ElementType._
import pme123.form.shared.ExtraProp.{CHECKBOX_TYPE, CLEARABLE, SIZE}
import pme123.form.shared.{ElementType, ExtraProperties}
import pme123.form.shared.services.Language

sealed abstract class BaseElementDiv {

  protected def changeEvent(ident: String, changeEvent: ChangeEvent): Unit = {
    val newText = jQuery(s"#$ident").value().toString
    changeEvent
      .foreach(ce =>
        ce(newText))
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
    val readOnlyClass = if (readOnly) "disabled" else ""
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
      {elementType.readOnly match {
      case false => <div class={s"$inlineClass $readOnlyClass field"}>
        {label(texts, activeLanguage, required).bind //
        }{input.bind}
      </div>

      case true => <div>
        {input.bind}
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
    <div class="">
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
    </div>
  }

  case class InputAttr(elementType: ElementType,
                       valueVar: Var[Option[String]],
                       activeLanguage: Language,
                       ident: String,
                       readOnly: Boolean,
                       placeholder: String,
                       label: String,
                       extras: ExtraProperties,
                       elementEntriesVar: Var[UIElementEntries],
                       changeEvent: ChangeEvent,
                      )

}

object DropdownDiv extends BaseElementDiv {

  @dom
  def create(inputAttr: InputAttr): Binding[HTMLElement] = {
    val clearableClass = if (inputAttr.extras.valueFor(CLEARABLE).contains("true")) "clearable" else ""
    val value = inputAttr.valueVar.bind
    val elemEntries = inputAttr.elementEntriesVar.bind
    <div class={s"ui $clearableClass fluid selection dropdown"}>
      <input id={inputAttr.ident}
             name={inputAttr.ident}
             type="hidden"
             readOnly={inputAttr.readOnly}
             value={value.getOrElse("")}
             onchange={_: Event =>
               changeEvent(inputAttr.ident, inputAttr.changeEvent)}/>
      <i class="dropdown icon"></i>
      <div class="default text">
        {value.getOrElse("")}
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
    val value = inputAttr.valueVar.bind
    val size = inputAttr.extras.valueFor(SIZE).map(_.toInt).getOrElse(20)
    <div class="ui input">
      <input id={inputAttr.ident}
             name={inputAttr.ident}
             size={size}
             type="text"
             readOnly={inputAttr.readOnly}
             placeholder={inputAttr.placeholder}
             value={value.getOrElse("")}
             onblur={_: Event =>
               changeEvent(inputAttr.ident, inputAttr.changeEvent)}/>
    </div>
  }

}

object TextAreaDiv extends BaseElementDiv {

  @dom
  def create(inputAttr: InputAttr): Binding[HTMLElement] = {
    val value = inputAttr.valueVar.bind
    <div class="ui input">
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
    val value = inputAttr.valueVar.bind
    val typeClass = inputAttr.extras.valueFor(CHECKBOX_TYPE).map(_.toLowerCase).getOrElse("")
    val checkedClass = if (value.contains("true")) "checked" else ""
    val checked = value.contains("true")
    <div class={s"ui $typeClass checkbox $checkedClass"}>
      <input id={inputAttr.ident}
             name={inputAttr.ident}
             type="checkbox"
             readOnly={inputAttr.readOnly}
             checked={checked}
             tabIndex={0}
             onchange={_: Event =>
               val newText = jQuery(s"#${inputAttr.ident}").is(":checked").toString
               inputAttr.changeEvent
                 .foreach(ce =>
                   ce(newText))}/>
    </div>
  }


}

object TitleDiv extends BaseElementDiv {

  @dom
  def create(inputAttr: InputAttr): Binding[HTMLElement] = {
    val sizeClass = inputAttr.extras.valueFor(SIZE).map(_.toLowerCase).getOrElse("")
    <div class={s"ui $sizeClass header"}>
      {inputAttr.label}
    </div>
  }

}

object DividerDiv extends BaseElementDiv {

  @dom
  def create(inputAttr: InputAttr): Binding[HTMLElement] = {
    val horDivider = if (inputAttr.label.nonEmpty) "horizontal" else ""
    val sizeClass = inputAttr.extras.valueFor(SIZE).map(_.toLowerCase).getOrElse("")
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