package pme123.form.client.services

import com.thoughtworks.binding.Binding.Var
import org.scalajs.dom.DragEvent
import pme123.form.client.{FormUIStore, UIFormElem}

object DragDrop {

  val dragObject: Var[Option[Var[UIFormElem]]] = Var(None)

  def allowDrop(ev: DragEvent): Unit = ev.preventDefault

  def drag(uiElemVar: Var[UIFormElem]): Unit =
    dragObject.value = Some(uiElemVar)

 def drop(moveToElemVar: Var[UIFormElem])(ev: DragEvent): Unit = {
   ev.preventDefault
   dragObject.value
       .foreach(FormUIStore.moveElement(_, moveToElemVar))
   dragObject.value = None
 }
}
