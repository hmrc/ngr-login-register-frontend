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
import play.api.data.{Form, FormError}
import play.api.libs.json.Json
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.{TestData, TestSupport}
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.Email

class EmailSpec extends TestSupport with TestData{
  "Email model" should {
    "serialise into json" in {
      Json.toJson(emailModel) mustBe emailJson
    }
    "deserialize from json" in {
      emailJson.as[Email] mustBe emailModel
    }
  }

  "Email form" should {
    "bind a valid email" in {
      val data = Map("email-value" -> "test@test.com")
      val form: Form[Email] = Email.form()
      val boundForm = form.bind(data)
      boundForm.errors shouldBe empty
      boundForm.value shouldBe Some(Email("test@test.com"))
    }
  }
}
