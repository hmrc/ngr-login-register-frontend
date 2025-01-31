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

import play.api.libs.json.{JsResultException, Json}
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.TestSupport
import uk.gov.hmrc.ngrloginregisterfrontend.models.{Address, ContactNumber, Email, Name, Postcode, RatepayerRegistration, TradingName}

class RatepayerRegistrationSpec extends TestSupport {

  val testRegistrationModel: RatepayerRegistration =
    RatepayerRegistration(UserType.Individual,
      AgentStatus.Agent,
      Name("John Doe"),
      Some(TradingName("CompanyLTD")),
      Email("JohnDoe@digital.hmrc.gov.uk"),
      ContactNumber("07123456789"),
      Some(ContactNumber("07123456789")),
      Address(line1 = "99",
        line2 = Some("Wibble Rd"),
        town = "Worthing",
        county = Some("West Sussex"),
        postcode = Postcode("BN110AA"),
        country = "UK",
      )
    )

  val optionalFields = testRegistrationModel.copy(tradingName = None, secondaryNumber = None)

  val regResponseJson = Json.parse(
    """{"userType":"Individual","agentStatus":"Agent","name":{"value":"John Doe"},"tradingName":{"value":"CompanyLTD"},"email":{"value":"JohnDoe@digital.hmrc.gov.uk"},"contactNumber":{"value":"07123456789"},"secondaryNumber":{"value":"07123456789"},"address":{"line1":"99","line2":"Wibble Rd","town":"Worthing","county":"West Sussex","postcode":{"value":"BN110AA"},"country":"UK"}}
      |""".stripMargin)

  val optionalRegResponseJson = Json.parse(
    """{"userType":"Individual","agentStatus":"Agent","name":{"value":"John Doe"},"email":{"value":"JohnDoe@digital.hmrc.gov.uk"},"contactNumber":{"value":"07123456789"}, "address":{"line1":"99","line2":"Wibble Rd","town":"Worthing","county":"West Sussex","postcode":{"value":"BN110AA"},"country":"UK"}}
      |""".stripMargin)

  val invalidRegResponseJson = Json.parse(
    """{"userType":"Person","agentStatus":"Agent","name":{"value":"John Doe"},"email":{"value":"JohnDoe@digital.hmrc.gov.uk"},"contactNumber":{"value":"07123456789"}, "address":{"line1":"99","line2":"Wibble Rd","town":"Worthing","county":"West Sussex","postcode":{"value":"BN110AA"},"country":"UK"}}
      |""".stripMargin)


  "RatepayerRegistrationModel" should {
    "serialise into Json" when {
      "all fields are present" in {
        Json.toJson(testRegistrationModel) mustBe regResponseJson
      }
      "the optional fields are not present" in {
        Json.toJson(optionalFields) mustBe optionalRegResponseJson
      }

    }
    "deserialise from Json" when {
      "all fields are present" in {
        regResponseJson.as[RatepayerRegistration] mustBe testRegistrationModel
      }
      "the optional fields are not present" in {
        optionalRegResponseJson.as[RatepayerRegistration] mustBe optionalFields
      }
    }
  }



}
