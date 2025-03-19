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
import org.mockito.Mockito.when
import play.api.data.format.Formatter
import play.api.http.Status._
import play.api.mvc.{ActionBuilder, AnyContent, AnyContentAsFormUrlEncoded, BodyParser, Request, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, status}
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.http.HeaderNames
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.CitizenDetailsConnector
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrloginregisterfrontend.models.{AuthenticatedUserRequest, ErrorResponse, SaUtr}
import uk.gov.hmrc.ngrloginregisterfrontend.models.cid.MatchingDetails
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.ConfirmUTR
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.ConfirmUTR.{NoLater, NoNI, Yes}
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.ConfirmUTRView

import scala.concurrent.{ExecutionContext, Future}

class ConfirmUTRControllerSpec extends ControllerSpecSupport {

  val view: ConfirmUTRView = inject[ConfirmUTRView]
  val mockCIDConnector: CitizenDetailsConnector = mock[CitizenDetailsConnector]
  def controller =  new ConfirmUTRController(view, mockAuthJourney, mockCIDConnector, mcc)
  val matchingDetails: MatchingDetails = MatchingDetails("bob", "jones", Some(SaUtr("1234567890")))
  val matchingDetailsNoUTR: MatchingDetails = MatchingDetails("bob", "jones", None)
  val pageTitle: String = "Confirm your Self Assessment Unique Taxpayer Reference"

  def requestWithFormValue(value: String): AuthenticatedUserRequest[AnyContentAsFormUrlEncoded] = AuthenticatedUserRequest(FakeRequest(routes.ConfirmUTRController.submit).withFormUrlEncodedBody((ConfirmUTR.formName, value)).withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino = true, Some("")))

  "ConfirmUTRController" must {
    "Return OK and the correct view" in {
      when(mockCIDConnector.getMatchingResponse(any())(any()))
        .thenReturn(Future.successful(Right(matchingDetails)))
      val result = controller.show()(authenticatedFakeRequest)
      status(result) mustBe OK
      val content = contentAsString(result)
      content must include(pageTitle)
    }
    "Return OK and the correct view no UTR" in {
      when(mockCIDConnector.getMatchingResponse(any())(any()))
        .thenReturn(Future.successful(Right(matchingDetailsNoUTR)))
      val exception = intercept[RuntimeException] {
        controller.show()(authenticatedFakeRequest).futureValue
      }
      exception.getMessage must include("No SAUTR found")
    }
    "CID call failed" in {
      when(mockCIDConnector.getMatchingResponse(any())(any()))
        .thenReturn(Future.successful(Left(ErrorResponse(404, "help"))))
      val exception = intercept[RuntimeException] {
        controller.show()(authenticatedFakeRequest).futureValue
      }
      exception.getMessage must include("call to citizen details failed: 404 help")
    }
    "no nino in request" in {
      when(mockAuthJourney.authWithUserDetails) thenReturn new ActionBuilder[AuthenticatedUserRequest, AnyContent] {
        override def invokeBlock[A](request: Request[A], block: AuthenticatedUserRequest[A] => concurrent.Future[Result]): concurrent.Future[Result] =  {
          val authRequest = AuthenticatedUserRequest(request, None, None, None, None, None, None, nino = Nino(hasNino = false, None))
          block(authRequest)
        }
        override def parser: BodyParser[AnyContent] = mcc.parsers.defaultBodyParser
        override protected def executionContext: ExecutionContext = ec
      }
      when(mockCIDConnector.getMatchingResponse(any())(any()))
        .thenReturn(Future.successful(Right(matchingDetails)))
      val exception = intercept[RuntimeException] {
        controller.show()(authenticatedFakeRequest).futureValue
      }
      exception.getMessage must include("No NINO found in request")
    }
    "method submit" must {
      "Successfully submit valid selection and redirect, YES" in {
        val result = controller.submit()(requestWithFormValue("Yes(1234567890)"))
        status(result) mustBe SEE_OTHER
      }
      "Successfully submit valid selection and redirect, NoNI" in {
        val result = controller.submit()(requestWithFormValue("NoNI"))
        status(result) mustBe SEE_OTHER
      }
      "Successfully submit valid selection and redirect, NoLater" in {
        val result = controller.submit()(requestWithFormValue("NoLater"))
        status(result) mustBe SEE_OTHER
      }
      "no selection" in {
          val result = controller.submit()(requestWithFormValue(""))
          status(result) mustBe BAD_REQUEST
          val content = contentAsString(result)
          content must include(pageTitle)
          content must include("Please select an option")
      }
    }

    "unbind a ConfirmUTR value correctly" in {
      val formatter: Formatter[ConfirmUTR] = ConfirmUTR.confirmUTRFormatter
      val yes = Yes("1234567890")
      formatter.unbind(ConfirmUTR.formName, yes) mustBe Map(ConfirmUTR.formName -> yes.toString)
      formatter.unbind(ConfirmUTR.formName, NoNI) mustBe Map(ConfirmUTR.formName -> NoNI.toString)
      formatter.unbind(ConfirmUTR.formName, NoLater) mustBe Map(ConfirmUTR.formName -> NoLater.toString)
    }
  }
}
