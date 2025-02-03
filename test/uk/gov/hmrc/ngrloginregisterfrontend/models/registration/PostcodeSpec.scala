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
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.{TestData, TestSupport}
import uk.gov.hmrc.ngrloginregisterfrontend.models.Postcode

class PostcodeSpec extends TestSupport with TestData {

  "Postcode model" should {
    "serialise into json" in {
      Json.toJson(postcodeModel) mustBe postcodeJson
    }
    "deserialize from json" in {
      postcodeJson.as[Postcode] mustBe postcodeModel
    }
  }

  "Calling the isValidPostcode" should {
    "return true if a valid post code is passed" in {
      val postCode = Postcode("E20 1HZ").isValidPostcode
      postCode mustBe true
    }
    "return true if its a valid post with only two characters as the prefix" in {
      val postCode = Postcode("M1 2HZ").isValidPostcode
      postCode mustBe true
    }
    "return false if the post code starts with a number" in {
      val postCode = Postcode("12 1HZ").isValidPostcode
      postCode mustBe false
    }
    "return false if the post code has special characters in it" in {
      val postCode = Postcode("E20 1HZ';';[;[..").isValidPostcode
      postCode mustBe false
    }
    "return false if the post code is malformed" in {
      val postCode = Postcode("blah blah blah").isValidPostcode
      postCode mustBe false
    }
  }


}
