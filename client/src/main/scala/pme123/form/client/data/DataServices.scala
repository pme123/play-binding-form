package pme123.form.client.data

import com.thoughtworks.binding.Binding
import org.scalajs.dom.FormData
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.raw.HTMLElement
import pme123.form.client.services.HttpServices
import pme123.form.shared.DataContainer

/**
  * Created by pascal.mengelt on 16.07.2017.
  */
object DataServices
  extends HttpServices {

  def persistData(dataContainer: DataContainer): Binding[HTMLElement] = {
    val path = s"$apiPath/data"

    httpPost(path, dataContainer, (_: DataContainer) => () // nothing to do
    )
  }

  def idents(): Binding[HTMLElement] = {
    val path = s"$apiPath/data/idents"

    httpGet(path, (ids: Seq[String]) => UIDataStore.changeIdents(ids))
  }

  def getData(ident: String): Binding[HTMLElement] = {
    val path = s"$apiPath/data/$ident"

    httpGet(path, (data: DataContainer) => UIDataStore.changeData(data))
  }

  def submitForm(form: FormData): Binding[HTMLElement] = {
    val path = s"$apiPath/data/import"

    callService(path, Ajax.post(path, form),
      (dc: DataContainer) => UIDataStore.changeData(dc))
  }


}
