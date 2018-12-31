package pme123.form.client.form

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

  def idents(): Binding[HTMLElement] = {
    val path = s"$apiPath/form/idents"

    httpGet(path, (ids: Seq[String]) => UIFormStore.changeIdents(ids))
  }

  def getForm(ident: String): Binding[HTMLElement] = {
    val path = s"$apiPath/form/$ident"

    httpGet(path, (form:FormContainer) => UIFormStore.changeForm(form))
  }

}
