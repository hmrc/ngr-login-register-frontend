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

package uk.gov.hmrc.ngrloginregisterfrontend.models.registration

import play.api.libs.json.Json
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.TestSupport
import uk.gov.hmrc.ngrloginregisterfrontend.models.{Address, Postcode, RatepayerRegistration}

class AddressModelSpec extends TestSupport {

  val testAddressModel: Address =
    Address(line1 = "99",
      line2 = Some("Wibble Rd"),
      town = "Worthing",
      county = Some("West Sussex"),
      postcode = Postcode("BN110AA"),
      country = "UK",
    )

  val optionalFields = testAddressModel.copy(line2 = None, county = None)

  val responseJson = Json.parse("""{"line1":"99","line2":"Wibble Rd","town":"Worthing","county":"West Sussex","postcode":{"value":"BN110AA"},"country":"UK"}""".stripMargin)

  val optionalResponseJson = Json.parse("""{"line1":"99","town":"Worthing","postcode":{"value":"BN110AA"},"country":"UK"}""".stripMargin)

  "AddressModel" should {
    "serialise into Json" when {
      "all fields are present" in {
        Json.toJson(testAddressModel) mustBe responseJson
      }
      "the optional fields are not present" in {
        Json.toJson(optionalFields) mustBe optionalResponseJson
      }

    }
    "deserialise from Json" when {
      "all fields are present" in {
        responseJson.as[Address] mustBe testAddressModel
      }
      "the optional fields are not present" in {
        optionalResponseJson.as[Address] mustBe optionalFields
      }
    }
    "format into a String" when{
      "toString method is called" in{
        testAddressModel.toString mustBe "99, Wibble Rd, Worthing, West Sussex, BN110AA, UK"
      }
    }
  }

}
