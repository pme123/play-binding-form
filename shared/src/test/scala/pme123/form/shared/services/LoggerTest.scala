package pme123.form.shared.services

import pme123.form.shared.services.LogLevel._

import scala.util.Try

/**
  * Created by pascal.mengelt on 05.03.2015.
  *
  */
class LoggerTest extends UnitTest {
  val logMsgPrefix = "this is the official test log message"
  val logParam1 = "testArg1"
  val logParam2 = "testArg2"
  val logMsg: String = s"$logMsgPrefix $logParam1 $logParam2"

  // Logger.debug
  {
    val msg = debug(logMsg)
    matchLogMsg("debug", msg)
  }
  // Logger.info
  {
    val msg = info(logMsg)
    matchLogMsg("info", msg)
  }
  // Logger.warn
  {
    val msg = warn(logMsg)
    matchLogMsg("warn", msg)
  }
  // Logger.error
  {
    val msg = error(logMsg)
    matchLogMsg("error", msg)
  }
  // Logger.error(Throwable)
  {
    val msg = error(new Exception(logMsgPrefix))
    "match the log message" in {
      msg should be(logMsgPrefix)
    }
  }
  // Logger.error(Throwable, msg)
  {
    val simpleMsg = "simple log"
    val msg = error(simpleMsg, new Exception(logMsgPrefix))

    "match the error message" in {
      msg should be(simpleMsg)
    }
  }
  // Logger.error(Throwable, msg, params)
  {
    val msg = error(logMsg, new Exception(logMsgPrefix))

    matchLogMsg("error with params", msg)
  }

  "AllErrorMsgs for a AdaptersException" should {
    "be formatted in an expected format" in {
      exceptionToString(new Exception("configException")) should startWith(
        "configException ["
      )
    }

    "be formatted also correct for Exceptions with causes" in {
      val msg = exceptionToString(new Exception("configException", new Exception("firstCause", new Exception("secondCause"))))
        .split("\\n")
      msg.head should startWith("configException [")
      msg.tail.head should startWith(" - Cause: firstCause [")
      msg.last should startWith(" - Cause: secondCause [")
    }
  }
  // LogEntry.asString
  {
    val msg = Logging.info(logMsg)
    "A LogEntry as string" should {
      "be a nice readable text preceeded by the LogLevel" in {
        msg should be(s"$logMsgPrefix $logParam1 $logParam2")
      }

      "have no problems with UTF-8 encodings" in {
        Logging.info("12%\u00e4Tafelgetr\u00e4nke\nw") should be("12%\u00e4Tafelgetr\u00e4nke\nw")
      }
    }
  }
  // LogLevel.fromLevel
  {
    "A LogLevel DEBUG" should {
      "be created from the String Debug" in {
        LogLevel.withNameInsensitive("Debug") should be(DEBUG)
      }
    }
    "A LogLevel INFO" should {
      "be created from the String Info" in {
        LogLevel.withNameInsensitive("Info") should be(INFO)
      }
    }
    "A LogLevel WARN" should {
      "be created from the String Warn" in {
        LogLevel.withNameInsensitive("Warn") should be(WARN)
      }
    }
    "A LogLevel ERROR" should {
      "be created from the String Error" in {
        LogLevel.withNameInsensitive("Error") should be(ERROR)
      }
    }

    "An unsupported Level" should {
      "return a Failure with an IllegalArgumentException." in {
        val badLevel = "autsch"
        val fromLevel = Try(LogLevel.withNameInsensitive(badLevel))
        assert(fromLevel.isFailure)
        fromLevel.failed.get.getMessage should be("autsch is not a member of Enum (DEBUG, INFO, WARN, ERROR)")
      }
    }
  }
  // LogLevel >= logLevel
  {
    val correct = true
    val incorrect = false
    "A LogLevel DEBUG" should {
      "be >= than DEBUG" in {
        DEBUG >= DEBUG should be(correct)
      }
      "be < than INFO" in {
        DEBUG >= INFO should be(incorrect)
      }
      "be < than WARN" in {
        DEBUG >= WARN should be(incorrect)
      }
      "be < than ERROR" in {
        DEBUG >= ERROR should be(incorrect)
      }
    }
    "A LogLevel INFO" should {
      "be >= than DEBUG" in {
        INFO >= DEBUG should be(correct)
      }
    }
    "A LogLevel INFO" should {
      "be >= than INFO" in {
        INFO >= INFO shouldBe correct
      }
      "be < than WARN" in {
        INFO >= WARN should be(incorrect)
      }
      "be < than ERROR" in {
        INFO >= ERROR should be(incorrect)
      }
    }
    "A LogLevel WARN" should {
      "be >= than INFO" in {
        WARN >= INFO should be(correct)
      }

      "be >= than WARN" in {
        WARN >= WARN should be(correct)
      }
      "be < than ERROR" in {
        WARN >= ERROR should be(incorrect)
      }
    }
    "A LogLevel ERROR" should {
      "be >= than INFO" in {
        ERROR >= INFO should be(correct)
      }

      "be >= than WARN" in {
        ERROR >= WARN should be(correct)
      }
      "be >= than ERROR" in {
        ERROR >= ERROR should be(correct)
      }
    }
  }

  private def matchLogMsg(postfix: String, msg: String): Unit =
    s"match the log message $postfix" in {
      msg should be(logMsg)
    }

}
