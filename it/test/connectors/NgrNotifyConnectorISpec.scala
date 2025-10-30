package connectors

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.http.Fault
import helpers.{IntegrationSpecBase, WiremockHelper}
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status.{ACCEPTED, INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.Json
import play.api.test.Injecting
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.NgrNotifyConnector
import uk.gov.hmrc.ngrloginregisterfrontend.models.ErrorResponse

class NgrNotifyConnectorISpec extends AnyWordSpec with IntegrationSpecBase with Injecting {

  lazy val connector: NgrNotifyConnector = app.injector.instanceOf[NgrNotifyConnector]

  override def beforeEach(): Unit = {
    WireMock.reset()
  }

  "NgrNotifyConnector" when {

    "calling .isAllowedInPrivateBeta()" should {
      "return true when allowed is true in response" in {
        val credId = "test-cred-id"
        val responseJson = Json.obj("allowed" -> true)

        WiremockHelper.stubGet(s"/allowed-in-private-beta/$credId", OK, responseJson.toString())

        val result = connector.isAllowedInPrivateBeta(credId).futureValue
        result mustBe true

        WiremockHelper.verifyGet(s"/allowed-in-private-beta/$credId")
      }

      "return false when allowed is false in response" in {
        val credId = "test-cred-id"
        val responseJson = Json.obj("allowed" -> false)

        WiremockHelper.stubGet(s"/allowed-in-private-beta/$credId", OK, responseJson.toString())

        val result = connector.isAllowedInPrivateBeta(credId).futureValue
        result mustBe false

        WiremockHelper.verifyGet(s"/allowed-in-private-beta/$credId")
      }

      "return false when response is not OK" in {
        val credId = "test-cred-id"

        WiremockHelper.stubGet(s"/allowed-in-private-beta/$credId", INTERNAL_SERVER_ERROR, "error")

        val result = connector.isAllowedInPrivateBeta(credId).futureValue
        result mustBe false

        WiremockHelper.verifyGet(s"/allowed-in-private-beta/$credId")
      }
    }

    "calling .registerRatePayer()" should {
      "return ACCEPTED when registration is successful" in {
        WiremockHelper.stubPost(
          "/ratepayer",
          ACCEPTED,
          """{"status": "OK"}"""
        )

        val result = connector.registerRatePayer(sampleRatepayerRegistration).futureValue
        result match {
          case Right(HttpResponse(status, body, _)) =>
            status mustBe ACCEPTED
            body mustBe """{"status": "OK"}"""
          case _ =>
            fail("Expected Right(HttpResponse(status, body, _) for successful request")
        }

        WiremockHelper.verifyPost("/ratepayer", Some(Json.toJson(sampleRatepayerRegistration).toString()))
      }

      val clientErrorCodes = Seq(400, 401, 403, 404, 405, 409, 410, 415, 422, 429)

      clientErrorCodes.foreach { statusCode =>
        s"return $statusCode response without throwing for client error" in {
          WiremockHelper.stubPost(
            "/ratepayer",
            statusCode,
            s"""{"status": "$statusCode", "error": "Client error"}"""
          )

          val result = connector.registerRatePayer(sampleRatepayerRegistration).futureValue
          result mustBe Left(ErrorResponse(statusCode, s"""{"status": "$statusCode", "error": "Client error"}"""))

          WiremockHelper.verifyPost("/ratepayer", Some(Json.toJson(sampleRatepayerRegistration).toString()))}
      }

     "throw an exception for $statusCode server error" in {
          WiremockHelper.stubWithFault(
            "POST",
            "/ratepayer",
            Fault.CONNECTION_RESET_BY_PEER
          )

          val result = connector.registerRatePayer(sampleRatepayerRegistration).futureValue
          result mustBe Left(ErrorResponse(INTERNAL_SERVER_ERROR, "Call to ngr-notify ratepayer failed: Connection reset"))

          WiremockHelper.verifyPost("/ratepayer", Some(Json.toJson(sampleRatepayerRegistration).toString()))
        }
      }
    }
}