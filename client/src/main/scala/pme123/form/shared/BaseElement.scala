package pme123.form.shared

import enumeratum.{Enum, EnumEntry, PlayInsensitiveJsonEnum}
import pme123.form.client.services.I18n
import pme123.form.shared.ElementType._
import pme123.form.shared.ExtraProp.{CHECKBOX_TYPE, CLEARABLE, SIZE}
import pme123.form.shared.ExtraPropValue.ExtraValue
import pme123.form.shared.LayoutWide.EIGHT
import pme123.form.shared.ValidationType.{EMAIL, INTEGER, REG_EXP}
import pme123.form.shared.services.Language

import scala.collection.immutable.IndexedSeq
import scala.util.Random

case class BaseElement(ident: String,
                       elementType: ElementType,
                       texts: Option[ElementTexts],
                       extras: ExtraProperties = ExtraProperties(),
                       value: Option[String] = Some(""),
                       required: Boolean = false,
                       layoutWide: LayoutWide = EIGHT,
                       elemEntries: Option[ElementEntries] = None,
                       validations: Option[Validations] = None,
                      ) {

  val hasTexts: Boolean = texts.nonEmpty
  val hasExtras: Boolean = extras.hasExtras
  val hasEntries: Boolean = elemEntries.nonEmpty
  val hasValidations: Boolean = validations.nonEmpty

}

object BaseElement {

  def ident(elementType: ElementType) = s"${elementType.entryName}-${Random.nextInt(100000)}"

  def apply(elementType: ElementType)
           (implicit supportedLangs: Seq[Language]): BaseElement = {

    def texts = elementType match {
      case SPACER => None
      case _ => Some(ElementTexts(ident(elementType)))
    }

    BaseElement(
      ident(elementType),
      elementType,
      texts,
      extras(elementType),
      elementType.defaultValue,
      elemEntries = entries(elementType),
      validations = validations(elementType),
    )
  }

  def extras(elementType: ElementType): ExtraProperties = {
    elementType match {
      case TEXTFIELD =>
        TextElem.extras
      case DROPDOWN =>
        DropdownElem.extras
      case CHECKBOX =>
        CheckboxElem.extras
      case TITLE =>
        TitleElem.extras
      case DIVIDER =>
        DividerElem.extras
      case _ => ExtraProperties()
    }
  }

  def entries(elementType: ElementType): Option[ElementEntries] = {
    elementType match {
      case DROPDOWN =>
        Some(ElementEntries())
      case _ => None
    }
  }

  def validations(elementType: ElementType): Option[Validations] = {
    elementType match {
      case TEXTFIELD =>
        Some(Validations(Seq(ValidationRule(EMAIL), ValidationRule(INTEGER,
          params = ValidationParams(intParam1 = Some(0), intParam2 = Some(100))),
          ValidationRule(REG_EXP,
            params = ValidationParams(stringParam = Some("")))
        )))
      case _ => None
    }
  }

  def enumEntries(enums: Seq[I18nEnum])(implicit supportedLangs: Seq[Language]): Some[ElementEntries] = {
    Some(ElementEntries(
      enums.map(enum => ElementEntry(enum.entryName, ElementText.label(I18n(enum.i18nKey))))
    ))
  }
}

case class ExtraProperties(propValues: Seq[ExtraPropValue] = Nil) {

  val hasExtras: Boolean = propValues.nonEmpty

  def valueFor(extraProp: ExtraProp): Option[String] =
    propValues.find(_.extraProp == extraProp)
      .flatMap(_.value)
}


case class ExtraPropValue(extraProp: ExtraProp, value: ExtraValue)

object ExtraPropValue {
  type ExtraValue = Option[String]
}

case class ElementEntries(entries: Seq[ElementEntry] = Nil)

case class ElementEntry(key: String, label: ElementText, ident: String = ElementEntry.ident) {

}

object ElementEntry {

  def ident = s"ENTRY-${Random.nextInt(10000)}"

  def apply()(implicit supportedLangs: Seq[Language]): ElementEntry =
    ElementEntry("", ElementText.emptyLabel)
}

object TextElem {
  def extras: ExtraProperties = {

    ExtraProperties()
  }
}

object DropdownElem {
  def extras: ExtraProperties = {

    ExtraProperties(Seq(
      ExtraPropValue(
        CLEARABLE, Some("true")
      ))
    )
  }
}

object CheckboxElem {
  def extras: ExtraProperties = {

    ExtraProperties(Seq(
      ExtraPropValue(
        CHECKBOX_TYPE, CheckboxType.defaultValue
      ))
    )
  }

  sealed trait CheckboxType
    extends EnumEntry
      with I18nEnum {

    def i18nKey = s"enum.checkbox-type.${entryName.toLowerCase}"

  }


  object CheckboxType
    extends Enum[CheckboxType]
      with PlayInsensitiveJsonEnum[CheckboxType] {

    def defaultValue: Option[String] = Some(STANDARD.entryName)

    val values: IndexedSeq[CheckboxType] = findValues

    // Used for: TitleElem
    case object STANDARD extends CheckboxType

    case object SLIDER extends CheckboxType

    case object TOGGLE extends CheckboxType

  }

}


object TitleElem {

  sealed trait SizeType
    extends EnumEntry
      with I18nEnum {

    def cssClass: String = entryName.toLowerCase

    def i18nKey: String = s"enum.size-type.${entryName.toLowerCase}"

  }


  object SizeType
    extends Enum[SizeType]
      with PlayInsensitiveJsonEnum[SizeType] {

    def defaultValue: Option[String] = Some(MEDIUM.entryName)

    val values: IndexedSeq[SizeType] = findValues

    // Used for: TitleElem
    case object HUGE extends SizeType

    case object BIG extends SizeType

    case object MEDIUM extends SizeType

    case object SMALL extends SizeType

    case object TINY extends SizeType

  }

  def sizeElem = ExtraPropValue(SIZE, SizeType.defaultValue)

  def extras: ExtraProperties = {
    ExtraProperties(
      Seq(sizeElem)
    )
  }
}

object DividerElem {
  def extras: ExtraProperties = {

    ExtraProperties(
      Seq(TitleElem.sizeElem)
    )
  }
}


