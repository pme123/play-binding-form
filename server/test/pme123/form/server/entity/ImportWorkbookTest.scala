package pme123.form.server.entity

import java.io.InputStream

import org.apache.poi.ss.usermodel.WorkbookFactory
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
  "The FormStore import" should {
    "be imported and contain 5 sheets" in {
      assert(wb.getNumberOfSheets === 5)
    }
    "contain a forms sheet" in {
      assert(wb.getSheet(sheetForms) != null)
    }
    "contain a data sheet" in {
      assert(wb.getSheet(sheetData) != null)
    }

    "contain a mapping sheet" in {
      assert(wb.getSheet(sheetMappings) != null)
    }

    "contain a mock sheet" in {
      assert(wb.getSheet(sheetMocks) != null)
    }
  }

  "The forms sheet" should {
    "have 2 forms" in {
      workbook.forms.get.length should be(2)
    }
  }
  "The data sheet" should {
    "have 1 data" in {
      workbook.data.get.length should be(1)
    }
  }

  "The first Form" should {
    "be" in {
      val form = workbook.forms.get.head.get
      form.ident should be("address")
      form.elems.head.ident should be("TITLE-60664")
    }
  }

  "The first Data" should {
    "be" in {
      val data = workbook.data.get.head.get
      data.ident should be("address-data")
      data.children.size should be(4)
      data.child("street").get should be(DataValue("street", STRING))
    }
  }

  "The first Mapping" should {
    "be" in {
      val mapping = workbook.mappings.get.head.get
      mapping.ident should be("address-mapping")
      mapping.mappings.size should be(4)
      mapping.formIdent should be("address")
      mapping.dataIdent should be("address-data")
    }
  }

  "The first Mock" should {
    "be" in {
      val mock = workbook.mocks.get.head.get
      mock.ident should be("swapi")
      mock.mocks.size should be(3)

    }
  }

}
