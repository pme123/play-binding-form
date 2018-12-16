package pme123.form.client

import com.thoughtworks.binding.Binding.Var
import org.scalajs.dom.DragEvent
import pme123.form.shared.ElementEntry

object ElemEntryDragDrop {

  val dragObject: Var[Option[ElementEntry]] = Var(None)

  def allowDrop(moveToEntry: ElementEntry)(ev: DragEvent): Unit = {
    println(s"allowDrop ${dragObject.value.get.key} - ${moveToEntry.key}")

    if (dragObject.value.exists(_.ident != moveToEntry.ident) &&
      dragObject.value.exists(_.getClass == moveToEntry.getClass))
      ev.preventDefault
  }

  def drag(uiEntry: ElementEntry): Unit = {
    dragObject.value = Some(uiEntry)
    UIFormElemDragDrop.dragObject.value = None
  }

  def drop(moveToEntry: ElementEntry)(ev: DragEvent): Unit = {
    ev.preventDefault
    dragObject.value
      .foreach(UIPropertyStore.moveEntry(_, moveToEntry))
    dragObject.value = None
  }
}
