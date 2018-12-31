package pme123.form.client.mapping

import com.thoughtworks.binding.Binding
import org.scalajs.dom.raw.HTMLElement
import pme123.form.client.services.HttpServices
import pme123.form.shared.{GetMappingContainer, MappingContainer}

/**
  * Created by pascal.mengelt on 16.07.2017.
  */
object MappingServices
  extends HttpServices {

  def persistMapping(mappingContainer: MappingContainer): Binding[HTMLElement] = {
    val path = s"$apiPath/mapping"

    httpPost(path, mappingContainer, (_: MappingContainer) => () // nothing to do
    )
  }

  def idents(): Binding[HTMLElement] = {
    val path = s"$apiPath/mapping/idents"

    httpGet(path, (ids: Seq[String]) => UIMappingStore.changeIdents(ids))
  }

  def getMapping(ident: String): Binding[HTMLElement] = {
    val path = s"$apiPath/mapping/$ident"

    httpGet(path, (mapping: GetMappingContainer) => UIMappingStore.changeMapping(mapping))
  }


}
