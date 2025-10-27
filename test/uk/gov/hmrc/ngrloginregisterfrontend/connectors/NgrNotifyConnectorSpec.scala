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

import play.api.http.Status.{ACCEPTED, BAD_REQUEST, INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.ngrloginregisterfrontend.mocks.MockHttpV2

import scala.concurrent.Future

class NgrNotifyConnectorSpec extends MockHttpV2 {

  val connector = new NgrNotifyConnector(mockHttpClientV2, mockConfig)

  "Calling the private beta access endpoint" when {

    "a valid and 'allowed'=true credId" should {
      "return true" in {
        val successResponse = HttpResponse(status = OK, json = Json.obj("allowed" -> true), headers = Map.empty)
        setupMockHttpV2Get(s"${mockConfig.ngrNotify}/allowed-in-private-beta/${credId.value}")(successResponse)

        val result: Future[Boolean] = connector.isAllowedInPrivateBeta(credId.value)
        result.futureValue mustBe true
      }
    }

    "a valid and 'allowed'=false credId " should {
      "return false" in {
        val successResponse = HttpResponse(status = OK, json = Json.obj("allowed" -> false), headers = Map.empty)
        setupMockHttpV2Get(s"${mockConfig.ngrNotify}/allowed-in-private-beta/${credId.value}")(successResponse)

        val result: Future[Boolean] = connector.isAllowedInPrivateBeta(credId.value)
        result.futureValue mustBe false
      }
    }

    "json missing 'allowed' filed" should {
      "return false" in {
        val successResponse = HttpResponse(status = OK, json = Json.obj(), headers = Map.empty)
        setupMockHttpV2Get(s"${mockConfig.ngrNotify}/allowed-in-private-beta/${credId.value}")(successResponse)

        val result: Future[Boolean] = connector.isAllowedInPrivateBeta(credId.value)
        result.futureValue mustBe false
      }
    }

    "a 400-499 response is returned" should {
      "return false" in {
        val errorResponse = HttpResponse(status = NOT_FOUND, """CredId not found""", headers = Map.empty)
        setupMockHttpV2Get(s"${mockConfig.ngrNotify}/allowed-in-private-beta/${credId.value}")(errorResponse)

        val result: Future[Boolean] = connector.isAllowedInPrivateBeta(credId.value)
        result.futureValue mustBe false
      }
    }

    "a 500-599 response is returned" should {
      "return false" in {
        val errorResponse = HttpResponse(status = INTERNAL_SERVER_ERROR, body = "Server error", headers = Map.empty)
        setupMockHttpV2Get(s"${mockConfig.ngrNotify}/allowed-in-private-beta/${credId.value}")(errorResponse)

        val result: Future[Boolean] = connector.isAllowedInPrivateBeta(credId.value)
        result.futureValue mustBe false
      }
    }
  }

  "Calling the ratepayer register endpoint" when {

    "a valid ratepayer" should {
      "return a successful response" in {
        val successResponse = HttpResponse(status = ACCEPTED, json = Json.obj("status" -> "OK"), headers = Map.empty)
        setupMockHttpV2Post(s"${mockConfig.ngrNotify}/ratepayer")(successResponse)

        val result: Future[HttpResponse] = connector.registerRatePayer(testRegistrationModel)
        result.futureValue mustBe successResponse
      }
    }

    "a ratepayer with missing data" should {
      "return a bad request response" in {
        val badRequestResponse = HttpResponse(
          status = BAD_REQUEST,
          json = Json.obj("status" -> "BAD_REQUEST", "error" -> "Missing required field: email"),
          headers = Map.empty
        )
        setupMockHttpV2Post(s"${mockConfig.ngrNotify}/ratepayer")(badRequestResponse)

        val result: Future[HttpResponse] = connector.registerRatePayer(testRegistrationModel)
        result.futureValue mustBe badRequestResponse
      }
    }

    "an unexpected response status" should {
      "throw an exception with status and body" in {
        val unexpectedResponse = HttpResponse(
          status = INTERNAL_SERVER_ERROR,
          body = "Something went wrong",
          headers = Map.empty
        )

        setupMockHttpV2Post(s"${mockConfig.ngrNotify}/ratepayer")(unexpectedResponse)
        val result = connector.registerRatePayer(testRegistrationModel)
        val thrown = intercept[Exception] {
          result.futureValue
        }
        thrown.getMessage must include("500: Something went wrong")
      }
    }
  }
}


