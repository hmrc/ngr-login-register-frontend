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


import play.api.libs.json.{JsString, JsValue, Json}
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.TestSupport

class SaUtrSpec extends TestSupport {

  val saUtr: SaUtr = SaUtr("1097172987")
  def validateSaUtr(saUtr: String): Boolean = SaUtr.isValid(saUtr)

  val saUtrJson: JsValue = Json.parse(
    """
      |{
      |"sautr": "1097172987"
      |}
      |""".stripMargin
  )

  "SaUtr" should {
    "serialize to json" in {
      Json.toJson(saUtr) mustBe JsString("1097172987")
    }
    "deserialize from json" in {
      saUtrJson.as[SaUtr] mustBe saUtr
    }
  }

  "The validation of a sautr" should {
    "pass with a valid number without spaces" in {
      validateSaUtr("1097172987") mustBe true
    }
    "fail with more than 10 digits" in {
      validateSaUtr("12345678901") mustBe false
    }
    "fail with less than 10 digits" in {
      validateSaUtr("12345") mustBe false
    }
    "fail with letters" in {
      validateSaUtr("abc1234567") mustBe false
    }
    "fail with total garbage" in {
      validateSaUtr("XXX") mustBe false
      validateSaUtr("werionownadefwe") mustBe false
      validateSaUtr("@£%!)(*&^") mustBe false
      validateSaUtr("123456") mustBe false
    }
    "fail with only space" in {
      validateSaUtr("    ") mustBe false
    }
    "fail with valid number with leading space" in {
      validateSaUtr(" 1097172987") mustBe false
    }
    "fail with valid number with trailing space" in {
      validateSaUtr("1097172987 ") mustBe false
    }
    "fail with empty string" in {
      validateSaUtr("")  mustBe false
    }
  }

}
