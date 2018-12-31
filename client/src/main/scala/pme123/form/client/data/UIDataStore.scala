package pme123.form.client.data

import com.softwaremill.quicklens._
import com.thoughtworks.binding.Binding.{Var, Vars}
import pme123.form.client.services.SemanticUI
import pme123.form.shared._
import pme123.form.shared.services.Logging

object UIDataStore extends Logging {

  val uiState = UIState()

  def changeIdent(parent: Var[VarDataObject], oldIdent: String)(newIdent: String): Unit = {
    info(s"DataUIStore: changeIdent $newIdent")
    val oldValue = parent.value.content(oldIdent)
    parent.value = VarDataObject(parent.value.ident, (parent.value.content - oldIdent).updated(newIdent, oldValue))
    SemanticUI.initElements()
  }

  def changeIdents(idents: Seq[String]): Unit = {
    info(s"DataUIStore: changeIdents $idents")
    uiState.idents.value.clear()
    uiState.idents.value ++= idents
    SemanticUI.initElements()
  }

  def changeData(data: DataContainer): Var[VarDataContainer] = {
    info(s"DataUIStore: changeData ${data.ident}")
    uiState.identVar.value = data.ident
    uiState.data.value = VarDataContainer(uiState.identVar, data)

    SemanticUI.initElements()
    uiState.data
  }

  def changeDataIdent(ident: String): Unit = {
    info(s"DataUIStore: changeDataIdent $ident")
    uiState.data.value.identVar.value = ident
    SemanticUI.initElements()
  }

  def changeStructureType(data: Var[VarDataStructure])(strTypeStr: String): Unit = {
    info(s"DataUIStore: changeStructureType $strTypeStr")
    val structureType = StructureType.withNameInsensitive(strTypeStr)

    data.value = VarDataStructure(data.value.ident, DataStructure(structureType))
    SemanticUI.initElements()
  }

  def addDataObject(parentDS: Var[VarDataObject]): Unit = {
    val parentContent = parentDS.value.content
    info(s"DataUIStore: addDataObject $parentContent".take(100))
    parentDS.value = parentDS.value.modify(_.content).setTo(parentContent.updated(DataStructure.defaultKey, Var(VarDataValue())))
    SemanticUI.initElements()
  }

  def deleteDataObject(dataIdent: String, parent: Var[UIDataStore.VarDataObject]): Unit = {
    info(s"DataUIStore: deleteDataObject $dataIdent")
    parent.value = VarDataObject(parent.value.ident, parent.value.content - dataIdent)
    SemanticUI.initElements()
  }

  def dataValue(searchIdent: String): Option[Var[VarDataValue]] =
    dataValues()
      .find(_.value.ident == searchIdent)

  def dataValues(): Seq[Var[VarDataValue]] =
    uiState.data.value
      .findValues()

  def dataValueIdents(): Seq[String] =
    dataValues()
    .map(_.value.ident)

  case class UIState(
                      identVar: Var[String],
                      data: Var[VarDataContainer],
                      idents: Vars[String]
                    )

  object UIState {

    def apply(): UIState = {
      val identVar = Var(DataStructure.defaultKey)
      UIState(
        identVar,
        Var(VarDataContainer(identVar)),
        Vars.empty
      )
    }
  }

  case class VarDataContainer(identVar: Var[String], structure: Var[VarDataObject] = Var(VarDataObject())) {

    lazy val toData: DataContainer = DataContainer(identVar.value, structure.value.toData)

    def findValues(): Seq[Var[VarDataValue]] =
      structure.value.findValues()

  }

  object VarDataContainer {
    def apply(identVar: Var[String], data: DataContainer): VarDataContainer =
      VarDataContainer(identVar, Var(VarDataObject.create(data.ident, data.structure.value)))
  }

  sealed abstract class VarDataStructure {

    def ident: String

    def structureType: StructureType

    def toData: DataStructure

    def findValues(): Seq[Var[VarDataValue]] = Nil

  }

  object VarDataStructure {
    def apply(ident: String, ds: DataStructure): VarDataStructure = ds match {
      case DataBoolean(value) => VarDataValue(ident, StructureType.BOOLEAN, Var(value.toString))
      case DataString(value) => VarDataValue(ident, StructureType.STRING, Var(value))
      case DataNumber(value) => VarDataValue(ident, StructureType.NUMBER, Var(value.toString))
      case DataObject(value) => VarDataObject.create(ident, value)
    }
  }

  case class VarDataObject(ident: String = DataStructure.defaultKey, content: Map[String, Var[VarDataStructure]] = Map.empty)
    extends VarDataStructure {
    val structureType: StructureType = StructureType.OBJECT

    def toData: DataObject = DataObject(content.map { case (k, v) => k -> v.value.toData })

    override def findValues(): Seq[Var[VarDataValue]] =
      content.values
      .filter((vdv: Var[VarDataStructure]) => vdv.value.isInstanceOf[VarDataValue])
      .map(_.asInstanceOf[Var[VarDataValue]])
      .toSeq

  }

  object VarDataObject {
    def create(ident: String, content: Map[String, DataStructure]): VarDataObject = {
      val map = content.map(entry => (entry._1, Var(VarDataStructure(entry._1, entry._2))))
      VarDataObject(ident, map)
    }
  }

  case class VarDataValue(ident: String, structureType: StructureType, content: Var[String] = Var(""))
    extends VarDataStructure {

    def toData: DataStructure = structureType match {
      case StructureType.STRING => DataString(content.value)
      case StructureType.NUMBER => DataNumber(BigDecimal(content.value))
      case StructureType.BOOLEAN => DataBoolean(content.value.toBoolean)
      case _ => DataString()
    }

  }

  object VarDataValue {
    def apply(): VarDataValue = new VarDataValue(DataStructure.defaultKey, StructureType.STRING, Var(""))
  }

}
