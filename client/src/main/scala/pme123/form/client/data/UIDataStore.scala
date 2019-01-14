package pme123.form.client.data

import com.thoughtworks.binding.Binding.{Var, Vars}
import pme123.form.client.data.UIDataStore.VarDataStructure
import pme123.form.client.services.SemanticUI
import pme123.form.shared.ElementType.{CHECKBOX, TEXTFIELD}
import pme123.form.shared.ExtraProp.INPUT_TYPE
import pme123.form.shared._
import pme123.form.shared.services.Logging

object UIDataStore extends Logging {

  val uiState = UIState()

  def changeIdent(data: Var[VarDataStructure])(newIdent: String): Unit = {
    info(s"DataUIStore: changeIdent $newIdent")
    data.value.identVar.value = newIdent
    data.value.adjustPath()
    SemanticUI.initElements()
  }

  def changeIdents(idents: Seq[String]): Unit = {
    info(s"DataUIStore: changeIdents $idents")
    uiState.idents.value.clear()
    uiState.idents.value ++= idents
    SemanticUI.initElements()
  }

  def changeData(data: DataObject): Var[VarDataObject] = {
    info(s"DataUIStore: changeData ${data.ident}")
    uiState.identVar.value = data.ident
    uiState.data.value = VarDataObject.create(uiState.identVar, data.value, Var(Nil))

    SemanticUI.initElements()
    uiState.data
  }

  def changeStructureType(data: Var[VarDataStructure])(strTypeStr: String): Unit = {
    info(s"DataUIStore: changeStructureType $strTypeStr")
    val structureType = StructureType.withNameInsensitive(strTypeStr)

    data.value = VarDataStructure.apply(data.value.identVar, DataStructure(structureType), data.value.parentPathVar)
    SemanticUI.initElements()
  }

  def addDataObject(parentDS: Var[VarDataObject]): Unit = {
    val parentContent = parentDS.value.content
    info(s"DataUIStore: addDataObject $parentContent".take(100))
    parentContent.value += Var(VarDataValue(parentDS.value.path))
    SemanticUI.initElements()
  }

  def deleteDataObject(dataIdent: String, contentVar: Vars[Var[_ <: VarDataStructure]]): Unit = {
    info(s"DataUIStore: deleteDataObject $dataIdent from Parent")
    val deleteCandidates = contentVar.value.filter(_.value.identVar.value == dataIdent)
    contentVar.value -= deleteCandidates.head
    SemanticUI.initElements()
  }

  def dataValue(searchIdent: String): Option[Var[VarDataValue]] =
    dataValues()
      .find(_.value.identVar.value == searchIdent)

  def dataPaths: Seq[String] =
    dataValues()
      .map(_.value.pathString)

  def dataValues(): Seq[Var[VarDataValue]] =
    uiState.data.value
      .findValues()

  case class UIState(
                      identVar: Var[String],
                      data: Var[VarDataObject],
                      idents: Vars[String]
                    )

  object UIState {

    def apply(): UIState = {
      val identVar = Var(DataStructure.defaultKey)
      UIState(
        identVar,
        Var(VarDataObject(identVar)),
        Vars.empty
      )
    }
  }

  /*
    object VarDataContainer {
      def apply(identVar: Var[String], data: DataContainer): VarDataContainer =
        VarDataContainer(identVar, Var(VarDataObject.create(identVar, data.structure.value, Var(Nil))))

      def apply(identVar: Var[String], form: FormContainer): VarDataContainer =
        VarDataContainer(identVar,
          Var(VarDataObject.create(identVar,
            form.elems.filterNot(_.elementType.readOnly)
              .map(el => el.elementType match {
                case TEXTFIELD if el.extras.propValue(INPUT_TYPE).contains(InputType.NUMBER.key) =>
                  DataNumber(el.ident, el.value.map(BigDecimal(_)))
                case CHECKBOX =>
                  DataBoolean(el.ident, el.value.map(_.toBoolean))
                case _ =>
                  DataString(el.ident, el.value)
              }
              ), Var(Nil)
          )))
    }
  */
  sealed abstract class VarDataStructure {

    def identVar: Var[String]

    def parentPathVar: Var[Seq[String]]

    def path: Seq[String] = parentPathVar.value :+ identVar.value

    def pathString: String =
      if(path.size >2)
        "../" + path.takeRight(2).mkString("/")
      else
        path.mkString("/")

    def structureType: StructureType

    def toData: DataStructure

    def adjustPath(): Unit = ()

    def adjustPath(path: Seq[String]): Unit = {
      parentPathVar.value = path
      adjustPath()
    }

    def isChild(moveToPath: Seq[String]): Boolean = {
      moveToPath.mkString(",")
        .startsWith(
          parentPathVar.value.mkString(",")
        ) && moveToPath.size != parentPathVar.value.size
    }

    def contents: Seq[Var[_ <: VarDataStructure]] = Nil

    def findValues(): Seq[Var[VarDataValue]] =
      contents
        .flatMap{
          case value: Var[VarDataValue] if value.value.isInstanceOf[VarDataValue] =>
            Seq(value)
          case obj:Var[VarDataObject] =>
            obj.value.findValues()
        }
  }

  object VarDataStructure {
    def apply(identVar: Var[String], ds: DataStructure, parentPathVar: Var[Seq[String]]): VarDataStructure = ds match {
      case DataBoolean(_, value) => VarDataValue(identVar, StructureType.BOOLEAN, Var(value.map(_.toString)), parentPathVar)
      case DataString(_, value) => VarDataValue(identVar, StructureType.STRING, Var(value), parentPathVar)
      case DataNumber(_, value) => VarDataValue(identVar, StructureType.NUMBER, Var(value.map(_.toString)), parentPathVar)
      case DataObject(_, value) => VarDataObject.create(identVar, value, parentPathVar)
    }
  }

  case class VarDataObject(identVar: Var[String] = Var(DataStructure.defaultKey),
                           content: Vars[Var[_ <: VarDataStructure]] = Vars.empty,
                           parentPathVar: Var[Seq[String]] = Var(Nil))
    extends VarDataStructure {
    val structureType: StructureType = StructureType.OBJECT

    def toData: DataObject = DataObject(identVar.value, content.value.map(_.value.toData))

    override def adjustPath(): Unit = {
      content.value
        .foreach(_.value.adjustPath(path :+ identVar.value))
    }

    override def contents: Seq[Var[_ <: VarDataStructure]] = content.value
  }

  object VarDataObject {
    def create(identVar: Var[String],
               content: Seq[DataStructure],
               parentPathVar: Var[Seq[String]]): VarDataObject = {
      val seq = content.map((entry: DataStructure) => Var(VarDataStructure(Var(entry.ident), entry, Var(parentPathVar.value :+ identVar.value))))
      VarDataObject(identVar, Vars(seq: _*), parentPathVar)
    }

    def apply(identVar: Var[String], form: FormContainer): VarDataObject =
      VarDataObject.create(identVar,
        form.elems.filterNot(_.elementType.readOnly)
          .map(el => el.elementType match {
            case TEXTFIELD if el.extras.propValue(INPUT_TYPE).contains(InputType.NUMBER.key) =>
              DataNumber(el.ident, el.value.map(BigDecimal(_)))
            case CHECKBOX =>
              DataBoolean(el.ident, el.value.map(_.toBoolean))
            case _ =>
              DataString(el.ident, el.value)
          })
        , Var(Nil))
  }

}

case class VarDataValue(identVar: Var[String],
                        structureType: StructureType,
                        contentVar: Var[Option[String]],
                        parentPathVar: Var[Seq[String]])
  extends VarDataStructure {

  def toData: DataStructure = structureType match {
    case StructureType.STRING => DataString(identVar.value, contentVar.value)
    case StructureType.NUMBER => DataNumber(identVar.value, contentVar.value.map(BigDecimal(_)))
    case StructureType.BOOLEAN => DataBoolean(identVar.value, contentVar.value.map(_.toBoolean))
    case _ => DataString()
  }

}

object VarDataValue {
  def apply(parentPath: Seq[String]): VarDataValue =
    new VarDataValue(Var(DataStructure.defaultKey), StructureType.STRING, Var(None), Var(parentPath))
}
