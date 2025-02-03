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

package uk.gov.hmrc.ngrloginregisterfrontend.helpers

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.{AgentStatus, UserType}
import uk.gov.hmrc.ngrloginregisterfrontend.models.{Address, ContactNumber, Email, Name, Postcode, RatepayerRegistration, TradingName}

trait TestData {

  val testAddressModel: Address =
    Address(line1 = "99",
      line2 = Some("Wibble Rd"),
      town = "Worthing",
      county = Some("West Sussex"),
      postcode = Postcode("BN110AA"),
      country = "UK",
    )
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

  val contactNumberModel: ContactNumber = ContactNumber("0300 200 3310")
  val contactNumberJson: JsValue = Json.parse(
    """
      |{"value":"0300 200 3310"}
      |""".stripMargin)

  val emailModel: Email = Email("test@digital.hmrc.gov.uk")
  val emailJson: JsValue = Json.parse(
    """
      |{"value":"test@digital.hmrc.gov.uk"}
      |""".stripMargin)

  val minRegResponseModel: RatepayerRegistration = testRegistrationModel.copy(tradingName = None, secondaryNumber = None)

  val regResponseJson: JsValue = Json.parse(
    """{"userType":"Individual","agentStatus":"Agent","name":{"value":"John Doe"},"tradingName":{"value":"CompanyLTD"},"email":{"value":"JohnDoe@digital.hmrc.gov.uk"},"contactNumber":{"value":"07123456789"},"secondaryNumber":{"value":"07123456789"},"address":{"line1":"99","line2":"Wibble Rd","town":"Worthing","county":"West Sussex","postcode":{"value":"BN110AA"},"country":"UK"}}
      |""".stripMargin)

  val minRegResponseJson: JsValue = Json.parse(
    """{"userType":"Individual","agentStatus":"Agent","name":{"value":"John Doe"},"email":{"value":"JohnDoe@digital.hmrc.gov.uk"},"contactNumber":{"value":"07123456789"}, "address":{"line1":"99","line2":"Wibble Rd","town":"Worthing","county":"West Sussex","postcode":{"value":"BN110AA"},"country":"UK"}}
      |""".stripMargin)


  val addressJsonResponse: JsValue = Json.parse("""{"line1":"99","line2":"Wibble Rd","town":"Worthing","county":"West Sussex","postcode":{"value":"BN110AA"},"country":"UK"}""".stripMargin)
  val minAddressModel: Address = testAddressModel.copy(line2 = None, county = None)
  val addressMinJsonResponse: JsValue = Json.parse("""{"line1":"99","town":"Worthing","postcode":{"value":"BN110AA"},"country":"UK"}""".stripMargin)

  val postcodeModel: Postcode = Postcode("E20 1HZ")
  val postcodeJson: JsValue = Json.parse(
    """
      |{"value":"E20 1HZ"}
      |""".stripMargin)
}
