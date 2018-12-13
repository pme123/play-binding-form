package pme123.form.client

import com.thoughtworks.binding.Binding.Constants
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.{Event, HTMLElement}
import org.scalajs.jquery.jQuery
import pme123.form.client.services.UIStore
import pme123.form.shared.ElementType.{DROPDOWN, TEXTAREA, TEXTFIELD, TITLE}
import pme123.form.shared.ExtraProp.SIZE
import pme123.form.shared.services.Language
import pme123.form.shared.{ElementEntry, ElementTexts}
sealed abstract class BaseElementDiv {

  @dom
  def label(elementTexts: ElementTexts, activeLanguage: Language): Binding[HTMLElement] =
    <label class="">
      {elementTexts.label.textFor(activeLanguage)}&nbsp;{//
      if (elementTexts.tooltip.textFor(activeLanguage).nonEmpty)
        <i class="question circle icon ui tooltip"
           title={elementTexts.tooltip.textFor(activeLanguage)}></i>
      else <span/>}
    </label>

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
      case TITLE =>
        TitleDiv.create(uiFormElem).bind
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
    <div class="field">
      {label(elem.texts, activeLanguage).bind //
      }<div class="ui selection dropdown">
      <input id={elem.ident}
             name={elem.ident}
             type="hidden"
             value={elem.value.get}
             onchange={_: Event =>
               val newText = jQuery(s"#${elem.ident}").value().toString
               uiFormElem.changeEvent
                 .foreach(ce =>
                   ce(newText))}/>
      <i class="dropdown icon"></i>
      <div class="default text">
        {elem.value.get}
      </div>
      <div class="menu">
        {Constants(elem.elemEntries.entries.map(elementEntry(_, activeLanguage)): _*).map(_.bind)}
      </div>
    </div>
    </div>
  }

  @dom
  private def elementEntry(entry: ElementEntry, language: Language): Binding[HTMLElement] = {
    <div class="item" data:data-value={entry.ident}>
      {entry.label.textFor(language)}
    </div>
  }

}

object TextFieldDiv extends BaseElementDiv {

  @dom
  def create(uiFormElem: UIFormElem): Binding[HTMLElement] = {
    val activeLanguage = UIStore.activeLanguage.bind
    val elem = uiFormElem.elem
    <div class="field">
      {label(elem.texts, activeLanguage).bind //
      }<div class="ui input">
      <input id={elem.ident}
             name={elem.ident}
             type="text"
             placeholder={elem.texts.placeholder.textFor(activeLanguage)}
             value={elem.value.get}
             onblur={_: Event =>
               val newText = jQuery(s"#${elem.ident}").value().toString
               uiFormElem.changeEvent
                 .foreach(ce =>
                   ce(newText))}/>
    </div>
    </div>
  }

}

object TextAreaDiv extends BaseElementDiv {

  @dom
  def create(uiFormElem: UIFormElem): Binding[HTMLElement] = {
    val activeLanguage = UIStore.activeLanguage.bind
    val elem = uiFormElem.elem
    <div class="field">
      {label(elem.texts, activeLanguage).bind //
      }<div class="ui input">
      <textarea id={elem.ident}
                name={elem.ident}
                placeholder={elem.texts.placeholder.textFor(activeLanguage)}
                value={elem.value.get}
                rows={6}>
      </textarea>
    </div>
    </div>
  }

}

object TitleDiv extends BaseElementDiv {

  @dom
  def create(uiFormElem: UIFormElem): Binding[HTMLElement] = {
    val activeLanguage = UIStore.activeLanguage.bind
    val elem = uiFormElem.elem
    <div class="field">
      <div class={s"ui ${elem.extras(SIZE).value} header"}>
        {elem.texts.label.textFor(activeLanguage) //
        }
      </div>
    </div>
  }

}
