package pme123.form.client.services

import com.thoughtworks.binding.Binding.Var
import org.scalajs.dom.DragEvent
import pme123.form.client.{FormUIStore, UIFormElem}

case class DragDrop(dragObject: Var[UIFormElem]) {

  def allowDrop(ev: DragEvent): Unit = ev.preventDefault

 def drop(moveToElemVar: Var[UIFormElem])(ev: DragEvent): Unit = {
   ev.preventDefault
   FormUIStore.moveElement(dragObject, moveToElemVar)
 }
}
