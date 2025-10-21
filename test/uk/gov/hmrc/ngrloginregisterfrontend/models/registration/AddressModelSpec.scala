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

import org.scalatest.matchers.should.Matchers._
import play.api.data.Form
import play.api.libs.json.Json
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.{TestData, TestSupport}
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.Address

class AddressModelSpec extends TestSupport with TestData {

  "AddressModel" should {
    "serialise into Json" when {
      "all fields are present" in {
        Json.toJson(testAddressModel) mustBe addressJsonResponse
      }
      "the optional fields are not present" in {
        Json.toJson(minAddressModel) mustBe addressMinJsonResponse
      }

    }
    "deserialise from Json" when {
      "all fields are present" in {
        addressJsonResponse.as[Address] mustBe testAddressModel
      }
      "the optional fields are not present" in {
        addressMinJsonResponse.as[Address] mustBe minAddressModel
      }
    }
    "format into a String" when{
      "toString method is called" in{
        testAddressModel.toString mustBe "99, Wibble Rd, Worthing, West Sussex, BN110AA"
      }
    }
  }

  "Address form" should {
    "bind a valid address" in {
      val data = Map("postcode-value" -> "AB1 XYZ")
      val form: Form[Address] = Address.form()
      val boundForm = form.bind(data)
      boundForm.value shouldBe empty
    }
  }
}
