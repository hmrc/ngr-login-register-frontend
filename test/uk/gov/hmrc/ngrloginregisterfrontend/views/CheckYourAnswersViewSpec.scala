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
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import uk.gov.hmrc.ngrloginregisterfrontend.controllers.routes
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.{TestData, ViewBaseSpec}
import uk.gov.hmrc.ngrloginregisterfrontend.models.NGRSummaryListRow.summarise
import uk.gov.hmrc.ngrloginregisterfrontend.models.{Link, NGRSummaryListRow}
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.{CredId, RatepayerRegistrationValuation}
import uk.gov.hmrc.ngrloginregisterfrontend.utils.SummaryListHelper
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.components.saveAndContinueButton
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.{CheckYourAnswersView, Layout}

class CheckYourAnswersViewSpec extends ViewBaseSpec with TestData with SummaryListHelper {

  val layout: Layout = MockitoSugar.mock[Layout]
  val button: saveAndContinueButton = mock[saveAndContinueButton]
  val injectedView: CheckYourAnswersView = injector.instanceOf[CheckYourAnswersView]
  lazy val contactDetailsSummaryList: SummaryList = createContactDetailSummaryRows(RatepayerRegistrationValuation(CredId("12345"), Some(testRegistrationModel)), checkYourAnswersMode)
  lazy val trnSummaryList: SummaryList = SummaryList(Seq(summarise(NGRSummaryListRow("Self Assessment Unique Taxpayer Reference", None, Seq.empty, Some(Link(Call("GET", routes.ConfirmUTRController.show.url), "sautr-linkid", provideTRN))))))

  val heading = "Register for the business rates valuation service"
  val backLink = "Back"
  val pageTitle = "Check your answers"
  val body1 = "Contact details"
  val body1ContentTitle = "This account is registered to name"
  val body2 = "Tax reference number"
  val contactName = "Contact name"
  val emailAddress = "Email address"
  val phoneNumber = "Phone number"
  val address = "Address We will send letters to this address"
  val sautr = "Self Assessment Unique Taxpayer Reference"
  val provideTRN = "Provide your TRN"
  val change = "Change"
  val body3 ="Register to use this service"
  val body3Content = "By registering for this service you are confirming that, to the best of your knowledge, the details you are providing are correct."
  val continue = "Accept and register"

  object Selectors {
    val backLink = "body > form > div > a"
    val heading = "#main-content > div > div > form > span"
    val pageTitle = "#main-content > div > div > form > h1"
    val body1 = "#main-content > div > div > form > h2:nth-child(3)"
    val body1ContentTitle = "#main-content > div > div > form > p:nth-child(4)"
    val body2 = "#main-content > div > div > form > h2:nth-child(6)"
    val contactName = "#main-content > div > div > form > dl:nth-child(5) > div:nth-child(1) > dt"
    val email = "#main-content > div > div > form > dl:nth-child(5) > div:nth-child(2) > dt"
    val phone = "#main-content > div > div > form > dl:nth-child(5) > div:nth-child(3) > dt"
    val address = "#main-content > div > div > form > dl:nth-child(5) > div:nth-child(4) > dt"
    val addressChangeLink = "#address-linkid"
    val sautr = "#main-content > div > div > form > dl:nth-child(7) > div > dt"
    val provideTRNLink = "#sautr-linkid"
    val body3 = "#main-content > div > div > form > h2:nth-child(8)"
    val body3Content = "#main-content > div > div > form > p:nth-child(9)"
    val continue = "#continue"
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    mockConfig.features.welshLanguageSupportEnabled(false)
  }

  "CheckYourAnswersView" when {

    "produce the same output for apply() and render()" in {
      val htmlApply = injectedView.apply(contactDetailsSummaryList, trnSummaryList, "name").body
      val htmlRender = injectedView.render(contactDetailsSummaryList, trnSummaryList, "name", request, messages, mockConfig).body
      val htmlF = injectedView.f(contactDetailsSummaryList, trnSummaryList, "name")(request, messages, mockConfig).body
      htmlApply mustBe htmlRender
      htmlF must not be empty
    }

    "injected into the view" should {

      "render the correct page title" in {
        lazy val view = injectedView(contactDetailsSummaryList, trnSummaryList, "name")
        lazy implicit val document: Document = Jsoup.parse(view.body)
        elementText(Selectors.heading) mustBe heading
        elementText(Selectors.pageTitle) mustBe pageTitle
        elementText(Selectors.backLink) mustBe backLink
        elementText(Selectors.body1) mustBe body1
        elementText(Selectors.body1ContentTitle) mustBe body1ContentTitle
        elementText(Selectors.body2) mustBe body2
        elementText(Selectors.contactName) mustBe contactName
        elementText(Selectors.email) mustBe emailAddress
        elementText(Selectors.phone) mustBe phoneNumber
        elementText(Selectors.address) mustBe address
        elementText(Selectors.addressChangeLink) mustBe change
        elementText(Selectors.sautr) mustBe sautr
        elementText(Selectors.provideTRNLink) mustBe provideTRN
        elementText(Selectors.body3) mustBe body3
        elementText(Selectors.body3Content) mustBe body3Content
        elementText(Selectors.continue) mustBe continue
      }
    }
  }

}
