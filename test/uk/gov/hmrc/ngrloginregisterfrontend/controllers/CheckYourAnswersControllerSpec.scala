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

package uk.gov.hmrc.ngrloginregisterfrontend.controllers

import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchersSugar.eqTo
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.http.Status.{CREATED, INTERNAL_SERVER_ERROR, OK, SEE_OTHER}
import play.api.mvc.Results
import play.api.test.Helpers.{defaultAwaitTimeout, redirectLocation, status}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.{ControllerSpecSupport, TestData}
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.ReferenceType.{NINO, SAUTR}
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.{RatepayerRegistrationValuation, TRNReferenceNumber}
import uk.gov.hmrc.ngrloginregisterfrontend.utils.SummaryListHelper
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.CheckYourAnswersView

import scala.concurrent.Future

class CheckYourAnswersControllerSpec extends ControllerSpecSupport with TestData with SummaryListHelper{
  lazy val view: CheckYourAnswersView = inject[CheckYourAnswersView]
  lazy val ratepayer: RatepayerRegistrationValuation = RatepayerRegistrationValuation(credId)

  def controller() = new CheckYourAnswersController(
    view,
    mockIsRegisteredCheck,
    mockHasMandotoryDetailsAction,
    mockRatepayerRegistraionRepo,
    mockAuthJourney,
    mockNGRConnector,
    mcc)

  "Controller" must {
    "return OK and the correct view for a GET" in {
      when(mockRatepayerRegistraionRepo.findByCredId(any())).thenReturn(Future.successful(Some(ratepayer)))
      val result = controller().show()(authenticatedFakeRequest)
      status(result) mustBe OK
    }
//    "will create summary rows from ratepayer registration model" in {
//      val ratepayer = RatepayerRegistrationValuation(credId, Some(testRegistrationModel))
//      val summaryList = createContactDetailSummaryRows(ratepayer, checkYourAnswersMode)
//      val rows: Seq[SummaryListRow] = summaryList.rows
//      rows.length shouldBe 4
//      rows(0).value.content.toString must include("John Doe")
//      rows(0).actions.get.items(0).href shouldBe s"/ngr-login-register-frontend/name?mode=$checkYourAnswersMode"
//      rows(1).value.content.toString must include("JohnDoe@digital.hmrc.gov.uk")
//      rows(1).actions.get.items(0).href shouldBe s"/ngr-login-register-frontend/change-email?mode=$checkYourAnswersMode"
//      rows(2).value.content.toString must include("07123456789")
//      rows(2).actions.get.items(0).href shouldBe s"/ngr-login-register-frontend/phone-number?mode=$checkYourAnswersMode"
//      rows(3).value.content.toString must include("99</br>Wibble Rd</br>Worthing</br>BN110AA")
//      rows(3).actions.get.items(0).href shouldBe s"/ngr-login-register-frontend/find-address?mode=$checkYourAnswersMode"
//    }

    "Tax reference row will show provide your UTR when referenceType is TRN" in {
      val ratepayer = RatepayerRegistrationValuation(credId, Some(testRegistrationModel.copy(trnReferenceNumber = None)))
      val summaryList = controller().createTRNSummaryRows(ratepayer)
      summaryList.rows.length shouldBe 1
      summaryList.rows(0).value.content.toString must include("<a id=\"sautr-linkid\" href=\"/ngr-login-register-frontend/confirm-utr\" class=\"govuk-link\">Provide your UTR</a>")
    }

    "Tax reference row will show provide your UTR when referenceType is SAUTR but no value" in {
      val model = testRegistrationModel.copy(trnReferenceNumber = Some(TRNReferenceNumber(SAUTR, "")))
      val ratepayer = RatepayerRegistrationValuation(credId, Some(model))
      val summaryList = controller().createTRNSummaryRows(ratepayer)
      summaryList.rows.length shouldBe 1
      summaryList.rows(0).value.content.toString must include("<a id=\"sautr-linkid\" href=\"/ngr-login-register-frontend/confirm-utr\" class=\"govuk-link\">Provide your UTR</a>")
    }

    "Tax reference row will show masked SAUTR value when referenceType is SAUTR and value has been provided" in {
      val model = testRegistrationModel.copy(trnReferenceNumber = Some(TRNReferenceNumber(SAUTR, "23456789")))
      val ratepayer = RatepayerRegistrationValuation(credId, Some(model))
      val summaryList = controller().createTRNSummaryRows(ratepayer)
      summaryList.rows.length shouldBe 1
      summaryList.rows(0).value.content.toString must include("*****789")
    }

    "Tax reference row will show masked NINO value when referenceType is NINO and value has been provided" in {
      val model = testRegistrationModel.copy(trnReferenceNumber = Some(TRNReferenceNumber(NINO, "QQ 12 34 56 C")))
      val ratepayer = RatepayerRegistrationValuation(credId, Some(model))
      val summaryList = controller().createTRNSummaryRows(ratepayer)
      summaryList.rows.length shouldBe 1
      summaryList.rows(0).value.content.toString must include("******56C")
    }

    "Calling the submit function return a 303 and the correct redirect location" in {
      mockRequest()
      val httpResponse = HttpResponse(CREATED, "Created Successfully")
      when(mockNGRConnector.upsertRatepayer(any())(any())).thenReturn(Future.successful(httpResponse))
      when(mockRatepayerRegistraionRepo.deleteRecord(any())).thenReturn(Future.successful(Results.Ok))
      val result = controller().submit()(authenticatedFakeRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.RegistrationCompleteController.show(Some("234567")).url)
    }

    "Calling the submit function return a exception when failing to upsert to backend" in {
      mockRequest()
      val httpResponse = HttpResponse(INTERNAL_SERVER_ERROR, "Failed upsert to backend")
      when(mockNGRConnector.upsertRatepayer(any())(any())).thenReturn(Future.successful(httpResponse))
      val exception = intercept[Exception] {
        controller().submit()(authenticatedFakeRequest).futureValue
      }
      exception.getMessage must include("Failed upsert to backend")
    }

    "Calling the submit function return a exception when failing to drop the frontend mongo" in {
      mockRequest()
      val httpResponse = HttpResponse(CREATED, "Created Successfully")
      when(mockNGRConnector.upsertRatepayer(any())(any())).thenReturn(Future.successful(httpResponse))
      val mongoFailure = new RuntimeException("mongo delete failed")
      when(mockRatepayerRegistraionRepo.deleteRecord(eqTo(credId)))
        .thenReturn(Future.failed(mongoFailure))
      val exception = intercept[Exception] {
        controller().submit()(authenticatedFakeRequest).futureValue
      }
      exception.getMessage must include("Upsert succeeded, but failed to delete mongo record for")
    }
  }
}
