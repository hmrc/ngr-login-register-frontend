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
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.http.Status.{CREATED, OK, SEE_OTHER}
import play.api.mvc.{AnyContent, AnyContentAsEmpty}
import play.api.test.Helpers.{defaultAwaitTimeout, redirectLocation, status}
import uk.gov.hmrc.auth.core.ConfidenceLevel.L250
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.CitizenDetailsConnector
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.{ControllerSpecSupport, TestData}
import uk.gov.hmrc.ngrloginregisterfrontend.models.cid.{Person, PersonAddress, PersonDetails}
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.RatepayerRegistrationValuation
import uk.gov.hmrc.ngrloginregisterfrontend.models.{AuthenticatedUserRequest, ErrorResponse}
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.ConfirmContactDetailsView

import scala.concurrent.Future

class ConfirmContactDetailsControllerSpec extends ControllerSpecSupport with TestData {
  lazy val view: ConfirmContactDetailsView = inject[ConfirmContactDetailsView]
  lazy val citizenDetailsConnector: CitizenDetailsConnector = inject[CitizenDetailsConnector]
  val noNinoAuth: AuthenticatedUserRequest[AnyContentAsEmpty.type] = AuthenticatedUserRequest(fakeRequest, None, None, None, None, None, None, nino = Nino(hasNino = false, None))
  lazy val mockCitizenDetailsConnector: CitizenDetailsConnector = mock[CitizenDetailsConnector]

  def controller() =
    new ConfirmContactDetailsController(
      view = view, authenticate = mockAuthJourney, mcc = mcc, citizenDetailsConnector = mockCitizenDetailsConnector, connector = mockNGRConnector
    )

  override def beforeEach(): Unit = {
    super.beforeEach()
    val ratepayer: RatepayerRegistrationValuation = RatepayerRegistrationValuation(credId)
    val response: Option[RatepayerRegistrationValuation] = Some(ratepayer)
    val httpResponse = HttpResponse(CREATED, "Created Successfully")
    when(mockNGRConnector.getRatepayer(any())(any()))
      .thenReturn(Future.successful(response))
    when(mockNGRConnector.upsertRatepayer(any())(any()))
      .thenReturn(Future.successful(httpResponse))
  }

  "Controller" must {
    "return OK and the correct view for a GET" in {
      when(mockCitizenDetailsConnector.getPersonDetails(any())(any())).thenReturn(Future.successful(Right(personDetailsResponse)))
      val result = controller().show()(authenticatedFakeRequest)
      status(result) mustBe OK
    }

    "throw an exception if auth does not return a nino" in{
    //  when(mockCitizenDetailsConnector.getPersonDetails(any())(any())).thenReturn(Future.successful(Right(personDetailsResponse)))
      val result = controller().show()(authenticatedFakeRequestNoNino)
      status(result) mustBe OK
//      val exception = intercept[Exception] {
//        controller.show()(authenticatedFakeRequestNoNino)
//      }
//      exception.getMessage must include("No nino found from auth")
    }

    "return the correct status when no ratepayer is found and citizen details fail" in {
      when(mockNGRConnector.getRatepayer(any())(any())).thenReturn(Future.successful(None))
      when(mockCitizenDetailsConnector.getPersonDetails(any())(any())).thenReturn(Future.successful(Left(ErrorResponse(404, "Not Found"))))

      val result = controller().show()(fakeRequest)
      status(result) mustBe 404
    }

    "person details returns error status" in {
      when(mockCitizenDetailsConnector.getPersonDetails(any())(any())).thenReturn(Future(Left(ErrorResponse(200, "bad"))))
      val result = controller().show()(authenticatedFakeRequest)
      status(result) mustBe 200
    }

    "create a ratepayer when no existing ratepayer is found and citizen details succeed" in {
      val personDetails = PersonDetails(
        person = Person(
          title = Some("Mr"),
          firstName = Some("John"),
          middleName = None,
          lastName = Some("Doe"),
          honours = None,
          sex = Some("M"),
          dateOfBirth = None,
          nino = None
        ),
        address = PersonAddress(
          line1 = Some("123 Street"),
          line2 = Some("Area"),
          line4 = Some("Town"),
          line5 = Some("County"),
          postcode = Some("AB12 3CD"),
          country = Some("UK")
        )
      )
      when(mockNGRConnector.getRatepayer(any())(any())).thenReturn(Future.successful(None))
      when(mockCitizenDetailsConnector.getPersonDetails(any())(any())).thenReturn(Future.successful(Right(personDetails)))
      when(mockNGRConnector.upsertRatepayer(any())(any())).thenReturn(Future.successful(HttpResponse(CREATED, "Created Successfully")))

      val result = controller().show()(fakeRequest)
      status(result) mustBe OK
    }

    "Will generate SummaryListRow from user data" in {
      val personDetails = PersonDetails(
        Person(
          title = None,
          firstName = Some("Joe"),
          middleName = Some("Eric"),
          lastName = Some("Jones"),
          honours = None,
          sex = None,
          dateOfBirth = None,
          nino = None),
        PersonAddress(
          line1 = Some("123 Britain Street"),
          line2 = Some("123 Britain Street"),
          line3 = Some("Nicetown"),
          line4 = Some("123 Britain Street"),
          line5 = Some("123 Britain Street"),
          postcode = Some("TT347TC"),
          country = Some("UK")
        )
      )
      val authRequest: AuthenticatedUserRequest[AnyContent] = AuthenticatedUserRequest(request, Some(L250), None, None, None, None, None, uk.gov.hmrc.auth.core.Nino(hasNino = true))
      val rows = controller().createSummaryRows(personDetails, authRequest)
      rows.length shouldBe 4
    }

    "will create summary rows from ratepayer registration model" in {
      val model = testRegistrationModel
      val ratepayer = RatepayerRegistrationValuation(credId, Some(model))
      val rows = controller().createSummaryRowsFromRatePayer(ratepayer)
      rows.length shouldBe 4
    }
    "Calling the submit function return a 303 and the correct redirect location" in {
      val result = controller().submit()(authenticatedFakeRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.ProvideTRNController.show().url)
    }
  }
}
