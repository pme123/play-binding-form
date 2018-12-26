package pme123.form.client

import com.softwaremill.quicklens._
import com.thoughtworks.binding.Binding.{Var, Vars}
import pme123.form.client.services.SemanticUI
import pme123.form.shared._
import pme123.form.shared.services.Logging

import scala.language.implicitConversions


object UIDataStore extends Logging {

  val uiState = UIState()

  def changeIdent(parent: Var[VarDataObject], oldIdent: String)(newIdent: String): Unit = {
    info(s"DataUIStore: changeIdent $newIdent")
    val oldValue = parent.value.content(oldIdent)
    parent.value = VarDataObject((parent.value.content - oldIdent).updated(newIdent, oldValue))
    SemanticUI.initElements()
  }

  def changeIdents(idents: Seq[String]): Unit = {
    info(s"FormUIStore: changeIdents $idents")
    uiState.idents.value.clear()
    uiState.idents.value ++= idents
    SemanticUI.initElements()
  }
  def changeData(data: DataContainer): Unit = {
    info(s"DataUIStore: changeData ${data.ident}")
    uiState.data.value = VarDataContainer(data)
  }

  def changeDataIdent(ident: String): Unit = {
    info(s"DataUIStore: changeDataIdent $ident")
    uiState.data.value = uiState.data.value.modify(_.ident).setTo(ident)
    SemanticUI.initElements()
  }

  def changeStructureType(data: Var[VarDataStructure])(strTypeStr: String): Unit = {
    info(s"DataUIStore: changeStructureType $strTypeStr")
    val structureType = StructureType.withNameInsensitive(strTypeStr)

    data.value = VarDataStructure(DataStructure(structureType))
    SemanticUI.initElements()
  }

  def addDataObject(parentDS: Var[VarDataObject]): Unit = {
    val parentContent = parentDS.value.content
    info(s"DataUIStore: addDataObject $parentContent".take(100))
    parentDS.value = parentDS.value.modify(_.content).setTo(parentContent.updated(DataStructure.defaultKey, Var(VarDataString())))
    SemanticUI.initElements()
  }

  def deleteDataObject(dataIdent: String, parent: Var[UIDataStore.VarDataObject]): Unit = {
    info(s"DataUIStore: deleteDataObject $dataIdent")
    parent.value = VarDataObject(parent.value.content - dataIdent)
    SemanticUI.initElements()
  }


  case class UIState(
                      data: Var[VarDataContainer],
                      idents: Vars[String]
                    )

  object UIState {
    def apply(): UIState = {
      UIState(
        Var(VarDataContainer()),
        Vars.empty
      )
    }
  }

  case class VarDataContainer(ident: String = DataStructure.defaultKey, structure: Var[VarDataObject] = Var(VarDataObject())) {
    lazy val toData: DataContainer = DataContainer(ident, structure.value.toData)

  }

  object VarDataContainer {
    def apply(data: DataContainer): VarDataContainer =
      VarDataContainer(data.ident, Var(VarDataObject.create(data.structure.value)))
  }

  sealed abstract class VarDataStructure {

    def structureType: StructureType

    def toData: DataStructure

  }

  object VarDataStructure {
    def apply(ds: DataStructure): VarDataStructure = ds match {
      case DataBoolean(value) => VarDataBoolean(Var(value))
      case DataString(value) => VarDataString(Var(value))
      case DataNumber(value) => VarDataNumber(Var(value))
      case DataObject(value) => VarDataObject.create(value)
    }
  }

  case class VarDataObject(content: Map[String, Var[VarDataStructure]] = Map.empty, level: Int = 0)
    extends VarDataStructure {
    val structureType: StructureType = StructureType.OBJECT

    def toData: DataObject = DataObject(content.map { case (k, v) => k -> v.value.toData })

  }

  object VarDataObject {
    def create(content: Map[String, DataStructure]): VarDataObject = {
      val map = content.map(entry => (entry._1, Var(VarDataStructure(entry._2))))
      VarDataObject(map)
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
