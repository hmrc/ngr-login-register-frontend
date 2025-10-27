package connectors

import com.github.tomakehurst.wiremock.client.WireMock
import helpers.{IntegrationSpecBase, WiremockHelper}
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status.{ACCEPTED, BAD_REQUEST, INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.Json
import play.api.test.Injecting
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.NgrNotifyConnector
import uk.gov.hmrc.ngrloginregisterfrontend.models._
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms._
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.ReferenceType.NINO
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.{AgentStatus, TRNReferenceNumber, UserType}

class NgrNotifyConnectorISpec extends AnyWordSpec with IntegrationSpecBase with Injecting {

  lazy val connector: NgrNotifyConnector = app.injector.instanceOf[NgrNotifyConnector]

  override def beforeEach(): Unit = {
    WireMock.reset()
  }

  val sampleRatepayerRegistration: RatepayerRegistration = RatepayerRegistration(
    userType = Some(UserType.Individual),
    agentStatus = Some(AgentStatus.Agent),
    name = Some(Name("Jane Doe")),
    tradingName = Some(TradingName("Jane's Bakery")),
    email = Some(Email("jane.doe@example.com")),
    nino = Some(Nino("AB123456C")),
    contactNumber = Some(PhoneNumber("07123456789")),
    secondaryNumber = Some(PhoneNumber("07987654321")),
    address = Some(Address("1 High Street", None, "London", None, Postcode("SW1A 1AA"))),
    trnReferenceNumber = Some(TRNReferenceNumber(NINO, "TRN123456")),
    isRegistered = Some(true)
  )

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
          "/register-ratepayer",
          ACCEPTED,
          "{status: OK}"
        )

        val result = connector.registerRatePayer(sampleRatepayerRegistration).futureValue
        result.status mustBe ACCEPTED
        result.body must include("OK")

        WiremockHelper.verifyPost("/register-ratepayer", Some(Json.toJson(sampleRatepayerRegistration).toString()))
      }

      "return BAD_REQUEST when registration fails due to client error" in {
        WiremockHelper.stubPost(
          "/register-ratepayer",
          BAD_REQUEST,
          "{status: INCOMPLETE, error: Invalid data}"
        )

        val result = connector.registerRatePayer(sampleRatepayerRegistration).futureValue
        result.status mustBe BAD_REQUEST
        result.body must include("Invalid data")

        WiremockHelper.verifyPost("/register-ratepayer", Some(Json.toJson(sampleRatepayerRegistration).toString()))
      }

      "throw an exception for unexpected status codes" in {
        WiremockHelper.stubPost(
          "/register-ratepayer",
          INTERNAL_SERVER_ERROR,
          "{500: Server error}"
        )

        val thrown = intercept[Exception] {
          connector.registerRatePayer(sampleRatepayerRegistration).futureValue
        }

        thrown.getMessage must include("500: Server error")
        WiremockHelper.verifyPost("/register-ratepayer", Some(Json.toJson(sampleRatepayerRegistration).toString()))
      }
    }
  }
}