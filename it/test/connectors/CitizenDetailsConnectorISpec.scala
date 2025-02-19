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
import helpers.{IntegrationSpecBase, IntegrationTestData, WiremockHelper}
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import play.api.test.Injecting
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.CitizenDetailsConnector
import uk.gov.hmrc.ngrloginregisterfrontend.models.cid.MatchingDetails
import uk.gov.hmrc.ngrloginregisterfrontend.models.{ErrorResponse, SaUtr}

class CitizenDetailsConnectorISpec extends AnyWordSpec with IntegrationSpecBase with Injecting with IntegrationTestData{

  lazy val connector: CitizenDetailsConnector = app.injector.instanceOf[CitizenDetailsConnector]

  override def beforeEach(): Unit = {
    WireMock.reset()
  }

  "CitizenDetailsConnector" when {
    "calling .getMatchingResponse()" when {
      "sending a request" should {
        "return a successful response" in {
          WiremockHelper.stubGet(s"/citizen-details/nino/${nino.value}",OK, cidMatchingDetailsResponseJson)
          val result = connector.getMatchingResponse(nino).futureValue
          result mustBe Right(MatchingDetails("Jim","Ferguson",Some(SaUtr("1097133333"))))
          WiremockHelper.verifyGet(s"/citizen-details/nino/${nino.value}")
        }
        "return an error when the request fails" in {
          WiremockHelper.stubGet(s"/citizen-details/nino/${nino.value}", INTERNAL_SERVER_ERROR, "{}")

          val result = connector.getMatchingResponse(nino).futureValue

          result mustBe Left(ErrorResponse(500, "{}"))
          WiremockHelper.verifyGet(s"/citizen-details/nino/${nino.value}")
        }

      }
    }
    "calling .getPersonDetails()" when {
      "sending a request" should {
        "return a successful response" in {
          WiremockHelper.stubGet(s"/citizen-details/${nino.value}/designatory-details",OK, cidPersonDetailsResponseJson)
          val result = connector.getPersonDetails(nino).futureValue
          result mustBe Right(personDetailsResponse)
          WiremockHelper.verifyGet(s"/citizen-details/${nino.value}/designatory-details")
        }
        "return an error when the request fails" in {
          WiremockHelper.stubGet(s"/citizen-details/${nino.value}/designatory-details", INTERNAL_SERVER_ERROR, "{}")

          val result = connector.getPersonDetails(nino).futureValue

          result mustBe Left(ErrorResponse(500, "{}"))
          WiremockHelper.verifyGet(s"/citizen-details/${nino.value}/designatory-details")
        }

      }
    }
  }

}
