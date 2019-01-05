package pme123.form.client.form

import pme123.form.client.services.{SemanticUI, UIStore}
import pme123.form.shared._
import pme123.form.shared.services.Logging

object UIElemEntriesStore extends Logging {

  import UIStore.supportedLangs

  val uiState: UIFormStore.UIState = UIFormStore.uiState

  private def uiElem = {
    uiState.selectedElement.value.value
  }

  def addEntry(): Unit = {
    info(s"FormUIStore: addElementEntry")
    uiElem.elemEntriesVar.value = UIElementEntries(
      uiElem.elemEntriesVar.value.entries :+ UIElementEntry(ElementEntry())
    )
  }

  def deleteEntry(entry: UIElementEntry): Unit = {
    info(s"PropertyUIStore: deleteEntry ${entry.ident}")
    uiElem.elemEntriesVar.value = UIElementEntries(
      uiElem.elemEntriesVar.value.entries
        .filter(_ != entry)
    )
  }

  def copyEntry(entry: UIElementEntry): Unit = {
    info(s"PropertyUIStore: copyEntry ${entry.ident}")
    uiElem.elemEntriesVar.value = UIElementEntries(
      uiElem.elemEntriesVar.value.entries
        .foldLeft(Seq.empty[UIElementEntry])((a, b) =>
          if (b.ident == entry.ident)
            a :+ b :+ b.copy(ident = ElementEntry.ident)
          else
            a :+ b
        )
    )
  }

  def moveEntry(draggedEntry: UIElementEntry, moveToEntry: UIElementEntry): Unit = {
    info(s"PropertyUIStore: moveEntry ${draggedEntry.ident}")
    uiElem.elemEntriesVar.value = UIElementEntries(
      uiElem.elemEntriesVar.value.entries
        .foldLeft(Seq.empty[UIElementEntry])((a, b) =>
          if (b.ident == moveToEntry.ident)
            a :+ b :+ draggedEntry
          else if (b.ident == draggedEntry.ident)
            a
          else
            a :+ b
        )
    )
    SemanticUI.initElements()
  }


}
