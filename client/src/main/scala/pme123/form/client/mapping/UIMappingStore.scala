package pme123.form.client.mapping

import com.softwaremill.quicklens._
import com.thoughtworks.binding.Binding.{Var, Vars}
import pme123.form.client.services.SemanticUI
import pme123.form.shared.services.Logging
import pme123.form.shared.{MappingContainer, MappingEntry}

object UIMappingStore extends Logging {

  val uiState = UIState()

  def changeIdents(idents: Seq[String]): Unit = {
    info(s"MappingUIStore: changeIdents $idents")
    uiState.idents.value.clear()
    uiState.idents.value ++= idents
    SemanticUI.initElements()
  }

  def changeMapping(mapping: MappingContainer): Unit = {
    info(s"MappingUIStore: changeMapping ${mapping.ident}")
    uiState.mapping.value = VarMappingContainer(mapping)
    SemanticUI.initElements()
  }

  def changeMappingIdent(ident: String): Unit = {
    info(s"MappingUIStore: changeMappingIdent $ident")
    uiState.mapping.value = uiState.mapping.value.modify(_.ident).setTo(ident)
    SemanticUI.initElements()
  }


  case class UIState(
                      mapping: Var[VarMappingContainer],
                      idents: Vars[String]
                    )

  object UIState {
    def apply(): UIState = {
      UIState(
        Var(VarMappingContainer()),
        Vars.empty
      )
    }
  }

  case class VarMappingContainer(ident: String = MappingContainer.defaultIdent,
                                 formIdent: String = "",
                                 dataIdent: String = "",
                                 mappings: Vars[Var[MappingEntry]] = Vars.empty) {
    lazy val toMapping: MappingContainer =
      MappingContainer(ident, formIdent, dataIdent, mappings.value.map(_.value))

  }

  object VarMappingContainer {
    def apply(mapping: MappingContainer): VarMappingContainer =
      VarMappingContainer(mapping.ident,
        mapping.formIdent,
        mapping.dataIdent,
        Vars(mapping.mappings.map(Var(_)): _*))
  }

}
