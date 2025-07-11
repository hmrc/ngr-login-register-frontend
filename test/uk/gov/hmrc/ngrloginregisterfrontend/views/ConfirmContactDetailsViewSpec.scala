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

package uk.gov.hmrc.ngrloginregisterfrontend.views

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.mockito.MockitoSugar.mock
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import uk.gov.hmrc.ngrloginregisterfrontend.controllers.ConfirmContactDetailsController
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.{TestData, ViewBaseSpec}
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.{CredId, RatepayerRegistrationValuation}
import uk.gov.hmrc.ngrloginregisterfrontend.utils.SummaryListHelper
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.components.saveAndContinueButton
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.{ConfirmContactDetailsView, Layout}

class ConfirmContactDetailsViewSpec extends ViewBaseSpec with TestData with SummaryListHelper {

  val layout: Layout = MockitoSugar.mock[Layout]
  val button: saveAndContinueButton = mock[saveAndContinueButton]
  val injectedView: ConfirmContactDetailsView = injector.instanceOf[ConfirmContactDetailsView]
  val summaryList: SummaryList = createContactDetailSummaryRows(RatepayerRegistrationValuation(CredId("12345"), Some(testRegistrationModel)), confirmContactDetailsMode)
  lazy val controller: ConfirmContactDetailsController = inject[ConfirmContactDetailsController]

  val navTitle = "Manage your business rates valuation"
  val pageTitle = "Confirm your contact details"
  val body1 = "The Valuation Office Agency (VOA) will use these details to:"
  val bullet1 = "send you information related to the service and your account"
  val bullet2 = "confirm your identity if you contact the VOA"
  val body2 = "This account is registered to name"
  val contactName = "Contact name"
  val emailAddress = "Email address"
  val phoneNumber = "Phone number"
  val address = "Address We will send letters to this address"
  val change = "Change"
  val continue = "Confirm and continue"

  object Selectors {
    val navTitle = ".govuk-header__service-name"
    val pageTitle = "#main-content > div > div > form > h1"
    val body1 = "#main-content > div > div > form > p:nth-child(3)"
    val body2 = "#main-content > div > div > form > p:nth-child(5)"
    val bullet1 = "#main-content > div > div > form > ul > li:nth-child(1)"
    val bullet2 = "#main-content > div > div > form > ul > li:nth-child(2)"
    val contactName = "#main-content > div > div > form > dl > div:nth-child(1) > dt"
    val email = "#main-content > div > div > form > dl > div:nth-child(2) > dt"
    val phone = "#main-content > div > div > form > dl > div:nth-child(3) > dt"
    val address = "#main-content > div > div > form > dl > div:nth-child(4) > dt"
    val continue = "#continue"
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    mockConfig.features.welshLanguageSupportEnabled(false)
  }

  "ConfirmContactDetailsView" when {

    "produce the same output for apply() and render()" in {
      val htmlApply = injectedView.apply(summaryList, "name").body
      val htmlRender = injectedView.render(summaryList, "name", request, messages, mockConfig).body
      val htmlF = injectedView.f(summaryList, "name")(request, messages, mockConfig).body
      htmlApply mustBe htmlRender
      htmlF must not be empty
    }

    "injected into the view" should {

      "render the correct page title" in {
        lazy val view = injectedView(summaryList, "name")
        lazy implicit val document: Document = Jsoup.parse(view.body)
        elementText(Selectors.navTitle) mustBe navTitle
        elementText(Selectors.pageTitle) mustBe pageTitle
        elementText(Selectors.body1) mustBe body1
        elementText(Selectors.body2) mustBe body2
        elementText(Selectors.bullet1) mustBe bullet1
        elementText(Selectors.bullet2) mustBe bullet2
        elementText(Selectors.contactName) mustBe contactName
        elementText(Selectors.email) mustBe emailAddress
        elementText(Selectors.phone) mustBe phoneNumber
        elementText(Selectors.address) mustBe address
        elementText(Selectors.continue) mustBe continue
      }
    }

  }

}
