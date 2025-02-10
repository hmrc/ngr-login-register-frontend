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
import helpers.{IntegrationSpecBase, WiremockHelper}
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.{JsValue, Json}
import play.api.test.Injecting
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.CitizenDetailsConnector
import uk.gov.hmrc.ngrloginregisterfrontend.models.{ErrorResponse, SaUtr}
import uk.gov.hmrc.ngrloginregisterfrontend.models.cid.{MatchingDetails, Person, PersonAddress, PersonDetails}

import java.time.LocalDate

class CitizenDetailsConnectorISpec extends AnyWordSpec with IntegrationSpecBase with Injecting {

  lazy val connector: CitizenDetailsConnector = app.injector.instanceOf[CitizenDetailsConnector]
  val personResponse: Person = Person(Some("Mr"),Some("John"),Some("Joe"),Some("Ferguson"),Some("BSC"),Some("M"), Some(LocalDate.parse("1952-04-01")),Some(Nino("TW189213B")))
  val addressResponse: PersonAddress = PersonAddress(Some("26 FARADAY DRIVE"), Some("PO BOX 45"),Some("LONDON"), None,None,Some("CT1 1RQ"),Some("GREAT BRITAIN") ,Some(LocalDate.parse("2009-08-29")),Some("Residential"))
  val personDetailsResponse: PersonDetails = PersonDetails(person = personResponse, address = addressResponse)

  override def beforeEach(): Unit = {
    WireMock.reset()
  }

  val nino : Nino = Nino("AA000003D")

  "CitizenDetailsConnector" when {
    "calling .getMatchingResponse()" when {
      "sending a request" should {
        "return a successful response" in {
          val cidMatchingDetailsResponseJson=
            """
              |{
              |  "name": {
              |    "current": {
              |      "firstName": "Jim",
              |      "lastName": "Ferguson"
              |    },
              |    "previous": []
              |  },
              |  "ids": {
              |    "sautr": "1097133333",
              |    "nino": "AA000003D"
              |  },
              |  "dateOfBirth": "23041948",
              |  "deceased": false
              |}
              |""".stripMargin

          WiremockHelper.stubGet(s"/citizen-details/nino/${nino.value}",OK, cidMatchingDetailsResponseJson)
          val result = connector.getMatchingResponse(nino).futureValue
          result mustBe Right(MatchingDetails("Jim","Ferguson",Some(SaUtr("1097133333"))))
          WiremockHelper.verifyGet(s"/citizen-details/nino/${nino.value}")
        }
        "return an error when the request fails" in {
          WiremockHelper.stubGet(s"/citizen-details/nino/${nino.value}", INTERNAL_SERVER_ERROR, "{}")

          val result = connector.getMatchingResponse(nino).futureValue

          result mustBe Left(ErrorResponse(500, "Call to citizen details failed"))
          WiremockHelper.verifyGet(s"/citizen-details/nino/${nino.value}")
        }

      }
    }
    "calling .getPersonDetails()" when {
      "sending a request" should {
        "return a successful response" in {
          val cidPersonDetailsResponseJson =
            """
              |{
              |  "etag" : "115",
              |  "person" : {
              |    "firstName" : "John",
              |    "middleName" : "Joe",
              |    "lastName" : "Ferguson",
              |    "title" : "Mr",
              |    "honours": "BSC",
              |    "sex" : "M",
              |    "dateOfBirth" : "1952-04-01",
              |    "nino" : "TW189213B",
              |    "deceased" : false
              |  },
              |  "address" : {
              |    "line1" : "26 FARADAY DRIVE",
              |    "line2" : "PO BOX 45",
              |    "line3" : "LONDON",
              |    "postcode" : "CT1 1RQ",
              |    "startDate": "2009-08-29",
              |    "country" : "GREAT BRITAIN",
              |    "type" : "Residential"
              |  }
              |}
              |""".stripMargin

          WiremockHelper.stubGet(s"/citizen-details/${nino.value}/designatory-details",OK, cidPersonDetailsResponseJson)
          val result = connector.getPersonDetails(nino).futureValue
          result mustBe Right(personDetailsResponse)
          WiremockHelper.verifyGet(s"/citizen-details/${nino.value}/designatory-details")
        }
        "return an error when the request fails" in {
          WiremockHelper.stubGet(s"/citizen-details/${nino.value}/designatory-details", INTERNAL_SERVER_ERROR, "{}")

          val result = connector.getPersonDetails(nino).futureValue

          result mustBe Left(ErrorResponse(500, "Call to citizen details failed"))
          WiremockHelper.verifyGet(s"/citizen-details/${nino.value}/designatory-details")
        }

      }
    }
  }

}
