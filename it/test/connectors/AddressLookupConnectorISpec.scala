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
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.addressLookup.{AddressLookupConnector, AddressLookupErrorResponse, AddressLookupResponse, AddressLookupSuccessResponse}
import uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup.AddressLookupResponseModel

import scala.concurrent.ExecutionContext

class AddressLookupConnectorISpec extends AnyWordSpec with IntegrationSpecBase with Injecting with IntegrationTestData{

  implicit lazy val ec: ExecutionContext = inject[ExecutionContext]
  lazy val connector: AddressLookupConnector = app.injector.instanceOf[AddressLookupConnector]

  override def beforeEach(): Unit = {
    WireMock.reset()
  }

  "AddressLookupConnector" when {
    "calling findAddressByPostcode" when {
      "sending a request" should {
        "return a successful response" in {
          WiremockHelper.stubPost(s"/address-lookup/lookup",OK, addressLookupResponseJson)
          val result = connector.findAddressByPostcode(testAddressLookupRequest.postcode, None).futureValue
          result mustBe AddressLookupSuccessResponse(AddressLookupResponseModel(Seq(testAddressLookupResponseModel)))
          WiremockHelper.verifyPost(s"/address-lookup/lookup")
        }
        "return an error when the request fails" in {
          WiremockHelper.stubPost(s"/address-lookup/lookup", INTERNAL_SERVER_ERROR, "Call to address lookup failed")

          val result: AddressLookupResponse = connector.findAddressByPostcode(testAddressLookupRequest.postcode, None).futureValue
          result match {
            case AddressLookupSuccessResponse(_) => fail("should return an error")
            case AddressLookupErrorResponse(_) => succeed
          }

          WiremockHelper.verifyPost(s"/address-lookup/lookup")
        }
      }
    }
  }
}
