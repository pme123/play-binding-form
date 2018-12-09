package pme123.form.client

import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.{Event, HTMLElement}
import org.scalajs.jquery.jQuery
import pme123.form.shared.ElementType.{TEXTAREA, TEXTFIELD, TITLE}
import pme123.form.shared.ExtraProp.SIZE
import pme123.form.shared.services.Language
import pme123.form.shared.{BaseElement, ElementTexts, ExtraProp}

sealed abstract class BaseElementDiv {

  protected def create: Binding[HTMLElement]

  @dom
  def label(elementTexts: ElementTexts, activeLanguage: Language): Binding[HTMLElement] =
    <label class="">
      {elementTexts.label.textFor(activeLanguage)}{//
      if (elementTexts.tooltip.textFor(activeLanguage).nonEmpty)
        <i class="question circle outline icon ui tooltip"
           title={elementTexts.tooltip.textFor(activeLanguage)}></i>
      else <span/>}
    </label>

}

object BaseElementDiv {

  @dom
  def apply(elem: BaseElement): Binding[HTMLElement] =
    <div class="content">
      {elem.elementType match {
      case TEXTFIELD =>
        TextFieldDiv(elem).create.bind
      case TEXTAREA =>
        TextAreaDiv(elem).create.bind
      case TITLE =>
        TitleDiv(elem).create.bind
      case _ =>
        <div>
          {elem.ident}
        </div>
    }}
    </div>

}

case class TextFieldDiv(elem: BaseElement) extends BaseElementDiv {

  @dom
  lazy val create: Binding[HTMLElement] = {
    val activeLanguage = FormUIStore.uiState.activeLanguage.bind

    <div class="field">
      {label(elem.texts, activeLanguage).bind //
      }<div class="ui input">
      <input id={elem.ident}
             type="text"
             name="text"
             placeholder={elem.texts.placeholder.textFor(activeLanguage)}
             value={elem.defaultValue}
             onblur={_: Event =>
               val newText = jQuery(s"#${elem.ident}").value().toString
               elem.changeEvent
                 .foreach(ce =>
                   ce(ExtraProp.withNameInsensitive(elem.ident), newText))}/>
    </div>
    </div>
  }

}

case class TextAreaDiv(elem: BaseElement) extends BaseElementDiv {

  @dom
  lazy val create: Binding[HTMLElement] = {
    val activeLanguage = FormUIStore.uiState.activeLanguage.bind

    <div class="field">
      {label(elem.texts, activeLanguage).bind //
      }<div class="ui input">
      <textarea id={elem.ident}
                name={elem.ident}
                placeholder={elem.texts.placeholder.textFor(activeLanguage)}
                value={elem.defaultValue}
                rows={6}>
      </textarea>
    </div>
    </div>
  }

}

case class TitleDiv(elem: BaseElement) extends BaseElementDiv {

  @dom
  lazy val create: Binding[HTMLElement] = {
    val activeLanguage = FormUIStore.uiState.activeLanguage.bind

    <div class="field">
      <div class={s"ui ${elem.extras(SIZE).defaultValue} header"}>
        {elem.texts.label.textFor(activeLanguage) //
        }
      </div>
    </div>
  }

}
