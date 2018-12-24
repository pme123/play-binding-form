package pme123.form.client

import com.thoughtworks.binding.Binding
import org.scalajs.dom.raw.HTMLElement
import pme123.form.client.services.HttpServices
import pme123.form.shared.{DataStructure, FormContainer}

/**
  * Created by pascal.mengelt on 16.07.2017.
  */
object DataServices
  extends HttpServices {

  def persistData(dataStructure: DataStructure): Binding[HTMLElement] = {
    val path = s"$apiPath/data"

    httpPost(path, dataStructure, (_: FormContainer) => () // nothing to do
    )
  }

}
