package pme123.form.client.form

import com.thoughtworks.binding.Binding.Var
import org.scalajs.dom.DragEvent

object UIFormElemDragDrop {

  val dragObject: Var[Option[Var[UIFormElem]]] = Var(None)

  def allowDrop(moveToElemVar: Var[UIFormElem])(ev: DragEvent): Unit = {
    dragObject.value match {
      case Some(v) if v == moveToElemVar => // just select
        UIFormStore.changeSelectedElement(moveToElemVar)
      case Some(v) if v.getClass == moveToElemVar.getClass => // allow drop
        ev.preventDefault
      case _ => 
    }
  }

  def drag(uiElemVar: Var[UIFormElem]): Unit = {
    dragObject.value = Some(uiElemVar)
    ElemEntryDragDrop.dragObject.value = None
  }

  def drop(moveToElemVar: Var[UIFormElem])(ev: DragEvent): Unit = {
    println("DROPPED")
    ev.preventDefault
    dragObject.value
      .foreach(UIFormStore.moveElement(_, moveToElemVar))
    dragObject.value = None
  }
}
