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

package uk.gov.hmrc.ngrloginregisterfrontend.connectors.centralauth

import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import play.api.libs.json.Json
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.TestData
import uk.gov.hmrc.ngrloginregisterfrontend.mocks.MockHttpV2
import uk.gov.hmrc.ngrloginregisterfrontend.models.ErrorResponse
import uk.gov.hmrc.ngrloginregisterfrontend.utils.NGRLogger


class CentralAuthConnectorSpec extends MockHttpV2 with TestData {

  val logger: NGRLogger = inject[NGRLogger]
  val httpClientV2: HttpClientV2 = mock[HttpClientV2]
  val centralAuthConnector = new CentralAuthConnector(mockHttpClientV2, mockConfig, logger)
  val userAgentHeader: Seq[(String, String)] = Seq("User-Agent" -> "ngr-login-register-frontend")

  "Calling the token search api" when {
    "a valid gnap token is passed in" should {
      "return a 200(OK)" in {
        val successResponse = HttpResponse(status = OK, json = tokenAttributesResponseJson, headers = Map.empty)
        setupMockHttpV2PostWithHeaderCarrier(s"${mockConfig.centralAuthServerUrl}/centralised-authorisation-server/token/search", userAgentHeader)(successResponse)
        val result = centralAuthConnector.getTokenAttributesResponse(gnapToken)
        result.futureValue mustBe Right(tokenAttributesResponse)
      }
    }
    "json is invalid" should {
      "return an error" in {
        val successResponse = HttpResponse(status = OK, json = Json.obj(), headers = Map.empty)
        setupMockHttpV2PostWithHeaderCarrier(s"${mockConfig.centralAuthServerUrl}/centralised-authorisation-server/token/search", userAgentHeader)(successResponse)
        val result = centralAuthConnector.getTokenAttributesResponse(gnapToken)
        result.futureValue mustBe Left(ErrorResponse(BAD_REQUEST, "Json Validation Error: List((/credId,List(JsonValidationError(List(error.path.missing),List()))), " +
          "(/authenticationProvider,List(JsonValidationError(List(error.path.missing),List()))), (/enrolments,List(JsonValidationError(List(error.path.missing),List()))))"))
      }
    }
    "a 400-499 response is returned" should {
      "return an ErrorResponse" in {
        val errorResponse = HttpResponse(status = NOT_FOUND, body = """No attributes found given token provided""", headers = Map.empty)
        setupMockHttpV2PostWithHeaderCarrier(s"${mockConfig.centralAuthServerUrl}/centralised-authorisation-server/token/search", userAgentHeader)(errorResponse)
        val result = centralAuthConnector.getTokenAttributesResponse(gnapToken)
        result.futureValue mustBe Left(ErrorResponse(NOT_FOUND, "No attributes found given token provided"))
      }
    }
    "a 500-599 response is returned" should {
      "return an ErrorResponse" in {
        setupMockHttpV2FailedPostWithHeaderCarrier(s"${mockConfig.centralAuthServerUrl}/centralised-authorisation-server/token/search",userAgentHeader)
        val result = centralAuthConnector.getTokenAttributesResponse(gnapToken)
        result.futureValue mustBe Left(ErrorResponse(INTERNAL_SERVER_ERROR, "Call to Central Auth Server Failed"))
      }
    }
  }
}
