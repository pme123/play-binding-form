package pme123.form.client

import com.thoughtworks.binding.Binding.Constants
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.{Event, HTMLElement}
import org.scalajs.jquery.jQuery
import pme123.form.client.form.UIFormElem
import pme123.form.client.services.UIStore
import pme123.form.shared.ElementType._
import pme123.form.shared.ExtraProp.{CHECKBOX_TYPE, CLEARABLE, SIZE}
import pme123.form.shared.services.Language
import pme123.form.shared.{ElementEntry, ElementTexts}

sealed abstract class BaseElementDiv {

  @dom
  protected def label(elementTexts: ElementTexts, activeLanguage: Language, required: Boolean): Binding[HTMLElement] = {
    val requiredStr = if (required) "*" else ""
    if (elementTexts.textForLabel(activeLanguage).nonEmpty)
      <label class="">
        {elementTexts.textForLabel(activeLanguage) + requiredStr}&nbsp;{//
        if (elementTexts.textForTooltip(activeLanguage).nonEmpty)
          <i class="question circle icon ui tooltip"
             title={elementTexts.textForTooltip(activeLanguage)}></i>
        else <span/>}
      </label>
    else <span/>

  }

  protected def changeEvent(uiFormElem: UIFormElem): Unit = {
    val newText = jQuery(s"#${uiFormElem.identVar.value}").value().toString
    uiFormElem.changeEvent
      .foreach(ce =>
        ce(newText))
  }
}

object BaseElementDiv {


  @dom
  def apply(uiFormElem: UIFormElem): Binding[HTMLElement] = {
    val elementType = uiFormElem.elementTypeVar.value
    <div class="content">
      {elementType match {
      case DROPDOWN =>
        DropdownDiv.create(uiFormElem).bind
      case TEXTFIELD =>
        TextFieldDiv.create(uiFormElem).bind
      case TEXTAREA =>
        TextAreaDiv.create(uiFormElem).bind
      case CHECKBOX =>
        CheckboxDiv.create(uiFormElem).bind
      case TITLE =>
        TitleDiv.create(uiFormElem).bind
      case DIVIDER =>
        DividerDiv.create(uiFormElem).bind
      case SPACER =>
        SpacerDiv.create(uiFormElem).bind
      case _ =>
        <div>
          {uiFormElem.identVar.value}
        </div>
    }}
    </div>
  }

}

object DropdownDiv extends BaseElementDiv {

  @dom
  def create(uiFormElem: UIFormElem): Binding[HTMLElement] = {
    val activeLanguage = UIStore.activeLanguage.bind
    val texts = uiFormElem.textsVar.bind
    val inlineClass = uiFormElem.inlineVar.bind
    val required = uiFormElem.requiredVar.bind
    val readOnly = uiFormElem.readOnlyVar.bind
    val ident = uiFormElem.identVar.bind
    val value = uiFormElem.valueVar.bind
    val elemEntries = uiFormElem.elemEntriesVar.bind
    val extras = uiFormElem.extrasVar.bind
    val clearableClass = if (extras.valueFor(CLEARABLE).contains("true")) "clearable" else ""
    <div class={s"$inlineClass field"}>
      {label(texts, activeLanguage, required).bind //
      }<div class={s"ui $clearableClass selection dropdown"}>
      <input id={ident}
             name={ident}
             type="hidden"
             readOnly={readOnly}
             value={value.getOrElse("")}
             onchange={_: Event =>
               changeEvent(uiFormElem)}/>
      <i class="dropdown icon"></i>
      <div class="default text">
        {value.getOrElse("")}
      </div>
      <div class="menu">
        {Constants(elemEntries.entries.map(elementEntry(_, activeLanguage)): _*).map(_.bind)}
      </div>
    </div>
    </div>
  }

  @dom
  private def elementEntry(entry: ElementEntry, language: Language): Binding[HTMLElement] = {
    <div class="item" data:data-value={entry.key}>
      {entry.label.textFor(language)}
    </div>
  }

}

object TextFieldDiv extends BaseElementDiv {

  @dom
  def create(uiFormElem: UIFormElem): Binding[HTMLElement] = {
    val activeLanguage = UIStore.activeLanguage.bind
    val texts = uiFormElem.textsVar.bind
    val inlineClass = uiFormElem.inlineVar.bind
    val required = uiFormElem.requiredVar.bind
    val readOnly = uiFormElem.readOnlyVar.bind
    val ident = uiFormElem.identVar.bind
    val value = uiFormElem.valueVar.bind
    val extras = uiFormElem.extrasVar.bind
    val size = extras.valueFor(SIZE).map(_.toInt).getOrElse(20)
    <div class={s"$inlineClass field"}>
      {if (texts.hasTexts) label(texts, activeLanguage, required).bind else <span/> //
      }<div class="ui input">
      <input id={ident}
             name={ident}
             size={size}
             type="text"
             readOnly={readOnly}
             placeholder={texts.textForPlaceholder(activeLanguage)}
             value={value.getOrElse("")}
             onblur={_: Event =>
               changeEvent(uiFormElem)}/>
    </div>
    </div>
  }

}

object TextAreaDiv extends BaseElementDiv {

  @dom
  def create(uiFormElem: UIFormElem): Binding[HTMLElement] = {
    val activeLanguage = UIStore.activeLanguage.bind
    val texts = uiFormElem.textsVar.bind
    val inlineClass = uiFormElem.inlineVar.bind
    val required = uiFormElem.requiredVar.bind
    val readOnly = uiFormElem.readOnlyVar.bind
    val ident = uiFormElem.identVar.bind
    val value = uiFormElem.valueVar.bind

    <div class={s"$inlineClass field"}>
      {label(texts, activeLanguage, required).bind //
      }<div class="ui input">
      <textarea id={ident}
                name={ident}
                placeholder={texts.textForPlaceholder(activeLanguage)}
                value={value.get}
                readOnly={readOnly}
                rows={6}
                onblur={_: Event =>
                  changeEvent(uiFormElem)}>
      </textarea>
    </div>
    </div>
  }

}

object CheckboxDiv extends BaseElementDiv {

  @dom
  def create(uiFormElem: UIFormElem): Binding[HTMLElement] = {
    val activeLanguage = UIStore.activeLanguage.bind
    val texts = uiFormElem.textsVar.bind
    val inlineClass = uiFormElem.inlineVar.bind
    val required = uiFormElem.requiredVar.bind
    val readOnly = uiFormElem.readOnlyVar.bind
    val ident = uiFormElem.identVar.bind
    val value = uiFormElem.valueVar.bind
    val extras = uiFormElem.extrasVar.bind
    val typeClass = extras.valueFor(CHECKBOX_TYPE).map(_.toLowerCase).getOrElse("")
    val checkedClass = if (value.contains("true")) "checked" else ""
    val checked = value.contains("true")
    <div class={s"$inlineClass field"}>
      {label(texts, activeLanguage, required).bind //
      }<div class={s"ui $typeClass checkbox $checkedClass"}>
      <input id={ident}
             name={ident}
             type="checkbox"
             readOnly={readOnly}
             checked={checked}
             tabIndex={0}
             onchange={_: Event =>
               val newText = jQuery(s"#$ident").is(":checked").toString
               uiFormElem.changeEvent
                 .foreach(ce =>
                   ce(newText))}/>
    </div>
    </div>

  }


}

object TitleDiv extends BaseElementDiv {

  @dom
  def create(uiFormElem: UIFormElem): Binding[HTMLElement] = {
    val activeLanguage = UIStore.activeLanguage.bind
    val texts = uiFormElem.textsVar.bind
    val inlineClass = uiFormElem.inlineVar.bind
    val extras = uiFormElem.extrasVar.bind
    val sizeClass = extras.valueFor(SIZE).map(_.toLowerCase).getOrElse("")
    <div class={s"$inlineClass field"}>
      <div class={s"ui $sizeClass header"}>
        {texts.textForLabel(activeLanguage) //
        }
      </div>
    </div>
  }

}

object DividerDiv extends BaseElementDiv {

  @dom
  def create(uiFormElem: UIFormElem): Binding[HTMLElement] = {
    val activeLanguage = UIStore.activeLanguage.bind
    val texts = uiFormElem.textsVar.bind
    val text = texts.textForLabel(activeLanguage)
    val horDivider = if (text.nonEmpty) "horizontal" else ""
    val extras = uiFormElem.extrasVar.bind
    val sizeClass = extras.valueFor(SIZE).map(_.toLowerCase).getOrElse("")
    <div class={s"ui $horDivider divider"}>
      <div class={s"ui $sizeClass header"}>
        {text}
      </div>
    </div>
  }

}

object SpacerDiv extends BaseElementDiv {

  @dom
  def create(uiFormElem: UIFormElem): Binding[HTMLElement] = {
    <div>
    </div>
  }

}