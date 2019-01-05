package pme123.form.client.form

import com.thoughtworks.binding.Binding.{Var, Vars}
import pme123.form.shared.ExtraPropValue.ExtraValue
import pme123.form.shared.{ExtraProp, ExtraPropValue, ExtraProperties}

case class UIExtraProperties(propValues: Vars[UIExtraPropValue] = Vars.empty) {

  def valueFor(extraProp: ExtraProp): Var[ExtraValue] =
    propValues.value.find(_.extraProp == extraProp)
      .map(_.valueVar)
      .getOrElse {
        val newPV = UIExtraPropValue(extraProp, Var(extraProp.defaultValue))
        propValues.value += newPV
        newPV.valueVar
      }

  def toExtraProperties =
    ExtraProperties(
      propValues.value.map(_.toExtraPropValue)
    )
}

object UIExtraProperties {
  def apply(extraProperties: ExtraProperties): UIExtraProperties =
    UIExtraProperties(Vars(
      extraProperties.propValues
        .map(UIExtraPropValue.apply): _*)
    )
}

case class UIExtraPropValue(extraProp: ExtraProp, valueVar: Var[ExtraValue]) {
  def toExtraPropValue =
    ExtraPropValue(
      extraProp,
      valueVar.value
    )
}

object UIExtraPropValue {

  def apply(extraPropValue: ExtraPropValue): UIExtraPropValue =
    UIExtraPropValue(
      extraPropValue.extraProp,
      Var(extraPropValue.value))
}