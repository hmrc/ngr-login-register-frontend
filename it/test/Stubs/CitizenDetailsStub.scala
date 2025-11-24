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

package Stubs

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, equalToJson, get, post, stubFor, urlPathEqualTo}
import com.github.tomakehurst.wiremock.stubbing.StubMapping

object CitizenDetailsStub {

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

  val cidMatchingDetailsResponseNoUtrJson: String =
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


  def designatoryDetails: StubMapping =
    stubFor(
      get(urlPathEqualTo("/citizen-details/AA000003D/designatory-details"))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withBody(cidPersonDetailsResponseJson)
        )
    )

  def matchingStub: StubMapping =
    stubFor(
      get(urlPathEqualTo("/citizen-details/nino/AA000003D"))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withBody(cidMatchingDetailsResponseJson)
        )
    )

  def matchingStubNoUtr: StubMapping =
    stubFor(
      get(urlPathEqualTo("/citizen-details/nino/AA000003D"))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withBody(cidMatchingDetailsResponseNoUtrJson)
        )
    )


}
