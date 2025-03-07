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

package helpers

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup.{AddressLookupRequest, AddressLookupResponse, LocalCustodian, Subdivision, Address => AlfAddress}
import uk.gov.hmrc.ngrloginregisterfrontend.models.centralauth.{Enrolment, Identifier, Identity, TokenAttributesResponse}
import uk.gov.hmrc.ngrloginregisterfrontend.models.cid.{Person, PersonAddress, PersonDetails}

import java.time.LocalDate

trait IntegrationTestData {
  val nino : Nino = Nino("AA000003D")

  val personResponse: Person = Person(Some("Mr"),Some("John"),Some("Joe"),Some("Ferguson"),Some("BSC"),Some("M"), Some(LocalDate.parse("1952-04-01")),Some(Nino("TW189213B")))
  val addressResponse: PersonAddress = PersonAddress(Some("26 FARADAY DRIVE"), Some("PO BOX 45"),Some("LONDON"), None,None,Some("CT1 1RQ"),Some("GREAT BRITAIN") ,Some(LocalDate.parse("2009-08-29")),Some("Residential"))
  val personDetailsResponse: PersonDetails = PersonDetails(person = personResponse, address = addressResponse)

  val cidMatchingDetailsResponseJson: String =
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
      |""".stripMargin

  val cidPersonDetailsResponseJson: String =
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
      |    "nino" : "TW189213B",
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

  val testAddressLookupRequest: AddressLookupRequest = AddressLookupRequest(
    postcode = "AA1 1BB",
    filter = Some("filter")
  )

  val testAddressLookupResponseModel : AddressLookupResponse = AddressLookupResponse (
    id = "1234567890",
    uprn = 246810,
    parentUprn = Some(1234567890),
    usrn = Some(987654321),
    organisation = Some("Capgemini"),
    address =
      AlfAddress(
        lines = Seq("99"+"Wibble Rd"),
        town= "Worthing",
        postcode ="BN110AA",
        subdivision = Some(Subdivision(
          code = "code",
          name = "name"
        )),
        country = Subdivision(
          code = "GB",
          name = "Great Britain"
        )),
    localCustodian = Some(LocalCustodian(
      code = 123,
      name = "LcName"
    )),
    location = Some(Seq(1,2,3)),
    language = "English",
    administrativeArea = Some("AdminArea"),
    poBox = Some("PO321")
  )

  val addressLookupResponseJson : String =
    """
      |{
      |  "id": "1234567890",
      |  "uprn": 246810,
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
      |  "location": [1, 2, 3],
      |  "language": "English",
      |  "administrativeArea": "AdminArea",
      |  "poBox": "PO321"
      |}
      |""".stripMargin


  val invalidAddressLookupResponseJson : String =
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
      |  "location": [1, 2, 3],
      |  "language": "English",
      |  "administrativeArea": "AdminArea",
      |  "poBox": "PO321"
      |}
      |""".stripMargin

  val gnapToken = "i16lTUCYVVcwAEDOtbZNyly2wwgJ"

  val tokenAttributesResponseJson: String =
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

}
