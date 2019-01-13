package pme123.form.client.data

import com.thoughtworks.binding.Binding
import org.scalajs.dom.FormData
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.raw.HTMLElement
import pme123.form.client.services.HttpServices
import pme123.form.shared.DataObject

/**
  * Created by pascal.mengelt on 16.07.2017.
  */
object DataServices
  extends HttpServices {

  def persistData(dataContainer: DataObject): Binding[HTMLElement] = {
    val path = s"$apiPath/data"

    httpPost(path, dataContainer, (_: DataObject) => () // nothing to do
    )
  }

  def idents(): Binding[HTMLElement] = {
    val path = s"$apiPath/data/idents"

    httpGet(path, (ids: Seq[String]) => UIDataStore.changeIdents(ids))
  }

  def getData(ident: String): Binding[HTMLElement] = {
    val path = s"$apiPath/data/$ident"

    httpGet(path, (data: DataObject) => UIDataStore.changeData(data))
  }

  def submitForm(form: FormData): Binding[HTMLElement] = {
    val path = s"$apiPath/data/import"

    callService(path, Ajax.post(path, form),
      (dc: DataObject) => UIDataStore.changeData(dc))
  }


}
