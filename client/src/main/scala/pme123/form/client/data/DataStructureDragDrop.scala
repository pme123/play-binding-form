package pme123.form.client.data

import com.thoughtworks.binding.Binding.{Var, Vars}
import org.scalajs.dom.DragEvent
import pme123.form.client.data.UIDataStore.{VarDataObject, VarDataStructure}
import pme123.form.client.services.SemanticUI

object DataStructureDragDrop {

  val dragObject: Var[Option[DataDragObject]] = Var(None)

  def allowDrop(moveToDataStructure: Var[_ <: VarDataStructure])(ev: DragEvent): Unit = {
    val dragObj = dragObject.value.map(_.dragObj.value)
    val notItself = dragObj.exists(_.varIdent != moveToDataStructure.value.varIdent)
    if (notItself)
      ev.preventDefault
  }

  def drag(uiElemVar: Var[_ <: VarDataStructure], parentContent: Vars[Var[_ <: VarDataStructure]]): Unit = {
    if (dragObject.value.isEmpty)
      dragObject.value = Some(DataDragObject(uiElemVar, parentContent))
  }

  def drop(moveToElemVar: Var[_ <: VarDataStructure], parentContent: Vars[Var[_ <: VarDataStructure]])(ev: DragEvent): Unit = {
    if (dragObject.value.nonEmpty) {
      ev.preventDefault
      moveToElemVar.value match {
        case VarDataObject(varIdent, _, content, parentPathVar) =>
          val dataDragObj = dragObject.value.get
          val dragObj = dataDragObj.dragObj
          val dragIdent = dragObj.value.varIdent.value
          val isChild = dragObj.value.isChild(parentPathVar.value)
          if (varIdent.value != dragIdent && !isChild) {
            dragObj.value.adjustPath(moveToElemVar.value.path)
            content.value += dragObj
            dataDragObj.parentContent.value -= dragObj
          }
        case VarDataValue(varIdent, _, _, parentPathVar) =>
          val dataDragObj = dragObject.value.get
          val dragObjVar = dataDragObj.dragObj
          val dragIdent = dragObjVar.value.varIdent.value
          val isChild = dragObjVar.value.isChild(parentPathVar.value)
          if (varIdent.value != dragIdent) {
            if (parentPathVar.value == dragObjVar.value.parentPathVar.value) {
              val existIndex = parentContent.value.indexOf(dataDragObj.dragObj)
              val newIndex = parentContent.value.indexOf(moveToElemVar)
              parentContent.value.remove(existIndex)
              parentContent.value.insert(newIndex, dataDragObj.dragObj)
            } else if (!isChild) {
              val existIndex = dataDragObj.parentContent.value.indexOf(dataDragObj.dragObj)
              val newIndex = parentContent.value.indexOf(moveToElemVar)
              dataDragObj.parentContent.value.remove(existIndex)
              parentContent.value.insert(newIndex, dataDragObj.dragObj)
            }
          }

      }
      SemanticUI.initElements()
    }
    dragObject.value = None
  }

}

case class DataDragObject(dragObj: Var[_ <: VarDataStructure], parentContent: Vars[Var[_ <: VarDataStructure]]) {

}

