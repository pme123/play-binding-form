package pme123.form.client.data

import com.thoughtworks.binding.Binding.Var
import org.scalajs.dom.DragEvent
import pme123.form.client.data.UIDataStore.VarDataStructure
import pme123.form.client.form.{ElemEntryDragDrop, UIFormStore}

object DataStructureDragDrop {

  val dragObject: Var[Option[Var[_ <: VarDataStructure]]] = Var(None)

  def allowDrop(moveToDataStructure: Var[_ <: VarDataStructure])(ev: DragEvent): Unit = {
    dragObject.value match {
      case Some(v) if v == moveToDataStructure => // just select
      case Some(v) if v.getClass == moveToDataStructure.getClass => // allow drop
        ev.preventDefault
      case _ => 
    }
  }

  def drag(uiElemVar: Var[_ <: VarDataStructure]): Unit = {
    dragObject.value = Some(uiElemVar)
    ElemEntryDragDrop.dragObject.value = None
  }

  def drop(moveToElemVar: Var[_ <: VarDataStructure])(ev: DragEvent): Unit = {
    ev.preventDefault
  //  dragObject.value
   //   .foreach(UIDataStore.moveElement(_, moveToElemVar))
    dragObject.value = None
  }
}
