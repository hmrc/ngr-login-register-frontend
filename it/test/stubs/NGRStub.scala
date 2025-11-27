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

package stubs

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, equalToJson, get, stubFor, urlPathEqualTo}
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import stubs.AuthStub.authorisedWithEverything

object NGRStub {

  def getRatePayer: StubMapping =
    stubFor(
      get(urlPathEqualTo("/next-generation-rates/get-ratepayer"))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(
              """{
                |  "_id": {
                |    "$oid": "69271d2e6877bf8420b18a5f"
                |  },
                |  "credId": {
                |    "value": "1234"
                |  },
                |  "ratepayerRegistration": {
                |    "userType": "Individual",
                |    "agentStatus": "Autonomous",
                |    "name": {
                |      "value": "Jake"
                |    },
                |    "tradingName": {
                |      "value": "Jake Ltd."
                |    },
                |    "email": {
                |      "value": "jake.r@jakeltd.com"
                |    },
                |    "nino": "AA000003D",
                |    "contactNumber": {
                |      "value": "07702467839"
                |    },
                |    "address": {
                |      "line1": "Address Line 1",
                |      "town": "Cambridge",
                |      "postcode": {
                |        "value": "CB2 1HW"
                |      }
                |    },
                |    "isRegistered": false
                |  },
                |  "createdAt": {
                |    "$date": "2025-11-26T15:30:55.325Z"
                |  }
                |}""".stripMargin
            )
        )
    )


}
