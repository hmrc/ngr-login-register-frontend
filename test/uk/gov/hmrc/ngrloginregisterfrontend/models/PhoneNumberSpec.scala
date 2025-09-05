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

package uk.gov.hmrc.ngrloginregisterfrontend.models

import org.scalatest.matchers.should.Matchers._
import play.api.data.Form
import play.api.libs.json.Json
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.{TestData, TestSupport}
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.PhoneNumber

class PhoneNumberSpec extends TestSupport with TestData{
  "Phone number model" should {
    "serialise into json" in {
      Json.toJson(contactNumberModel) mustBe phoneNumberJson
    }
    "deserialize from json" in {
      phoneNumberJson.as[PhoneNumber] mustBe contactNumberModel
    }
  }

  "PhoneNumber form" should {
    "bind a valid PhoneNumber" in {
      val data = Map("phoneNumber-value" -> "0123456789")
      val form: Form[PhoneNumber] = PhoneNumber.form()
      val boundForm = form.bind(data)
      boundForm.errors shouldBe empty
      boundForm.value shouldBe Some(PhoneNumber("0123456789"))
    }
  }
}
