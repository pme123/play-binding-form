package pme123.form.client.data

import com.thoughtworks.binding.Binding.{Var, Vars}
import pme123.form.client.data.UIDataStore.VarDataStructure
import pme123.form.client.services.SemanticUI
import pme123.form.shared.Cardinality.ONE
import pme123.form.shared.ElementType.{CHECKBOX, TEXTFIELD}
import pme123.form.shared.ExtraProp.INPUT_TYPE
import pme123.form.shared.StructureType.{BOOLEAN, NUMBER, STRING}
import pme123.form.shared._
import pme123.form.shared.services.Logging

object UIDataStore extends Logging {


  val uiState = UIState()

  def changeIdent(data: Var[VarDataStructure])(newIdent: String): Unit = {
    info(s"DataUIStore: changeIdent $newIdent")
    data.value.varIdent.value = newIdent
    data.value.adjustPath()
    SemanticUI.initElements()
  }

  def changeIdents(idents: Seq[String]): Unit = {
    info(s"DataUIStore: changeIdents $idents")
    uiState.varsIdents.value.clear()
    uiState.varsIdents.value ++= idents
    SemanticUI.initElements()
  }

  def changeData(data: DataObject): Var[VarDataObject] = {
    info(s"DataUIStore: changeData ${data.ident}")
    uiState.varIdent.value = data.ident
    uiState.data.value = VarDataObject.create(uiState.varIdent, data.cardinality, data.children, Var(Nil))

    SemanticUI.initElements()
    uiState.data
  }

  def changeStructureType(data: Var[VarDataStructure])(strTypeStr: String): Unit = {
    info(s"DataUIStore: changeStructureType $strTypeStr")
    val structureType = StructureType.withNameInsensitive(strTypeStr)

    data.value = VarDataStructure.apply(data.value.varIdent, DataStructure(data.value.varIdent.value, structureType), data.value.parentPathVar)
    SemanticUI.initElements()
  }

  def addDataObject(parentDS: Var[VarDataObject]): Unit = {
    val parentContent = parentDS.value.childrenVars
    info(s"DataUIStore: addDataObject $parentContent".take(100))
    parentContent.value += Var(VarDataValue(parentDS.value.path))
    SemanticUI.initElements()
  }

  def deleteDataObject(dataIdent: String, contentVar: Vars[Var[_ <: VarDataStructure]]): Unit = {
    info(s"DataUIStore: deleteDataObject $dataIdent from Parent")
    val deleteCandidates = contentVar.value.filter(_.value.varIdent.value == dataIdent)
    contentVar.value -= deleteCandidates.head
    SemanticUI.initElements()
  }

  def dataValue(searchIdent: String): Option[Var[VarDataValue]] =
    dataValues()
      .find(_.value.varIdent.value == searchIdent)

  def dataPaths: Seq[String] =
    dataValues()
      .map(_.value.pathString)

  def dataValues(): Seq[Var[VarDataValue]] =
    uiState.data.value
      .findValues()

  case class UIState(
                      varIdent: Var[String],
                      data: Var[VarDataObject],
                      varsIdents: Vars[String],
                    )

  object UIState {

    def apply(): UIState = {
      val varIdent = Var(DataStructure.defaultKey)
      UIState(
        varIdent,
        Var(VarDataObject(varIdent)),
        Vars.empty,
      )
    }
  }

  /*
    object VarDataContainer {
      def apply(varIdent: Var[String], data: DataContainer): VarDataContainer =
        VarDataContainer(varIdent, Var(VarDataObject.create(varIdent, data.structure.value, Var(Nil))))

      def apply(varIdent: Var[String], form: FormContainer): VarDataContainer =
        VarDataContainer(varIdent,
          Var(VarDataObject.create(varIdent,
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

    def varIdent: Var[String]

    def cardinalityVar: Var[Cardinality]

    def parentPathVar: Var[Seq[String]]

    def path: Seq[String] = parentPathVar.value :+ varIdent.value

    def pathString: String =
      if (path.size > 2)
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
        .flatMap { value =>
          value.value match {
            case _: VarDataValue =>
              Seq(value.asInstanceOf[Var[VarDataValue]])
            case obj: VarDataObject =>
              obj.findValues()
          }
        }
  }

  object VarDataStructure {
    def apply(varIdent: Var[String],
              dataStructure: DataStructure,
              parentPathVar: Var[Seq[String]],
             ): VarDataStructure = dataStructure match {
      case DataObject(_, cardinality, children) => VarDataObject.create(varIdent, cardinality, children, parentPathVar)
      case DataValue(_, structureType, cardinality) => VarDataValue(varIdent, structureType, Var(cardinality), parentPathVar)
    }
  }

  case class VarDataObject(varIdent: Var[String] = Var(DataStructure.defaultKey),
                           cardinalityVar: Var[Cardinality] = Var(ONE),
                           childrenVars: Vars[Var[_ <: VarDataStructure]] = Vars.empty,
                           parentPathVar: Var[Seq[String]] = Var(Nil))
    extends VarDataStructure {
    val structureType: StructureType = StructureType.OBJECT

    def toData: DataObject = DataObject(varIdent.value, cardinalityVar.value, childrenVars.value.map(_.value.toData))

    override def adjustPath(): Unit = {
      childrenVars.value
        .foreach(_.value.adjustPath(path :+ varIdent.value))
    }

    override def contents: Seq[Var[_ <: VarDataStructure]] = childrenVars.value
  }

  object VarDataObject {
    def create(varIdent: Var[String],
               cardinality: Cardinality,
               children: Seq[DataStructure],
               parentPathVar: Var[Seq[String]]): VarDataObject = {
      val seq = children.map((entry: DataStructure) => Var(VarDataStructure(Var(entry.ident), entry, Var(parentPathVar.value :+ varIdent.value))))
      VarDataObject(varIdent, Var(cardinality), Vars(seq: _*), parentPathVar)
    }

    def apply(varIdent: Var[String], form: FormContainer): VarDataObject =
      VarDataObject.create(varIdent,
        ONE,
        form.elems.filterNot(_.elementType.readOnly)
          .map(el => el.elementType match {
            case TEXTFIELD if el.extras.propValue(INPUT_TYPE).contains(InputType.NUMBER.key) =>
              DataValue(el.ident, NUMBER)
            case CHECKBOX =>
              DataValue(el.ident, BOOLEAN)
            case _ =>
              DataValue(el.ident, STRING)
          })
        , Var(Nil))
  }

}

case class VarDataValue(varIdent: Var[String],
                        structureType: StructureType,
                        cardinalityVar: Var[Cardinality],
                        parentPathVar: Var[Seq[String]])
  extends VarDataStructure {

  def toData: DataStructure =
    DataValue(varIdent.value, structureType)

}

object VarDataValue {
  def apply(parentPath: Seq[String]): VarDataValue =
    new VarDataValue(Var(DataStructure.defaultKey), StructureType.STRING, Var(ONE), Var(parentPath))
}
