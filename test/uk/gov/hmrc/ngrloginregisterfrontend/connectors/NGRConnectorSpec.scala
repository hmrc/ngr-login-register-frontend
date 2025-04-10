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

import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.RecoverMethods.recoverToSucceededIf
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.libs.json.Json
import uk.gov.hmrc.http.{HttpResponse, StringContextOps}
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.TestData
import uk.gov.hmrc.ngrloginregisterfrontend.mocks.MockHttpV2
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.ReferenceType.TRN
import uk.gov.hmrc.ngrloginregisterfrontend.models.RatepayerRegistration
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.Email
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.{CredId, RatepayerRegistrationValuation, TRNReferenceNumber}
import uk.gov.hmrc.ngrloginregisterfrontend.utils.NGRLogger
import scala.concurrent.Future

class NGRConnectorSpec extends MockHttpV2 with TestData {
  val logger: NGRLogger = inject[NGRLogger]
  val ngrConnector: NGRConnector = new NGRConnector(mockHttpClientV2, mockConfig, logger)
  val credId: CredId = CredId("1234")
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

  "changePhoneNumber" when {
    "return HttpResponse when the response is OK" in {
      val response: HttpResponse = HttpResponse(200, "Phone number changed")
      setupMockHttpV2Post(s"${mockConfig.nextGenerationRatesUrl}/next-generation-rates/change-phone-number")(response)
      val result: Future[HttpResponse] = ngrConnector.changePhoneNumber(credId, contactNumberModel)
      result.futureValue.status mustBe 200
    }

    "throw an exception when response is not 200" in {
      val response: HttpResponse = HttpResponse(400, "Bad Request")

      setupMockHttpV2Post(s"${mockConfig.nextGenerationRatesUrl}/next-generation-rates/change-phone-number")(response)

      val exception = intercept[Exception] {
        ngrConnector.changePhoneNumber(credId, contactNumberModel).futureValue
      }
      exception.getMessage must include("400: Bad Request")
    }

    "propagate exception when the request fails" in {

      setupMockHttpV2FailedPost(s"${mockConfig.nextGenerationRatesUrl}/next-generation-rates/change-phone-number")
      val exception = intercept[RuntimeException] {
        ngrConnector.changePhoneNumber(credId, contactNumberModel).futureValue
      }
      exception.getMessage must include("Request Failed")
    }
  }

  "changeNino" when {
    "return HttpResponse when the response is OK" in {
      val response: HttpResponse = HttpResponse(200, "Nino changed")
      setupMockHttpV2Post(s"${mockConfig.nextGenerationRatesUrl}/next-generation-rates/change-nino")(response)
      val result: Future[HttpResponse] = ngrConnector.changeNino(credId, ninoModel)
      result.futureValue.status mustBe 200
    }

    "throw an exception when response is not 200" in {
      val response: HttpResponse = HttpResponse(400, "Bad Request")

      setupMockHttpV2Post(s"${mockConfig.nextGenerationRatesUrl}/next-generation-rates/change-nino")(response)

      val exception = intercept[Exception] {
        ngrConnector.changeNino(credId, ninoModel).futureValue
      }
      exception.getMessage must include("400: Bad Request")
    }

    "propagate exception when the request fails" in {

      setupMockHttpV2FailedPost(s"${mockConfig.nextGenerationRatesUrl}/next-generation-rates/change-nino")
      val exception = intercept[RuntimeException] {
        ngrConnector.changeNino(credId, ninoModel).futureValue
      }
      exception.getMessage must include("Request Failed")
    }
  }

  "changeName" when {
    "return HttpResponse when the response is OK" in {
      val response: HttpResponse = HttpResponse(200, "Name changed")
      setupMockHttpV2Post(s"${mockConfig.nextGenerationRatesUrl}/next-generation-rates/change-name")(response)
      val result: Future[HttpResponse] = ngrConnector.changeName(credId, nameModel)
      result.futureValue.status mustBe 200
    }

    "throw an exception when response is not 200" in {
      val response: HttpResponse = HttpResponse(400, "Bad Request")

      setupMockHttpV2Post(s"${mockConfig.nextGenerationRatesUrl}/next-generation-rates/change-name")(response)

      val exception = intercept[Exception] {
        ngrConnector.changeName(credId, nameModel).futureValue
      }
      exception.getMessage must include("400: Bad Request")
    }

    "propagate exception when the request fails" in {

      setupMockHttpV2FailedPost(s"${mockConfig.nextGenerationRatesUrl}/next-generation-rates/change-name")
      val exception = intercept[RuntimeException] {
        ngrConnector.changeName(credId, nameModel).futureValue
      }
      exception.getMessage must include("Request Failed")
    }
  }

  "changeEmail" when {
    "return HttpResponse when the response is OK" in {
      val response: HttpResponse = HttpResponse(200, "Email changed")
      setupMockHttpV2Post(s"${mockConfig.nextGenerationRatesUrl}/next-generation-rates/change-email")(response)
      val result: Future[HttpResponse] = ngrConnector.changeEmail(credId, email)
      result.futureValue.status mustBe 200
    }

    "throw an exception when response is not 200" in {
      val response: HttpResponse = HttpResponse(400, "Bad Request")

      setupMockHttpV2Post(s"${mockConfig.nextGenerationRatesUrl}/next-generation-rates/change-email")(response)

      val exception = intercept[Exception] {
        ngrConnector.changeEmail(credId, email).futureValue
      }
      exception.getMessage must include("400: Bad Request")
    }

    "propagate exception when the request fails" in {

      setupMockHttpV2FailedPost(s"${mockConfig.nextGenerationRatesUrl}/next-generation-rates/change-email")
      val exception = intercept[RuntimeException] {
        ngrConnector.changeEmail(credId, email).futureValue
      }
      exception.getMessage must include("Request Failed")
    }
  }

  "changeTrn" when {
    "return HttpResponse when the response is OK" in {
      val response: HttpResponse = HttpResponse(200, "TRN changed")
      setupMockHttpV2Post(s"${mockConfig.nextGenerationRatesUrl}/next-generation-rates/change-trn")(response)
      val result: Future[HttpResponse] = ngrConnector.changeTrn(credId, trn)
      result.futureValue.status mustBe 200
    }

    "throw an exception when response is not 200" in {
      val response: HttpResponse = HttpResponse(400, "Bad Request")

      setupMockHttpV2Post(s"${mockConfig.nextGenerationRatesUrl}/next-generation-rates/change-trn")(response)

      val exception = intercept[Exception] {
        ngrConnector.changeTrn(credId, trn).futureValue
      }
      exception.getMessage must include("400: Bad Request")
    }

    "propagate exception when the request fails" in {
      setupMockHttpV2FailedPost(s"${mockConfig.nextGenerationRatesUrl}/next-generation-rates/change-trn")
      val exception = intercept[RuntimeException] {
        ngrConnector.changeTrn(credId, trn).futureValue
      }
      exception.getMessage must include("Request Failed")
    }
  }

  "changeAddress" when {
    "return HttpResponse when the response is OK" in {
      val response: HttpResponse = HttpResponse(200, "Address changed")
      setupMockHttpV2Post(s"${mockConfig.nextGenerationRatesUrl}/next-generation-rates/change-address")(response)
      val result: Future[HttpResponse] = ngrConnector.changeAddress(credId, testAddressModel)
      result.futureValue.status mustBe 200
    }

    "throw an exception when response is not 200" in {
      val response: HttpResponse = HttpResponse(400, "Bad Request")

      setupMockHttpV2Post(s"${mockConfig.nextGenerationRatesUrl}/next-generation-rates/change-address")(response)

      val exception = intercept[Exception] {
        ngrConnector.changeAddress(credId, testAddressModel).futureValue
      }
      exception.getMessage must include("400: Bad Request")
    }

    "propagate exception when the request fails" in {
      setupMockHttpV2FailedPost(s"${mockConfig.nextGenerationRatesUrl}/next-generation-rates/change-address")
      val exception = intercept[RuntimeException] {
        ngrConnector.changeAddress(credId, testAddressModel).futureValue
      }
      exception.getMessage must include("Request Failed")
    }
  }

  "findAddress" when {
    "return an address for a successful response" in {
      val responseBody = Json.toJson(testAddressModel).toString()
      val mockResponse = mock[HttpResponse]
      when(mockResponse.status).thenReturn(200)
      when(mockResponse.body).thenReturn(responseBody)
      val url = s"${mockConfig.nextGenerationRatesUrl}/next-generation-rates/find-address"
      when(mockHttpClientV2.post(ArgumentMatchers.eq(url"$url"))(any())).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.withBody(any())(any(), any(), any())).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.execute[HttpResponse](any(), any())).thenReturn(Future.successful(mockResponse))
      val result = ngrConnector.findAddress(credId)
      whenReady(result) { addressOpt =>
        addressOpt shouldBe Some(testAddressModel)
      }
    }

    "return None for an invalid address JSON" in {
      val responseBody = """{"invalid": "data"}"""
      val mockResponse = mock[HttpResponse]

      when(mockResponse.status).thenReturn(200) // OK
      when(mockResponse.body).thenReturn(responseBody)
      val url = s"${mockConfig.nextGenerationRatesUrl}/next-generation-rates/find-address"
      when(mockHttpClientV2.post(ArgumentMatchers.eq(url"$url"))(any())).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.withBody(any())(any(), any(), any())).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.execute[HttpResponse](any(), any())).thenReturn(Future.successful(mockResponse))

      val result = ngrConnector.findAddress(credId)

      whenReady(result) { addressOpt =>
        addressOpt shouldBe None
      }
    }

    "throw an exception for a non-OK response" in {
      val credId = CredId("test-cred-id")
      val mockResponse = mock[HttpResponse]

      when(mockResponse.status).thenReturn(404) // Not Found
      when(mockResponse.body).thenReturn("Not Found")
      val url = s"${mockConfig.nextGenerationRatesUrl}/next-generation-rates/find-address"
      when(mockHttpClientV2.post(ArgumentMatchers.eq(url"$url"))(any())).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.withBody(any())(any(), any(), any())).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.execute[HttpResponse](any(), any())).thenReturn(Future.successful(mockResponse))

      val result = ngrConnector.findAddress(credId)

      recoverToSucceededIf[Exception] {
        result
      }
    }

    "handle exceptions during HTTP request" in {
      val credId = CredId("test-cred-id")

      val url = s"${mockConfig.nextGenerationRatesUrl}/next-generation-rates/find-address"
      when(mockHttpClientV2.post(ArgumentMatchers.eq(url"$url"))(any())).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.withBody(any())(any(), any(), any())).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.execute[HttpResponse](any(), any())).thenReturn(Future.failed(new RuntimeException("Find address failed")))
      val result = ngrConnector.findAddress(credId)
      recoverToSucceededIf[RuntimeException] {
        result
      }
    }
  }

  "isRegistered" when {


    "return true for a successful response" in {
      val responseBody = """{"status": "OK"}""" // Example successful response
      val mockResponse = mock[HttpResponse]
      when(mockResponse.status).thenReturn(200) // OK
      val url = s"${mockConfig.nextGenerationRatesUrl}/next-generation-rates/register-account"
      when(mockHttpClientV2.post(ArgumentMatchers.eq(url"$url"))(any())).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.withBody(any())(any(), any(), any())).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.execute[HttpResponse](any(), any())).thenReturn(Future.successful(mockResponse))
      when(mockResponse.body).thenReturn(responseBody)
      val result = ngrConnector.registerAccount(credId)
      whenReady(result) { registered =>
        registered shouldBe true
      }
    }

    "throw an exception for a non-OK response" in {
      val mockResponse = mock[HttpResponse]

      when(mockResponse.status).thenReturn(404) // Not Found
      val url = s"${mockConfig.nextGenerationRatesUrl}/next-generation-rates/register-account"
      when(mockHttpClientV2.post(ArgumentMatchers.eq(url"$url"))(any())).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.withBody(any())(any(), any(), any())).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.execute[HttpResponse](any(), any())).thenReturn(Future.successful(mockResponse))

      val result = ngrConnector.registerAccount(credId)

      recoverToSucceededIf[Exception] {
        result
      }
    }
  }





}


