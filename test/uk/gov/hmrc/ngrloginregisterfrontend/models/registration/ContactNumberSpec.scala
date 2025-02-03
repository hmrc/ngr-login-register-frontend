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
import uk.gov.hmrc.ngrloginregisterfrontend.models.ContactNumber

class ContactNumberSpec extends TestSupport with TestData{

  "Contact Number model" should {
    "serialise into json" in {
      Json.toJson(contactNumberModel) mustBe contactNumberJson
    }
    "deserialize from json" in {
      contactNumberJson.as[ContactNumber] mustBe contactNumberModel
    }
  }

  "Calling the isValidContactNumber" should {
    "return true if  a valid uk landline number" in {
        val contactNumber = ContactNumber("0300 200 3310").isValidContactNumber
        contactNumber mustBe true
    }
    "return true if a number has uk dialing code" in {
      val contactNumber = ContactNumber("+44300 200 3310").isValidContactNumber
      contactNumber mustBe true
    }
    "return false if the number contains special characters" in {
      val contactNumber = ContactNumber("0300 200 3310/;'[]l").isValidContactNumber
      contactNumber mustBe false
    }
    "return false if the number is malformed" in {
      val contactNumber = ContactNumber("blah blah blah").isValidContactNumber
      contactNumber mustBe false
    }
    "return false if the number has too many characters" in {
      val contactNumber = ContactNumber("0300 200 331012345678").isValidContactNumber
      contactNumber mustBe false
    }
    "return false if the number has too little characters" in {
      val contactNumber = ContactNumber("0300").isValidContactNumber
      contactNumber mustBe false
    }
  }

}
