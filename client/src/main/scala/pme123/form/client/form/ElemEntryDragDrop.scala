package pme123.form.client.form

import com.thoughtworks.binding.Binding.Var
import org.scalajs.dom.DragEvent

object ElemEntryDragDrop {

  val dragObject: Var[Option[UIElementEntry]] = Var(None)

  def allowDrop(moveToEntry: UIElementEntry)(ev: DragEvent): Unit = {

    if (dragObject.value.exists(_.ident != moveToEntry.ident) &&
      dragObject.value.exists(_.getClass == moveToEntry.getClass))
      ev.preventDefault
  }

  def drag(uiEntry: UIElementEntry): Unit = {
    dragObject.value = Some(uiEntry)
    UIFormElemDragDrop.dragObject.value = None
  }

  def drop(moveToEntry: UIElementEntry)(ev: DragEvent): Unit = {
    ev.preventDefault
    dragObject.value
      .foreach(UIPropertyStore.moveEntry(_, moveToEntry))
    dragObject.value = None
  }
}
