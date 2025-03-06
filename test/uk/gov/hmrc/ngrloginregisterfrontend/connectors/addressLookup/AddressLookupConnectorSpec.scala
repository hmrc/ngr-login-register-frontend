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

package uk.gov.hmrc.ngrloginregisterfrontend.connectors.addressLookup

import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.AddressLookup.AddressLookupConnector
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.TestData
import uk.gov.hmrc.ngrloginregisterfrontend.mocks.MockHttpV2
import uk.gov.hmrc.ngrloginregisterfrontend.models.ErrorResponse
import uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup.AddressLookupResponse
import uk.gov.hmrc.ngrloginregisterfrontend.util.NGRLogger

import scala.concurrent.Future

class AddressLookupConnectorSpec extends MockHttpV2 with TestData {

  val httpClientV2: HttpClientV2 = mock[HttpClientV2]
  val logger: NGRLogger = inject[NGRLogger]
  val testAlfConnector: AddressLookupConnector = new AddressLookupConnector(mockHttpClientV2,mockConfig, logger)

  "Calling the Address lookup api" when {
    "a valid AddressLookupRequest is passed in" should {
      "return a 200(OK)" in {
        val successResponse = HttpResponse(status = OK, json = addressLookupResponseJson, headers = Map.empty)
        setupMockHttpV2Post(s"${mockConfig.addressLookupUrl}/address-lookup/lookup")(successResponse)
        val result: Future[Either[ErrorResponse,Seq[AddressLookupResponse]]] = testAlfConnector.findAddressByPostcode(testAddressLookupRequest)
        result.futureValue mustBe Right(Seq(testAddressLookupResponseModel))
      }
    }
    "json is invalid" should {
      "return an error" in {
        val successResponse = HttpResponse(status = OK, json = Json.obj(), headers = Map.empty)
        setupMockHttpV2Post(s"${mockConfig.addressLookupUrl}/address-lookup/lookup")(successResponse)
        val result = testAlfConnector.findAddressByPostcode(testAddressLookupRequest).futureValue
        result.left.map(_.code) mustBe Left(BAD_REQUEST)
      }
    }
    "a 400-499 response is returned" should{
      "return an ErrorResponse" in {
        val errorResponse = HttpResponse(status = NOT_FOUND, body="""No address found for this post code""", headers = Map.empty)
        setupMockHttpV2Post(s"${mockConfig.addressLookupUrl}/address-lookup/lookup")(errorResponse)
        val result = testAlfConnector.findAddressByPostcode(testAddressLookupRequest)
        result.futureValue mustBe Left(ErrorResponse(NOT_FOUND, "No address found for this post code"))
      }
    }
    "a 500-599 response is returned" should {
      "return an ErrorResponse" in {
        setupMockHttpV2FailedPost(s"${mockConfig.addressLookupUrl}/address-lookup/lookup")
        val result = testAlfConnector.findAddressByPostcode(testAddressLookupRequest)
        result.futureValue mustBe Left(ErrorResponse(INTERNAL_SERVER_ERROR, "call to AddressLookup failed: null Request Failed"))
      }
    }

  }

}
