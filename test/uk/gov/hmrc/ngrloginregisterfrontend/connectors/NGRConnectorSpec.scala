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

package uk.gov.hmrc.ngrloginregisterfrontend.connectors

import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.TestData
import uk.gov.hmrc.ngrloginregisterfrontend.mocks.MockHttpV2
import uk.gov.hmrc.ngrloginregisterfrontend.models.RatepayerRegistration
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.Email
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.ReferenceType.TRN
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.{RatepayerRegistrationValuation, TRNReferenceNumber}
import uk.gov.hmrc.ngrloginregisterfrontend.utils.NGRLogger

import scala.concurrent.Future

class NGRConnectorSpec extends MockHttpV2 with TestData {
  val logger: NGRLogger = inject[NGRLogger]
  val ngrConnector: NGRConnector = new NGRConnector(mockHttpClientV2, mockConfig, logger)
  val email: Email = Email("hello@me.com")
  val trn: TRNReferenceNumber = TRNReferenceNumber(TRN, "1234")

    "upsertRatepayer" when {
      "return HttpResponse when the response is 201 CREATED" in {
        val ratepayer: RatepayerRegistration = RatepayerRegistration()
        val model: RatepayerRegistrationValuation = RatepayerRegistrationValuation(credId, Some(ratepayer))
        val response: HttpResponse = HttpResponse(201, "Created")
        setupMockHttpV2Post(s"${mockConfig.nextGenerationRatesUrl}/next-generation-rates/upsert-ratepayer")(response)
        val result: Future[HttpResponse] = ngrConnector.upsertRatepayer(model)
        result.futureValue.status mustBe 201
      }

      "throw an exception when response is not 201" in {
        val ratepayer: RatepayerRegistration = RatepayerRegistration()
        val model: RatepayerRegistrationValuation = RatepayerRegistrationValuation(credId, Some(ratepayer))
        val response: HttpResponse = HttpResponse(400, "Bad Request")

        setupMockHttpV2Post(s"${mockConfig.nextGenerationRatesUrl}/next-generation-rates/upsert-ratepayer")(response)

        val exception = intercept[Exception] {
          ngrConnector.upsertRatepayer(model).futureValue
        }
        exception.getMessage must include("400: Bad Request")
      }

      "propagate exception when the request fails" in {
        val ratepayer: RatepayerRegistration = RatepayerRegistration()
        val model: RatepayerRegistrationValuation = RatepayerRegistrationValuation(credId, Some(ratepayer))

        setupMockHttpV2FailedPost(s"${mockConfig.nextGenerationRatesUrl}/next-generation-rates/upsert-ratepayer")
        val exception = intercept[RuntimeException] {
          ngrConnector.upsertRatepayer(model).futureValue
        }
        exception.getMessage must include("Request Failed")
      }
    }

  "getRatepayer" when {
    "Successfully return a Ratepayer" in {
      val ratepayer: RatepayerRegistration = RatepayerRegistration()
      val response: RatepayerRegistrationValuation = RatepayerRegistrationValuation(credId, Some(ratepayer))
      setupMockHttpV2Get(s"${mockConfig.nextGenerationRatesUrl}/next-generation-rates/get-ratepayer")(Some(response))
      val result: Future[Option[RatepayerRegistrationValuation]] = ngrConnector.getRatepayer(credId)
      result.futureValue.get.credId mustBe credId
      result.futureValue.get.ratepayerRegistration mustBe Some(ratepayer)
    }
    "ratepayer not found" in {
      setupMockHttpV2Get(s"${mockConfig.nextGenerationRatesUrl}/next-generation-rates/get-ratepayer")(None)
      val result: Future[Option[RatepayerRegistrationValuation]] = ngrConnector.getRatepayer(credId)
      result.futureValue mustBe None
    }
  }
}


