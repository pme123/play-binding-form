package pme123.form.server.control

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import pme123.form.server.entity.FormConfSettings

@Singleton
class FormConfiguration @Inject()(conf: Configuration) extends FormConfSettings(conf){

}
