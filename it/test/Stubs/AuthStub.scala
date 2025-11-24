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

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, equalToJson, post, stubFor, urlPathEqualTo}
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.libs.json.Json
import play.api.test.FakeRequest
import uk.gov.hmrc.ngrloginregisterfrontend.models.RatepayerRegistration
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.{Email, Name, Nino}
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.{CredId, RatepayerRegistrationValuationRequest}

object AuthStub {

  private val authorisedWithEverything: String =
    """{
      |  "authorise": [
      |    {
      |      "confidenceLevel": 250
      |    }
      |  ],
      |  "retrieve": [
      |    "optionalCredentials",
      |    "nino",
      |    "confidenceLevel",
      |    "email",
      |    "affinityGroup",
      |    "optionalName"
      |  ]
      |}""".stripMargin

  private val authorisedWithInsufficientConfidenceLevel: String =
    """{
      |  "authorise": [
      |    {
      |      "confidenceLevel": 200
      |    }
      |  ]
      |}""".stripMargin



  def authorised: StubMapping =
    stubFor(
      post(urlPathEqualTo("/auth/authorise"))
        .withRequestBody(equalToJson(authorisedWithEverything))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(
              """{
                |  "optionalCredentials": { "providerId": "12345", "providerType": "GovernmentGateway" },
                |  "nino": "AA000003D",
                |  "confidenceLevel": 250,
                |  "email": "test@test.co.uk",
                |  "affinityGroup": "Individual",
                |  "optionalName": { "name": "Joe Bloggs" }
                |}""".stripMargin
            )
        )
    )

  def insufficientConfidenceLevel: StubMapping =
    stubFor(
      post(urlPathEqualTo("/auth/authorise"))
        .withRequestBody(equalToJson(authorisedWithInsufficientConfidenceLevel,false,true))
        .willReturn(
          aResponse()
            .withStatus(401)
            .withHeader("WWW-Authenticate", """MDTP detail="InsufficientConfidenceLevel"""")
        )
    )


}
