package hydra.spark.operations.transform

import com.typesafe.config.ConfigFactory
import hydra.spark.api.{Invalid, Valid}
import hydra.spark.testutils.{ListOperation, SharedSparkContext, StaticJsonSource}
import hydra.spark.transform.SparkBatchTransformation
import org.scalatest.{BeforeAndAfterEach, FunSpecLike, Matchers}
import spray.json._

class ChangeCaseSpec extends Matchers with FunSpecLike with BeforeAndAfterEach with SharedSparkContext {

  val config = ConfigFactory.parseString(
    """
      |spark.master = "local[4]"
      |spark.ui.enabled = false
      |spark.driver.allowMultipleContexts = false
    """.stripMargin
  )

  /**
    * ListOperation needs to be reset before each test suite because it is a global singleton
    */
  override def beforeAll(): Unit = {
    super.beforeAll()
    ListOperation.reset()
  }

  describe("When changing case of column names") {
    it("changes case from lower underscore to lower camel") {
      val cc = ChangeCase("LOWER_UNDERSCORE", "LOWER_CAMEL")
      val json: JsValue = StaticJsonSource.msgs(0).parseJson

      SparkBatchTransformation("underscore_camel_test", StaticJsonSource, Seq(cc, ListOperation), config).run()
      val result = ListOperation.l.map(_.parseJson)
      val r2 = result.head.asJsObject.getFields("msgNo") shouldEqual json.asJsObject.getFields("msg_no")
    }
    it("validates input") {
      val cc = ChangeCase("LOWER_UNDERSCORE", "LOWER_CAMEL")
      cc.validate shouldBe Valid
    }
    it("should recognize invalid CaseFormat") {
      val cfg = ConfigFactory.parseString("from = LOWER_UNDERSCORE, to = INVALID").resolve
      val validation = ChangeCase(cfg).validate.asInstanceOf[Invalid]
      println(validation)
      validation.errors shouldBe Invalid.unapply(validation).get
    }
    it("should recognize invalid input") {
      val cfg = ConfigFactory.parseString("from = LOWER_UNDERSCORE").resolve
      val validation = ChangeCase(cfg).validate.asInstanceOf[Invalid]
      println(validation)
      validation.errors shouldBe Invalid.unapply(validation).get
    }
  }


}
