package pme123.form.client.form

import com.thoughtworks.binding.Binding.Var
import pme123.form.shared.{ElementEntries, ElementEntry}

case class UIElementEntries(entries: Seq[UIElementEntry] = Nil) {
  def toElementEntries =
    ElementEntries(
      entries.map(_.toElementEntry)
    )
}

object UIElementEntries {
  def apply(elementEntries: ElementEntries): UIElementEntries =
    UIElementEntries(
      elementEntries.entries
        .map(UIElementEntry.apply)
    )
}

case class UIElementEntry(key: Var[String], label: UIElementText, ident: String = ElementEntry.ident) {
  def toElementEntry =
    ElementEntry(
      key.value,
      label.toText,
      ident
    )
}

object UIElementEntry {
  def apply(elementEntry: ElementEntry): UIElementEntry =
    UIElementEntry(
      Var(elementEntry.key),
      UIElementText(elementEntry.label),
      elementEntry.ident)
}