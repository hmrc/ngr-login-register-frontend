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

package uk.gov.hmrc.ngrloginregisterfrontend.connectors

import uk.gov.hmrc.http.client.HttpClientV2
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import play.api.libs.json.Json
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.TestData
import uk.gov.hmrc.ngrloginregisterfrontend.mocks.MockHttpV2
import uk.gov.hmrc.ngrloginregisterfrontend.models.ErrorResponse
import uk.gov.hmrc.ngrloginregisterfrontend.models.cid.{MatchingDetails, PersonDetails}

import scala.concurrent.Future

class CitizenDetailsConnectorSpec extends MockHttpV2 with TestData {

  val httpClientV2: HttpClientV2 = mock[HttpClientV2]
  val cidConnector: CitizenDetailsConnector = new CitizenDetailsConnector(mockHttpClientV2,mockConfig)


  "Calling the citizen details matching api" when {
    "a valid nino is passed in" should {
      "return a 200(OK)" in {
        val successResponse = HttpResponse(status = OK, json = cidMatchingDetailsResponseJson, headers = Map.empty)
        setupMockHttpV2Get(s"${mockConfig.citizenDetailsUrl}/citizen-details/nino/$testNino")(successResponse)
        val result: Future[Either[ErrorResponse, MatchingDetails]] = cidConnector.getMatchingResponse(Nino(testNino))
        result.futureValue mustBe Right(matchingDetailsResponse)
      }
    }
      "json is invalid" should {
        "return an error" in {
          val successResponse = HttpResponse(status = OK, json = Json.obj(), headers = Map.empty)
          setupMockHttpV2Get(s"${mockConfig.citizenDetailsUrl}/citizen-details/nino/$testNino")(successResponse)
          val result = cidConnector.getMatchingResponse(Nino(testNino))
          result.futureValue mustBe Left(ErrorResponse(BAD_REQUEST, "Json Validation Error: List((/name/current/firstName,List(JsonValidationError(List(error.path.missing),List()))))"))
        }
      }
      "a 400-499 response is returned" should {
        "return an ErrorResponse" in {
          val errorResponse = HttpResponse(status = NOT_FOUND, """No nino found on account""", headers = Map.empty)
          setupMockHttpV2Get(s"${mockConfig.citizenDetailsUrl}/citizen-details/nino/$testNino")(errorResponse)
          val result = cidConnector.getMatchingResponse(Nino(testNino))
          result.futureValue mustBe Left(ErrorResponse(NOT_FOUND,"No nino found on account"))

        }
      }
      "a 500-599 response is returned" should {
        "return an ErrorResponse" in {
          setupMockFailedHttpV2Get(s"${mockConfig.citizenDetailsUrl}/citizen-details/nino/$testNino")
          val result = cidConnector.getMatchingResponse(Nino(testNino))
          result.futureValue mustBe Left(ErrorResponse(INTERNAL_SERVER_ERROR,"Call to citizen details failed"))
        }
      }
  }

  "Calling the citizen details person details api" when {
    "a valid nino is passed in" should {
      "return a 200(OK)" in {
        val successResponse = HttpResponse(status = OK, json = cidPersonDetailsResponseJson, headers = Map.empty)
        setupMockHttpV2Get(s"${mockConfig.citizenDetailsUrl}/citizen-details/$testNino/designatory-details")(successResponse)
        val result: Future[Either[ErrorResponse, PersonDetails]] = cidConnector.getPersonDetails(Nino(testNino))
        result.futureValue mustBe Right(personDetailsResponse)
      }
      "json is invalid" should {
        "return an error" in {
          val successResponse = HttpResponse(status = OK, json = Json.obj(), headers = Map.empty)
          setupMockHttpV2Get(s"${mockConfig.citizenDetailsUrl}/citizen-details/$testNino/designatory-details")(successResponse)
          val result = cidConnector.getPersonDetails(Nino(testNino))
          result.futureValue mustBe Left(ErrorResponse(400, "Json Validation Error: List((/address,List(JsonValidationError(List(error.path.missing),List()))), (/person,List(JsonValidationError(List(error.path.missing),List()))))"))
        }
      }
      "a 400-499 response is returned" should {
        "return an ErrorResponse" in {
          val errorResponse = HttpResponse(status = NOT_FOUND, """No nino found on account""", headers = Map.empty)
          setupMockHttpV2Get(s"${mockConfig.citizenDetailsUrl}/citizen-details/$testNino/designatory-details")(errorResponse)
          val result = cidConnector.getPersonDetails(Nino(testNino))
          result.futureValue mustBe Left(ErrorResponse(NOT_FOUND,"No nino found on account"))

        }
      }
      "a 500-599 response is returned" should {
        "return an ErrorResponse" in {
          setupMockFailedHttpV2Get(s"${mockConfig.citizenDetailsUrl}/citizen-details/$testNino/designatory-details")
          val result = cidConnector.getPersonDetails(Nino(testNino))
          result.futureValue mustBe Left(ErrorResponse(INTERNAL_SERVER_ERROR,"Call to citizen details failed"))
        }
      }
    }
  }
}
