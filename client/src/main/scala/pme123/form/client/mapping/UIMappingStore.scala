package pme123.form.client.mapping

import com.thoughtworks.binding.Binding.{Var, Vars}
import pme123.form.client.data.UIDataStore
import pme123.form.client.data.UIDataStore.{VarDataContainer, VarDataValue}
import pme123.form.client.form.{UIFormElem, UIFormStore, VarFormContainer}
import pme123.form.client.services.{SemanticUI, UIStore}
import pme123.form.client.services.UIStore.supportedLangs
import pme123.form.shared.ElementType.TEXTFIELD
import pme123.form.shared._
import pme123.form.shared.services.Logging

object UIMappingStore extends Logging {

  val uiState = UIState()

  def changeIdents(idents: Seq[String]): Unit = {
    info(s"MappingUIStore: changeIdents $idents")
    uiState.idents.value.clear()
    uiState.idents.value ++= idents
    SemanticUI.initElements()
  }

  def changeMapping(mapping: GetMappingContainer): Unit = {
    info(s"MappingUIStore: changeMapping ${mapping.ident}")
    changeMappingIdent(mapping.ident)
    uiState.mapping.value =
      VarMappingContainer(
        mapping,
        UIFormStore.changeForm(mapping.form),
        UIDataStore.changeData(mapping.data),
      )
    SemanticUI.initElements()
  }

  def changeMappingIdent(ident: String): Unit = {
    info(s"MappingUIStore: changeMappingIdent $ident")
    uiState.mapping.value.identVar.value = ident
    SemanticUI.initElements()
  }

  def adjustMappings(elems: Seq[Var[UIFormElem]]): Vars[Var[UIMappingEntry]] = {
    val mappings = uiState.mapping.value.mappings.value
    // remove unused elements
    uiState.mapping.value.mappings.value
      .filterNot(m => elems.exists(e => m.value.uiFormElem.value == e.value))
      .foreach(e => uiState.mapping.value.mappings.value -= e)
    // add usedElements
    elems
      .filterNot(_.value.elementTypeVar.value.readOnly)
      .foreach { uiE =>
        mappings.find(_.value.uiFormElem.value == uiE.value)
          .getOrElse(
            uiState.mapping.value.mappings.value += Var(UIMappingEntry(uiE, Var(None)))
          )
      }
    uiState.mapping.value.mappings

  }

  def changeData(uiMappingVar: Var[UIMappingEntry])(dataIdent: String): Unit = {
    if (dataIdent.isEmpty)
      uiMappingVar.value.varDataValue.value = None
    else
      UIDataStore.dataValue(dataIdent).foreach((dv: Var[VarDataValue]) =>
        uiMappingVar.value.varDataValue.value = Some(dv.value)
      )
    SemanticUI.initElements()
    MappingUtils.initFields(
      UIStore.activeLanguage.value,
      UIFormStore.uiState.formElements.value,
      uiState.mapping.value.mappings.value)

  }


  case class UIState(
                      mapping: Var[VarMappingContainer],
                      idents: Vars[String]
                    )

  object UIState {
    def apply(): UIState = {
      UIState(
        Var(VarMappingContainer(
          Var(MappingContainer.defaultIdent),
          UIFormStore.uiState.form,
          UIDataStore.uiState.data,
        )),
        Vars.empty
      )
    }
  }

  case class VarMappingContainer(identVar: Var[String],
                                 form: Var[VarFormContainer],
                                 data: Var[VarDataContainer],
                                 mappings: Vars[Var[UIMappingEntry]] = Vars.empty) {
    lazy val toMapping: MappingContainer = {
      MappingContainer(identVar.value,
        form.value.identVar.value,
        data.value.identVar.value,
        mappings.value.map(_.value.toMapping),
      )
    }

  }

  object VarMappingContainer {
    def apply(mapping: GetMappingContainer,
              varForm: Var[VarFormContainer],
              varData: Var[VarDataContainer]): VarMappingContainer =
      VarMappingContainer(Var(mapping.ident),
        varForm,
        varData,
        Vars(mapping.mappings.flatMap(UIMappingEntry.apply(_).toSeq).map(Var(_)): _*))
  }

  case class UIMappingEntry(uiFormElem: Var[UIFormElem] = Var(UIFormElem(BaseElement(TEXTFIELD))),
                            varDataValue: Var[Option[VarDataValue]] = Var(None)) {
    lazy val toMapping: MappingEntry = {
      MappingEntry(
        uiFormElem.value.identVar.value,
        varDataValue.value.map(_.ident),
      )
    }
  }

  object UIMappingEntry {

    def apply(mapping: MappingEntry): Option[UIMappingEntry] = {
      UIFormStore.formElement(mapping.formIdent)
        .map { fe =>
          UIMappingEntry(fe,
            Var(mapping.dataIdent.flatMap(di => UIDataStore.dataValue(di))
              .map(_.value)
            )
          )
        }
    }
  }

}
