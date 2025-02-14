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
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.TestSupport

class NinoSpec extends TestSupport {

  val nino: Nino = Nino("AA055075C")
  def validateNino(nino: String): Boolean = Nino.isValid(nino)

  val ninoJson: JsValue = Json.parse(
    """
      |{
      |"nino": "AA055075C"
      |}
      |""".stripMargin
  )

  "Nino" should {
    "deserialize to json" in {
      Json.toJson(nino) mustBe JsString("AA055075C")
    }
    "serialize to json" in {
      ninoJson.as[Nino] mustBe nino
    }
  }

  "The validation of a nino" should {
    "pass with valid number without spaces" in {
      validateNino("AB123456C") mustBe true
    }
    "pass with valid number with spaces" in {
      validateNino("AB 12 34 56 C") mustBe true
    }
    "fail with valid number with leading space" in {
      validateNino(" AB123456C") mustBe false
    }
    "fail with valid number with trailing space" in {
      validateNino("AB123456C ") mustBe false
    }
    "fail with empty string" in {
      validateNino("")  mustBe false
    }
    "fail with only space" in {
      validateNino("    ") mustBe false
    }
    "fail with total garbage" in {
      validateNino("XXX") mustBe false
      validateNino("werionownadefwe") mustBe false
      validateNino("@£%!)(*&^") mustBe false
      validateNino("123456") mustBe false
    }
    "fail with only one starting letter" in {
      validateNino("A123456C") mustBe false
      validateNino("A1234567C") mustBe false
    }
    "fail with three starting letters" in {
      validateNino("ABC12345C") mustBe false
      validateNino("ABC123456C") mustBe false
    }
    "fail with lowercase letters" in {
      validateNino("ab123456c")  mustBe false
    }
  }
}
