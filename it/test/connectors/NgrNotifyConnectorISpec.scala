/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package connectors

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.http.Fault
import helpers.{IntegrationSpecBase, WiremockHelper}
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status.{ACCEPTED, INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.Json
import play.api.test.Injecting
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.NgrNotifyConnector

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

        WiremockHelper.stubGet(s"/ngr-notify/allowed-in-private-beta/$credId", OK, responseJson.toString())

        val result = connector.isAllowedInPrivateBeta(credId).futureValue
        result mustBe true

        WiremockHelper.verifyGet(s"/ngr-notify/allowed-in-private-beta/$credId")
      }

      "return false when allowed is false in response" in {
        val credId = "test-cred-id"
        val responseJson = Json.obj("allowed" -> false)

        WiremockHelper.stubGet(s"/ngr-notify/allowed-in-private-beta/$credId", OK, responseJson.toString())

        val result = connector.isAllowedInPrivateBeta(credId).futureValue
        result mustBe false

        WiremockHelper.verifyGet(s"/ngr-notify/allowed-in-private-beta/$credId")
      }

      "return false when response is not OK" in {
        val credId = "test-cred-id"

        WiremockHelper.stubGet(s"/ngr-notify/allowed-in-private-beta/$credId", INTERNAL_SERVER_ERROR, "error")

        val result = connector.isAllowedInPrivateBeta(credId).futureValue
        result mustBe false

        WiremockHelper.verifyGet(s"/ngr-notify/allowed-in-private-beta/$credId")
      }
    }

    "calling .registerRatePayer()" should {
      "return ACCEPTED when registration is successful" in {
        WiremockHelper.stubPost(
          "/ngr-notify/register-ratepayer",
          ACCEPTED,
          """{"status": "OK"}"""
        )

        val result = connector.registerRatePayer(sampleRatepayerRegistration).futureValue
        result mustBe true

        WiremockHelper.verifyPost("/ngr-notify/register-ratepayer", Some(Json.toJson(sampleRatepayerRegistration).toString()))
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
          result mustBe false

          WiremockHelper.verifyPost("/ngr-notify/register-ratepayer", Some(Json.toJson(sampleRatepayerRegistration).toString()))}
      }

     "throw an exception for $statusCode server error" in {
          WiremockHelper.stubWithFault(
            "POST",
            "/ratepayer",
            Fault.CONNECTION_RESET_BY_PEER
          )

          val result = connector.registerRatePayer(sampleRatepayerRegistration).futureValue
          result mustBe false

          WiremockHelper.verifyPost("/ngr-notify/register-ratepayer", Some(Json.toJson(sampleRatepayerRegistration).toString()))
        }
      }
    }
}