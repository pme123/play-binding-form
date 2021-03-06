package pme123.form.client.mapping

import com.thoughtworks.binding.Binding.{Var, Vars}
import pme123.form.client.data.{UIDataStore, VarDataValue}
import pme123.form.client.data.UIDataStore.VarDataObject
import pme123.form.client.form.{UIFormElem, UIFormStore, VarFormContainer}
import pme123.form.client.services.UIStore.supportedLangs
import pme123.form.client.services.{SemanticUI, UIStore}
import pme123.form.shared.ElementType.TEXTFIELD
import pme123.form.shared._
import pme123.form.shared.services.Logging

object UIMappingStore extends Logging {

  val uiState = UIState()

  def changeIdents(idents: Seq[String]): Unit = {
    info(s"MappingUIStore: changeIdents $idents")
    uiState.varsIdents.value.clear()
    uiState.varsIdents.value ++= idents
    SemanticUI.initElements()
  }

  def changeMapping(mapping: GetMappingContainer): Unit = {
    info(s"MappingUIStore: changeMapping ${mapping.ident}")
    changeMappingIdent(mapping.ident)
    uiState.mapping.value =
      VarMappingContainer(
        uiState.varIdent,
        mapping,
        UIFormStore.changeForm(mapping.form),
        UIDataStore.changeData(mapping.data),
      )
    SemanticUI.initElements()
  }

  def changeMappingIdent(ident: String): Unit = {
    info(s"MappingUIStore: changeMappingIdent $ident")
    uiState.varIdent.value = ident
    SemanticUI.initElements()
  }

  def adjustMappings(elems: Seq[Var[UIFormElem]]): Vars[Var[UIMappingEntry]] = {
    info(s"MappingUIStore: adjustMappings")
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
    println(s"DATAIDENT: $dataIdent")
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
                      varIdent: Var[String],
                      mapping: Var[VarMappingContainer],
                      varsIdents: Vars[String]
                    )

  object UIState {
    def apply(varIdent: Var[String] = Var(MappingContainer.defaultIdent)): UIState = {
      UIState(
        varIdent,
        Var(VarMappingContainer(
          varIdent,
          UIFormStore.uiState.form,
          UIDataStore.uiState.data,
        )),
        Vars.empty
      )
    }
  }

  case class VarMappingContainer(varIdent: Var[String],
                                 form: Var[VarFormContainer],
                                 data: Var[VarDataObject],
                                 mappings: Vars[Var[UIMappingEntry]] = Vars.empty) {
    def toMapping: MappingContainer = {
      MappingContainer(varIdent.value,
        form.value.varIdent.value,
        data.value.varIdent.value,
        mappings.value.map(_.value.toMapping),
      )
    }

    def autoMap(form: VarFormContainer,
                data: VarDataObject): Unit = {

      val ident = s"${form.varIdent.value}-mapping"
      uiState.varIdent.value = ident
      uiState.mapping.value.form.value = form
      uiState.mapping.value.data.value = data

      uiState.mapping.value.mappings.value.clear()

      uiState.mapping.value.mappings.value ++=
      form.elems.value
          .filterNot(_.value.elementTypeVar.value.readOnly)
          .map { ue: Var[UIFormElem] =>
            val dataValue =
              data.findValues()
                .map(_.value)
                .find(_.varIdent.value == ue.value.varIdent.value)
            UIMappingEntry(ue, Var(dataValue))
          }.map(Var(_))
      SemanticUI.initElements()
    }

  }

  object VarMappingContainer {
    def apply(varIdent: Var[String],
              mapping: GetMappingContainer,
              varForm: Var[VarFormContainer],
              varData: Var[VarDataObject]): VarMappingContainer =
      VarMappingContainer(varIdent,
        varForm,
        varData,
        Vars(mapping.mappings.flatMap(UIMappingEntry.apply(_).toSeq).map(Var(_)): _*))
  }

  case class UIMappingEntry(uiFormElem: Var[UIFormElem] = Var(UIFormElem(BaseElement(TEXTFIELD))),
                            varDataValue: Var[Option[VarDataValue]] = Var(None)) {
    def toMapping: MappingEntry = {
      MappingEntry(
        uiFormElem.value.varIdent.value,
        varDataValue.value.map(_.varIdent.value),
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
