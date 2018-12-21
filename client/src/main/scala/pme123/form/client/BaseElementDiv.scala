package pme123.form.client

import com.thoughtworks.binding.Binding.Constants
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.{Event, HTMLElement}
import org.scalajs.jquery.jQuery
import pme123.form.client.services.UIStore
import pme123.form.shared.ElementType._
import pme123.form.shared.ExtraProp.{CHECKBOX_TYPE, CLEARABLE, SIZE}
import pme123.form.shared.services.Language
import pme123.form.shared.{ElementEntry, ElementTexts}

sealed abstract class BaseElementDiv {

  @dom
  protected def label(elementTexts: ElementTexts, activeLanguage: Language, required: Boolean): Binding[HTMLElement] = {
    val requiredStr = if (required) "*" else ""
    if (elementTexts.label.textFor(activeLanguage).nonEmpty)
      <label class="">
        {elementTexts.label.textFor(activeLanguage) + requiredStr}&nbsp;{//
        if (elementTexts.tooltip.textFor(activeLanguage).nonEmpty)
          <i class="question circle icon ui tooltip"
             title={elementTexts.tooltip.textFor(activeLanguage)}></i>
        else <span/>}
      </label>
    else <span/>

  }

  protected def changeEvent(uiFormElem: UIFormElem): Unit = {
    val newText = jQuery(s"#${uiFormElem.elem.ident}").value().toString
    uiFormElem.changeEvent
      .foreach(ce =>
        ce(newText))
  }
}

object BaseElementDiv {


  @dom
  def apply(uiFormElem: UIFormElem): Binding[HTMLElement] = {
    val elem = uiFormElem.elem
    <div class="content">
      {elem.elementType match {
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
          {elem.ident}
        </div>
    }}
    </div>
  }

}

object DropdownDiv extends BaseElementDiv {

  @dom
  def create(uiFormElem: UIFormElem): Binding[HTMLElement] = {
    val activeLanguage = UIStore.activeLanguage.bind
    val elem = uiFormElem.elem
    val clearableClass = if (elem.extras.valueFor(CLEARABLE).contains("true")) "clearable" else ""
    val texts = elem.texts.get
    <div class="field">
      {label(texts, activeLanguage, elem.required).bind //
      }<div class={s"ui $clearableClass selection dropdown"}>
      <input id={elem.ident}
             name={elem.ident}
             type="hidden"
             value={elem.value.get}
             onchange={_: Event =>
               changeEvent(uiFormElem)}/>
      <i class="dropdown icon"></i>
      <div class="default text">
        {elem.value.get}
      </div>
      <div class="menu">
        {Constants(elem.elemEntries.toSeq.flatMap(_.entries).map(elementEntry(_, activeLanguage)): _*).map(_.bind)}
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
    val elem = uiFormElem.elem
    val texts = elem.texts
    val size = elem.extras.valueFor(SIZE).map(_.toInt).getOrElse(20)
    <div class="field">
      {if (texts.nonEmpty) label(texts.get, activeLanguage, elem.required).bind else <span/> //
      }<div class="ui input">
      <input id={elem.ident}
             name={elem.ident}
             size={size}
             type="text"
             placeholder={texts.map(_.placeholder.textFor(activeLanguage)).getOrElse("")}
             value={elem.value.get}
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
    val elem = uiFormElem.elem
    val texts = elem.texts.get
    <div class="field">
      {label(texts, activeLanguage, elem.required).bind //
      }<div class="ui input">
      <textarea id={elem.ident}
                name={elem.ident}
                placeholder={texts.placeholder.textFor(activeLanguage)}
                value={elem.value.get}
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
    val elem = uiFormElem.elem
    val typeClass = uiFormElem.elem.extras.valueFor(CHECKBOX_TYPE).map(_.toLowerCase).getOrElse("")
    val checkedClass = if (elem.value.contains("true")) "checked" else ""
    val checked = elem.value.contains("true")
    val texts = elem.texts
    <div class="field">
      {label(texts.get, activeLanguage, elem.required).bind //
      }<div class={s"ui $typeClass checkbox $checkedClass"}>
        <input id={elem.ident}
               name={elem.ident}
               type="checkbox"
               checked={checked}
               tabIndex={0}
               onchange={_: Event =>
                 val newText = jQuery(s"#${uiFormElem.elem.ident}").is(":checked").toString
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
    val elem = uiFormElem.elem
    val sizeClass = elem.extras.valueFor(SIZE).map(_.toLowerCase).getOrElse("")
    val texts = elem.texts.get
    <div class="field">
      <div class={s"ui $sizeClass header"}>
        {texts.label.textFor(activeLanguage) //
        }
      </div>
    </div>
  }

}

object DividerDiv extends BaseElementDiv {

  @dom
  def create(uiFormElem: UIFormElem): Binding[HTMLElement] = {
    val activeLanguage = UIStore.activeLanguage.bind
    val elem = uiFormElem.elem
    val texts = elem.texts.get
    val text = texts.label.textFor(activeLanguage)
    val horDivider = if (text.nonEmpty) "horizontal" else ""
    val sizeClass = elem.extras.valueFor(SIZE).map(_.toLowerCase).getOrElse("")
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