package pme123.form.client.form

import com.thoughtworks.binding.Binding.{Var, Vars}
import pme123.form.shared._

case class UIValidations(rules: Vars[UIValidationRule] = Vars.empty) {

  def toValidations =
    Validations(
      rules.value.map(_.toValidationRule)
    )
}

object UIValidations {
  def apply(validations: Validations): UIValidations =
    UIValidations(Vars(
      validations.rules
        .map(UIValidationRule.apply): _*)
    )
}

case class UIValidationRule(validationType: ValidationType, enabled: Var[Boolean], params: UIValidationParams) {
  def toValidationRule =
    ValidationRule(
      validationType,
      enabled.value,
      params.toValidationParams
    )
}

object UIValidationRule {

  def apply(validationRule: ValidationRule): UIValidationRule =
    UIValidationRule(
      validationRule.validationType,
      Var(validationRule.enabled),
      UIValidationParams(validationRule.params))
}

case class UIValidationParams(stringParam: Var[Option[String]],
                            intParam1: Var[Option[Int]],
                            intParam2: Var[Option[Int]]) {

  def toValidationParams =
    ValidationParams(
      stringParam.value,
      intParam1.value,
      intParam2.value
    )
}

object UIValidationParams {

  def apply(params: ValidationParams): UIValidationParams =
    UIValidationParams(
      Var(params.stringParam),
      Var(params.intParam1),
      Var(params.intParam2))
}