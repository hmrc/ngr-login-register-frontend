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
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.ngrloginregisterfrontend.models._
import uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup.{AddressLookupRequest, Location, LookedUpAddress, LookedUpAddressWrapper, Uprn}
import uk.gov.hmrc.ngrloginregisterfrontend.models.centralauth.{Enrolment, Identifier, Identity, TokenAttributesResponse}
import uk.gov.hmrc.ngrloginregisterfrontend.models.cid.{MatchingDetails, Person, PersonAddress, PersonDetails}
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.ReferenceType.TRN
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.UserType.Individual
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.{AgentStatus, ReferenceNumber}

import java.time.LocalDate

trait TestData {

  val testAddressModel: Address =
    Address(line1 = "99",
      line2 = Some("Wibble Rd"),
      town = "Worthing",
      county = Some("West Sussex"),
      postcode = Postcode("BN110AA"),
      country = "UK",
    )

  val addressLookupAddress:LookedUpAddress =
    LookedUpAddress(
      lines = Seq("100"),
      town = "London",
      county = Some("Hillingdon"),
      postcode = "W126WA",
    )

  val testPostcode: String = "AA00 0AA"
  val testFilter: Option[String] = Some("")

  val testAddressString: String =
    "99 Wibble Rd, Testtown, West Sussex BN110AA"

  val testRegistrationModel: RatepayerRegistration =
    RatepayerRegistration(
      userType = Some(Individual),
      agentStatus = Some(AgentStatus.Agent),
      name = Some(Name("John Doe")),
      tradingName = Some(TradingName("CompanyLTD")),
      email = Some(Email("JohnDoe@digital.hmrc.gov.uk")),
      contactNumber = Some(ContactNumber("07123456789")),
      secondaryNumber = Some(ContactNumber("07123456789")),
      address = Some(
        Address(line1 = "99",
          line2 = Some("Wibble Rd"),
          town = "Worthing",
          county = Some("West Sussex"),
          postcode = Postcode("BN110AA"),
          country = "UK",
        )
      ),
      referenceNumber = Some(ReferenceNumber(TRN, "12345")),
      isRegistered = Some(true)
    )

  val testAddressLookupResponseModel : LookedUpAddressWrapper = LookedUpAddressWrapper (
    id = "GB690091234501",
    uprn = Uprn(690091234501L),
    address =
      LookedUpAddress(
        lines = Seq("1 Test Street"),
        town = "Testtown",
        county = None,
        postcode = "AA00 0AA"
      ),
    language = "English",
    location = None
  )


  val contactNumberModel: ContactNumber = ContactNumber("0300 200 3310")
  val nameModel: Name = Name("Lovely Fella")

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
    """{"userType":"Individual","agentStatus":"Agent","name":{"value":"John Doe"},"tradingName":{"value":"CompanyLTD"},"email":{"value":"JohnDoe@digital.hmrc.gov.uk"},"contactNumber":{"value":"07123456789"},"secondaryNumber":{"value":"07123456789"},"address":{"line1":"99","line2":"Wibble Rd","town":"Worthing","county":"West Sussex","postcode":{"value":"BN110AA"},"country":"UK"},"referenceNumber":{"referenceType":"TRN","value":"12345"},"isRegistered":true}
      |""".stripMargin)


  val minRegResponseJson: JsValue = Json.parse(
    """{"userType":"Individual","agentStatus":"Agent","name":{"value":"John Doe"},"email":{"value":"JohnDoe@digital.hmrc.gov.uk"},"contactNumber":{"value":"07123456789"},"address":{"line1":"99","line2":"Wibble Rd","town":"Worthing","county":"West Sussex","postcode":{"value":"BN110AA"},"country":"UK"},"referenceNumber":{"referenceType":"TRN","value":"12345"},"isRegistered":true}
      |""".stripMargin)


  val addressJsonResponse: JsValue = Json.parse("""{"line1":"99","line2":"Wibble Rd","town":"Worthing","county":"West Sussex","postcode":{"value":"BN110AA"},"country":"UK"}""".stripMargin)
  val minAddressModel: Address = testAddressModel.copy(line2 = None, county = None)
  val addressMinJsonResponse: JsValue = Json.parse("""{"line1":"99","town":"Worthing","postcode":{"value":"BN110AA"},"country":"UK"}""".stripMargin)

  val postcodeModel: Postcode = Postcode("E20 1HZ")
  val postcodeJson: JsValue = Json.parse(
    """
      |{"value":"E20 1HZ"}
      |""".stripMargin)

  val matchingDetailsResponse: MatchingDetails = MatchingDetails("Jim","Ferguson",Some(SaUtr("1097133333")))
  val personResponse: Person = Person(Some("Mr"),Some("John"),Some("Joe"),Some("Ferguson"),Some("BSC"),Some("M"), Some(LocalDate.parse("1952-04-01")),Some(Nino("AA000003D")))
  val addressResponse: PersonAddress = PersonAddress(Some("26 FARADAY DRIVE"), Some("PO BOX 45"),Some("LONDON"), None,None,Some("CT1 1RQ"),Some("GREAT BRITAIN") ,Some(LocalDate.parse("2009-08-29")),Some("Residential"))
  val personDetailsResponse: PersonDetails = PersonDetails(person = personResponse, address = addressResponse)
  val cidMatchingDetailsResponseJson: JsValue = Json.parse(
    """
      |{
      |  "name": {
      |    "current": {
      |      "firstName": "Jim",
      |      "lastName": "Ferguson"
      |    },
      |    "previous": []
      |  },
      |  "ids": {
      |    "sautr": "1097133333",
      |    "nino": "AA000003D"
      |  },
      |  "dateOfBirth": "23041948",
      |  "deceased": false
      |}
      |""".stripMargin)

  val cidPersonDetailsResponseJson : JsValue = Json.parse(
    """
      |{
      |  "etag" : "115",
      |  "person" : {
      |    "firstName" : "John",
      |    "middleName" : "Joe",
      |    "lastName" : "Ferguson",
      |    "title" : "Mr",
      |    "honours": "BSC",
      |    "sex" : "M",
      |    "dateOfBirth" : "1952-04-01",
      |    "nino" : "AA000003D",
      |    "deceased" : false
      |  },
      |  "address" : {
      |    "line1" : "26 FARADAY DRIVE",
      |    "line2" : "PO BOX 45",
      |    "line3" : "LONDON",
      |    "postcode" : "CT1 1RQ",
      |    "startDate": "2009-08-29",
      |    "country" : "GREAT BRITAIN",
      |    "type" : "Residential"
      |  }
      |}
      |""".stripMargin
  )


  val expectedRatepayerRegistration : JsValue = Json.parse(
    """{
      |  "userType" : "Individual",
      |  "agentStatus" : "Autonomous",
      |  "name" : {
      |    "value" : "Anna"
      |  },
      |  "tradingName" : {
      |    "value" : "Anna Ltd."
      |  },
      |  "email" : {
      |    "value" : "Anna.s@annaltd.com"
      |  },
      |  "contactNumber" : {
      |    "value" : "08707632451"
      |  },
      |  "address" : {
      |    "line1" : "Address Line 1",
      |    "town" : "Chester",
      |    "postcode" : {
      |      "value" : "CH2 7RH"
      |    },
      |    "country" : "GB"
      |  },
      |  "isRegistered" : false
      |}""".stripMargin
  )

  val addressLookupResponseJson : JsValue = Json.parse(
    """
      |[
      | {
      |   "id":"GB690091234501",
      |   "uprn":690091234501,
      |   "address":{
      |     "lines":["1 Test Street"],
      |     "town":"Testtown",
      |     "postcode":"AA00 0AA",
      |     "subdivision":{"code":"GB-ENG","name":"England"},
      |     "country":{"code":"GB","name":"United Kingdom"}
      |   },
      |   "language":"English",
      |   "localCustodian":{"code":121,"name":"NORTH SOMERSET"}
      | }
      |]
      |""".stripMargin
  )

  val addressLookupResponsesJson: JsValue = Json.parse(
    """
      |[
      | {
      |   "id":"GB690091234501",
      |   "uprn":690091234501,
      |   "address":{
      |     "lines":["1 Test Street"],
      |     "town":"Testtown",
      |     "postcode":"AA00 0AA",
      |     "subdivision":{"code":"GB-ENG","name":"England"},
      |     "country":{"code":"GB","name":"United Kingdom"}
      |   },
      |   "language":"en"
      | },
      |  {
      |   "id":"GB690091234501",
      |   "uprn":690091234501,
      |   "address":{
      |     "lines":["1 Test Street"],
      |     "town":"Testtown",
      |     "postcode":"AA00 0AA",
      |     "subdivision":{"code":"GB-ENG","name":"England"},
      |     "country":{"code":"GB","name":"United Kingdom"}
      |   },
      |   "language":"en"
      | },
      |  {
      |   "id":"GB690091234501",
      |   "uprn":690091234501,
      |   "address":{
      |     "lines":["1 Test Street"],
      |     "town":"Testtown",
      |     "postcode":"AA00 0AA",
      |     "subdivision":{"code":"GB-ENG","name":"England"},
      |     "country":{"code":"GB","name":"United Kingdom"}
      |   },
      |   "language":"en"
      | },
      |  {
      |   "id":"GB690091234501",
      |   "uprn":690091234501,
      |   "address":{
      |     "lines":["1 Test Street"],
      |     "town":"Testtown",
      |     "postcode":"AA00 0AA",
      |     "subdivision":{"code":"GB-ENG","name":"England"},
      |     "country":{"code":"GB","name":"United Kingdom"}
      |   },
      |   "language":"en"
      | },
      |  {
      |   "id":"GB690091234501",
      |   "uprn":690091234501,
      |   "address":{
      |     "lines":["1 Test Street"],
      |     "town":"Testtown",
      |     "postcode":"AA00 0AA",
      |     "subdivision":{"code":"GB-ENG","name":"England"},
      |     "country":{"code":"GB","name":"United Kingdom"}
      |   },
      |   "language":"en"
      | },
      |  {
      |   "id":"GB690091234501",
      |   "uprn":690091234501,
      |   "address":{
      |     "lines":["1 Test Street"],
      |     "town":"Testtown",
      |     "postcode":"AA00 0AA",
      |     "subdivision":{"code":"GB-ENG","name":"England"},
      |     "country":{"code":"GB","name":"United Kingdom"}
      |   },
      |   "language":"en"
      | },
      |  {
      |   "id":"GB690091234501",
      |   "uprn":690091234501,
      |   "address":{
      |     "lines":["1 Test Street"],
      |     "town":"Testtown",
      |     "postcode":"AA00 0AA",
      |     "subdivision":{"code":"GB-ENG","name":"England"},
      |     "country":{"code":"GB","name":"United Kingdom"}
      |   },
      |   "language":"en"
      | },
      |  {
      |   "id":"GB690091234501",
      |   "uprn":690091234501,
      |   "address":{
      |     "lines":["1 Test Street"],
      |     "town":"Testtown",
      |     "postcode":"AA00 0AA",
      |     "subdivision":{"code":"GB-ENG","name":"England"},
      |     "country":{"code":"GB","name":"United Kingdom"}
      |   },
      |   "language":"en"
      | },
      |  {
      |   "id":"GB690091234501",
      |   "uprn":690091234501,
      |   "address":{
      |     "lines":["1 Test Street"],
      |     "town":"Testtown",
      |     "postcode":"AA00 0AA",
      |     "subdivision":{"code":"GB-ENG","name":"England"},
      |     "country":{"code":"GB","name":"United Kingdom"}
      |   },
      |   "language":"en"
      | },
      |  {
      |   "id":"GB690091234501",
      |   "uprn":690091234501,
      |   "address":{
      |     "lines":["1 Test Street"],
      |     "town":"Testtown",
      |     "postcode":"AA00 0AA",
      |     "subdivision":{"code":"GB-ENG","name":"England"},
      |     "country":{"code":"GB","name":"United Kingdom"}
      |   },
      |   "language":"en"
      | },
      |  {
      |   "id":"GB690091234501",
      |   "uprn":690091234501,
      |   "address":{
      |     "lines":["1 Test Street"],
      |     "town":"Testtown",
      |     "postcode":"AA00 0AA",
      |     "subdivision":{"code":"GB-ENG","name":"England"},
      |     "country":{"code":"GB","name":"United Kingdom"}
      |   },
      |   "language":"en"
      | },
      |  {
      |   "id":"GB690091234501",
      |   "uprn":690091234501,
      |   "address":{
      |     "lines":["1 Test Street"],
      |     "town":"Testtown",
      |     "postcode":"AA00 0AA",
      |     "subdivision":{"code":"GB-ENG","name":"England"},
      |     "country":{"code":"GB","name":"United Kingdom"}
      |   },
      |   "language":"en"
      | },
      |  {
      |   "id":"GB690091234501",
      |   "uprn":690091234501,
      |   "address":{
      |     "lines":["1 Test Street"],
      |     "town":"Testtown",
      |     "postcode":"AA00 0AA",
      |     "subdivision":{"code":"GB-ENG","name":"England"},
      |     "country":{"code":"GB","name":"United Kingdom"}
      |   },
      |   "language":"en"
      | },
      |  {
      |   "id":"GB690091234501",
      |   "uprn":690091234501,
      |   "address":{
      |     "lines":["1 Test Street"],
      |     "town":"Testtown",
      |     "postcode":"AA00 0AA",
      |     "subdivision":{"code":"GB-ENG","name":"England"},
      |     "country":{"code":"GB","name":"United Kingdom"}
      |   },
      |   "language":"en"
      | }
      |]
      |""".stripMargin
  )

  val invalidAddressLookupResponseJson : JsValue = Json.parse(
    """
      |{
      |  "parentUprn": 1234567890,
      |  "usrn": 987654321,
      |  "organisation": "Capgemini",
      |  "address": {
      |    "lines": [
      |      "99Wibble Rd"
      |    ],
      |    "town": "Worthing",
      |    "postcode": "BN110AA",
      |    "subdivision": {
      |      "code": "code",
      |      "name": "name"
      |    },
      |    "country": {
      |      "code": "GB",
      |      "name": "Great Britain"
      |    }
      |  },
      |  "localCustodian": {
      |    "code": 123,
      |    "name": "LcName"
      |  },
      |  "language": "English",
      |  "administrativeArea": "AdminArea",
      |  "poBox": "PO321"
      |}
      |""".stripMargin
  )

  val tokenAttributesResponseJson: JsValue = Json.parse(
    """{
      | "authenticationProvider": "One Login",
      | "name": "John Ferguson",
      | "email": "test@testUser.com",
      | "identity": {
      |    "provider": "MDTP",
      |    "level": "50",
      |    "nino": "AB666666A"
      | },
      | "enrolments": [{
      |			"service": "IR-SA",
      |			"identifiers": [{
      |				"key": "UTR",
      |				"value": "1234567890"
      |			}],
      |   "state": "Activated",
      |			"friendlyName": "My SA"
      |		}],
      | "credId": "12345",
      | "eacdGroupId": "12345",
      | "caUserId": "12345"
      |}""".stripMargin
  )

  val tokenAttributesResponse: TokenAttributesResponse = TokenAttributesResponse(
    authenticationProvider = "One Login",
    name = Some("John Ferguson"),
    email = Some("test@testUser.com"),
    identity = Some(Identity("MDTP",Some(Nino("AB666666A")),Some("50"))),
    enrolments = Set(Enrolment("IR-SA",Seq(Identifier("UTR","1234567890")),"My SA","Activated")),
    credId = "12345",
    eacdGroupId = Some("12345"),
    caUserId = Some("12345")
  )

  val gnapToken = "i16lTUCYVVcwAEDOtbZNyly2wwgJ"
}
