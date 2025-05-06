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
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers.{defaultAwaitTimeout, redirectLocation, status}
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.ngrloginregisterfrontend.actions.RegistrationActionSpec
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.CitizenDetailsConnector
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.{ControllerSpecSupport, TestData}
import uk.gov.hmrc.ngrloginregisterfrontend.models.cid.{Person, PersonAddress, PersonDetails}
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.RatepayerRegistrationValuation
import uk.gov.hmrc.ngrloginregisterfrontend.models.{AuthenticatedUserRequest, ErrorResponse, RatepayerRegistration}
import uk.gov.hmrc.ngrloginregisterfrontend.utils.SummaryListHelper
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.ConfirmContactDetailsView

import scala.concurrent.Future

class ConfirmContactDetailsControllerSpec extends ControllerSpecSupport with TestData with SummaryListHelper {
  lazy val view: ConfirmContactDetailsView = inject[ConfirmContactDetailsView]
  lazy val citizenDetailsConnector: CitizenDetailsConnector = inject[CitizenDetailsConnector]
  val noNinoAuth: AuthenticatedUserRequest[AnyContentAsEmpty.type] = AuthenticatedUserRequest(fakeRequest, None, None, None, None, None, None, nino = Nino(hasNino = false, None))
  lazy val mockCitizenDetailsConnector: CitizenDetailsConnector = mock[CitizenDetailsConnector]

  def controller() =
    new ConfirmContactDetailsController(
      view = view, authenticate = mockAuthJourney, isRegisteredCheck = mockIsRegisteredCheck, mcc = mcc, citizenDetailsConnector = mockCitizenDetailsConnector, connector = mockNGRConnector
    )

  override def beforeEach(): Unit = {
    super.beforeEach()
    mockRequest()
    val registration: RatepayerRegistration = RatepayerRegistration()
    val ratepayer: RatepayerRegistrationValuation = RatepayerRegistrationValuation(credId, Some(registration))
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

    "return the correct status when no ratepayer is found and citizen details fail" in {
      when(mockNGRConnector.getRatepayer(any())(any())).thenReturn(Future.successful(None))
      when(mockCitizenDetailsConnector.getPersonDetails(any())(any())).thenReturn(Future.successful(Left(ErrorResponse(404, "Not Found"))))

      val result = controller().show(Some("email@email.com"))(fakeRequest)
      status(result) mustBe 404
    }

    "person details returns error status" in {
      when(mockCitizenDetailsConnector.getPersonDetails(any())(any())).thenReturn(Future(Left(ErrorResponse(200, "bad"))))
      val result = controller().show()(authenticatedFakeRequest)
      status(result) mustBe 200
    }

    "throw exception when nino is not found from auth" in {
      mockRequest(hasNino = false)
      val exception = intercept[RuntimeException] {
        controller().show(Some("email@email.com"))(authenticatedFakeRequest).futureValue
      }
      exception.getMessage mustBe "No nino found from auth"
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

      val result = controller().show(Some("email@email.com"))(fakeRequest)
      status(result) mustBe OK
    }

    "will create summary rows from ratepayer registration model correctly" in {
      val ratepayer = RatepayerRegistrationValuation(credId, Some(testRegistrationModel))
      val summaryList = createContactDetailSummaryRows(ratepayer, confirmContactDetailsMode)
      val rows: Seq[SummaryListRow] = summaryList.rows
      rows.length shouldBe 4
      rows(0).value.content.toString must include("John Doe")
      rows(0).actions.get.items(0).href shouldBe s"/ngr-login-register-frontend/name?mode=$confirmContactDetailsMode"
      rows(1).value.content.toString must include("JohnDoe@digital.hmrc.gov.uk")
      rows(1).actions.get.items(0).href shouldBe s"/ngr-login-register-frontend/change-email?mode=$confirmContactDetailsMode"
      rows(2).value.content.toString must include("07123456789")
      rows(2).actions.get.items(0).href shouldBe s"/ngr-login-register-frontend/phone-number?mode=$confirmContactDetailsMode"
      rows(3).value.content.toString must include("99</br>Wibble Rd</br>Worthing</br>BN110AA")
      rows(3).actions.get.items(0).href shouldBe s"/ngr-login-register-frontend/find-address?mode=$confirmContactDetailsMode"
    }

    "will create summary rows from ratepayer registration model correctly with add link for phone number" in {
      val ratepayer = RatepayerRegistrationValuation(credId, Some(testRegistrationModel.copy(contactNumber = None)))
      val summaryList = createContactDetailSummaryRows(ratepayer, confirmContactDetailsMode)
      val rows: Seq[SummaryListRow] = summaryList.rows
      rows.length shouldBe 4
      rows(0).value.content.toString must include("John Doe")
      rows(1).value.content.toString must include("JohnDoe@digital.hmrc.gov.uk")
      rows(2).value.content.toString must include(s"<a id=\"number-linkid\" href=\"/ngr-login-register-frontend/phone-number?mode=$confirmContactDetailsMode\" class=\"govuk-link\">Add</a>")
      rows(3).value.content.toString must include("99</br>Wibble Rd</br>Worthing</br>BN110AA")
    }

    "Calling the submit function return a 303 and the correct redirect location" in {
      val result = controller().submit()(authenticatedFakeRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.ProvideTRNController.show().url)
    }

    "no email redirects to enter email" in {
      mockRequest(authenticatedFakeRequest)
      val result = controller().show()(authenticatedFakeRequest)
      redirectLocation(result) shouldBe Some(routes.EnterEmailController.show.url)
    }

    "manual email should render" in {
      mockRequest(authenticatedFakeRequest)
      when(mockNGRConnector.changeEmail(any(), any())(any())).thenReturn(Future.successful(HttpResponse(CREATED, "Created Successfully")))
      val result = controller().show(Some("email@email.com"))(authenticatedFakeRequest)
      status(result) mustBe OK
    }
  }
}
