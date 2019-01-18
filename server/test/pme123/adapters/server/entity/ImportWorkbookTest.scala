package pme123.adapters.server.entity

import java.io.InputStream

import org.apache.poi.ss.usermodel.WorkbookFactory
import pme123.form.server.entity.ImportWorkbook
import pme123.form.shared.DataValue
import pme123.form.shared.StructureType.STRING


class ImportWorkbookTest
  extends UnitTest {

  import ImportWorkbook._

  def badTestSFn: InputStream = getClass.getResourceAsStream("/excel/bad_excel.xlsx")

  lazy val badTestWorkbook = ImportWorkbook(WorkbookFactory.create(badTestSFn))

  def noConfigTestSFn: InputStream = getClass.getResourceAsStream("/excel/no_config_excel.xlsx")

  lazy val noConfigTestWorkbook = ImportWorkbook(WorkbookFactory.create(noConfigTestSFn))

  private val wb = workbook.wb
  workbook.printImport()
  "The FormStore import" should "be imported and contain 4 sheets" in {
    assert(wb.getNumberOfSheets === 4)
  }

  it should "contain a forms sheet" in {
    assert(wb.getSheet(sheetForms) != null)
  }
  it should "contain a data sheet" in {
    assert(wb.getSheet(sheetData) != null)
  }

  "The forms sheet" should "have 2 forms" in {
    workbook.forms.get.length should be(2)
  }
  "The data sheet" should "have 1 data" in {
    workbook.data.get.length should be(1)
  }

  "The first Form" should "be" in {
    val form = workbook.forms.get.head.get
    form.ident should be("address")
    form.elems.head.ident should be("TITLE-60664")
  }

  "The first Data" should "be" in {
    val data = workbook.data.get.head.get
    data.ident should be("address-data")
    data.children.size should be(4)
    data.child("street").get should be(DataValue("street",STRING))
  }

}
