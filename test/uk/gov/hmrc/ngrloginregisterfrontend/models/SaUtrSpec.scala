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
      Json.toJson(saUtr) mustBe saUtrJson
    }
    "deserialize from json" in {
      saUtrJson.as[SaUtr] mustBe saUtr
    }
  }


}
