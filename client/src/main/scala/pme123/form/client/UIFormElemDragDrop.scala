package pme123.form.client

import com.thoughtworks.binding.Binding.Var
import org.scalajs.dom.DragEvent

object UIFormElemDragDrop {

  val dragObject: Var[Option[Var[UIFormElem]]] = Var(None)

  def allowDrop(moveToElemVar: Var[UIFormElem])(ev: DragEvent): Unit = {
    if (dragObject.value.exists(_ != moveToElemVar) &&
      dragObject.value.exists(_.getClass == moveToElemVar.getClass))
      ev.preventDefault
  }

  def drag(uiElemVar: Var[UIFormElem]): Unit = {
    dragObject.value = Some(uiElemVar)
    ElemEntryDragDrop.dragObject.value = None
  }

  def drop(moveToElemVar: Var[UIFormElem])(ev: DragEvent): Unit = {
    ev.preventDefault
    dragObject.value
      .foreach(UIFormStore.moveElement(_, moveToElemVar))
    dragObject.value = None
  }
}
