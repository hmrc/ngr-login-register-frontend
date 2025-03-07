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
import uk.gov.hmrc.ngrloginregisterfrontend.models.Email

class EmailSpec extends TestSupport with TestData{

  "Email model" should {
    "serialise into json" in {
      Json.toJson(emailModel) mustBe emailJson
    }
    "deserialize from json" in {
      emailJson.as[Email] mustBe emailModel
    }
  }

  "Calling the isValidEmail" should {
    "return true if a valid email passed" in {
      val email = Email("test@digital.hmrc.gov.uk").isValidEmail
      email mustBe true
    }
    "return false if the email does not contain the domain" in {
      val email = Email("aaaa@aaaa").isValidEmail
      email mustBe false
    }
    "return false if the email contains special characters" in {
      val email = Email("test@digital.hmrc.gov.uk/.;'';[;").isValidEmail
      email mustBe false
    }
    "return false if the email only contains first half" in {
      val email = Email("test").isValidEmail
      email mustBe false
    }
    "return false if the email only contains the second half" in {
      val email = Email("@digital.hmrc.gov.uk").isValidEmail
      email mustBe false
    }
  }

}
