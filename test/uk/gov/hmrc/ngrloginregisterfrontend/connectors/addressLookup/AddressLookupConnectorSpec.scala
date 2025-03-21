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

import play.api.libs.json.{JsArray, JsNumber, JsValue}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.TestData
import uk.gov.hmrc.ngrloginregisterfrontend.mocks.MockHttpV2
import uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup.AddressLookupResponseModel
import uk.gov.hmrc.ngrloginregisterfrontend.util.NGRLogger

class AddressLookupConnectorSpec extends MockHttpV2 with TestData {

  val httpClientV2: HttpClientV2 = mock[HttpClientV2]
  val logger: NGRLogger = inject[NGRLogger]
  val testAlfConnector: AddressLookupConnector = new AddressLookupConnector(mockHttpClientV2,mockConfig, logger)
  val addressLookupHeader: Seq[(String, String)] = Seq("X-Hmrc-Origin" -> "ngr-login-register-frontend")

  "Calling the Address lookup api" when {
    "a valid AddressLookupRequest is passed in with only a valid postcode" should {
      "return a 200(OK)" in {
        val successResponse: JsValue = addressLookupResponseJson
        setupMockHttpV2PostWithHeaderCarrier(s"${mockConfig.addressLookupUrl}/address-lookup/lookup", addressLookupHeader)(successResponse)
        val result: AddressLookupResponse = testAlfConnector.findAddressByPostcode(testPostcode, None).futureValue
        result mustBe AddressLookupSuccessResponse(AddressLookupResponseModel(Seq(testAddressLookupResponseModel)))
      }
    }
    "a valid AddressLookupRequest is passed in with both a valid postcode and filter" should {
      "return a 200(OK)" in {
        val successResponse: JsValue = addressLookupResponseJson
        setupMockHttpV2PostWithHeaderCarrier(s"${mockConfig.addressLookupUrl}/address-lookup/lookup", addressLookupHeader)(successResponse)
        val result: AddressLookupResponse = testAlfConnector.findAddressByPostcode(testPostcode, Some("1 Test Street")).futureValue
        result mustBe AddressLookupSuccessResponse(AddressLookupResponseModel(Seq(testAddressLookupResponseModel)))
      }
    }
    "json is invalid" should {
      "return an error" in {
        val successResponse: JsArray = JsArray(IndexedSeq(JsNumber(123),JsNumber(123)))
        setupMockHttpV2PostWithHeaderCarrier(s"${mockConfig.addressLookupUrl}/address-lookup/lookup", addressLookupHeader)(successResponse)
        val result = testAlfConnector.findAddressByPostcode(testPostcode, None).futureValue
        result match {
          case AddressLookupSuccessResponse(_) => fail("must not succeed")
          case AddressLookupErrorResponse(_) => succeed
        }
      }
    }
    "a 500-599 response is returned" should {
      "return an ErrorResponse" in {
        setupMockHttpV2FailedPostWithHeaderCarrier(s"${mockConfig.addressLookupUrl}/address-lookup/lookup", addressLookupHeader)
        val result = testAlfConnector.findAddressByPostcode(testPostcode, None).futureValue
        result.leftSide.toString mustBe "AddressLookupErrorResponse(java.lang.RuntimeException: Request Failed)"
      }
    }
  }
}
