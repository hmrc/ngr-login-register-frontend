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

import uk.gov.hmrc.domain.Nino
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

}
