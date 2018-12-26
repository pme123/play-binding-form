package pme123.form.client

import com.thoughtworks.binding.Binding.Var
import pme123.form.client.services.SemanticUI
import pme123.form.shared._
import pme123.form.shared.services.Logging

import scala.language.implicitConversions


object UIDataStore extends Logging {

  val uiState = UIState()

  def changeData(data: DataContainer): Unit = {
    info(s"FormUIStore: changeData ${data.ident}")
    uiState.data.value = VarDataContainer(data)
  }

  def addDataObject(parentDS: VarDataStructure): Unit = {
    parentDS match {
      case VarDataObject(content) =>
        info(s"FormUIStore: addDataObject $content".take(100))
        content.value = content.value.updated(DataStructure.defaultKey, VarDataString())
      case other =>
        error(s"Unexpected parent: $other") // unexpected
    }
    SemanticUI.initElements()
  }

  case class UIState(
                      data: Var[VarDataContainer],
                    )

  object UIState {
    def apply(): UIState = {
      UIState(
        Var(VarDataContainer()),
      )
    }
  }

  case class VarDataContainer(ident: String= DataStructure.defaultKey, structure: VarDataObject = VarDataObject()) {
    lazy val toData: DataContainer = DataContainer(ident, structure.toData)

  }

  object VarDataContainer {
    def apply(data: DataContainer): VarDataContainer =
      VarDataContainer(data.ident, VarDataObject(data.structure.value))
  }

  sealed abstract class VarDataStructure {
    def content: Var[_]

    def structureType: StructureType

    def toData: DataStructure

  }

  object VarDataStructure {
    def apply(ds: DataStructure): VarDataStructure = ds match {
      case DataBoolean(value) => VarDataBoolean(Var(value))
      case DataString(value) => VarDataString(Var(value))
      case DataNumber(value) => VarDataNumber(Var(value))
      case DataObject(value) => VarDataObject(value)
    }
  }

  case class VarDataObject(content: Var[Map[String, VarDataStructure]] = Var(Map.empty))
    extends VarDataStructure {
    val structureType: StructureType = StructureType.OBJECT

    def toData: DataObject = DataObject(content.value.map { case (k, v) => k -> v.toData })

  }

  object VarDataObject {
    def apply(content:  Map[String, DataStructure]): VarDataObject = {
      val map: Map[String, VarDataStructure] = content.map(entry => (entry._1, VarDataStructure(entry._2)))
      VarDataObject(Var(map))
    }
  }

  case class VarDataString(content: Var[String] = Var(""))
    extends VarDataStructure {
    val structureType: StructureType = StructureType.STRING

    def toData: DataString = DataString(content.value)

  }

  case class VarDataNumber(content: Var[BigDecimal] = Var(0))
    extends VarDataStructure {
    val structureType: StructureType = StructureType.NUMBER

    def toData: DataNumber = DataNumber(content.value)
  }

  case class VarDataBoolean(content: Var[Boolean] = Var(false))
    extends VarDataStructure {
    val structureType: StructureType = StructureType.BOOLEAN

    def toData: DataBoolean = DataBoolean(content.value)
  }

}
