package pme123.form.client

import com.thoughtworks.binding.Binding
import org.scalajs.dom.raw.HTMLElement
import pme123.form.client.services.HttpServices
import pme123.form.shared.FormContainer

/**
  * Created by pascal.mengelt on 16.07.2017.
  */
object FormServices
  extends HttpServices {

  def persistForm(formContainer: FormContainer): Binding[HTMLElement] = {
    val path = s"$apiPath/form"

    httpPost(path, formContainer, (_: FormContainer) => ()// nothing to do
    )
  }

  def formIds(): Binding[HTMLElement] = {
    val path = s"$apiPath/formIds"

    httpGet(path, (ids: Seq[String]) => UIFormStore.changeFormIds(ids))
  }

  def getForm(formId: String): Binding[HTMLElement] = {
    val path = s"$apiPath/form/$formId"

    httpGet(path, (form:FormContainer) => UIFormStore.changeForm(form))
  }

}
