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
  val body1ContentTitle = "This account is registered to name"
  val body2 = "Tax reference number"
  val sautr = "Self Assessment Unique Taxpayer Reference"
  val provideTRN = "Provide your TRN"
  val change = "Change"
  val body3 ="Register to use this service"
  val body3Content = "By registering for this service you are confirming that, to the best of your knowledge, the details you are providing are correct."
  val continue = "Accept and register"

  object Selectors {
    val backLink = "body > div > a"
    val heading = "#main-content > div > div > form > span"
    val pageTitle = "#main-content > div > div > form > h1"
    val body1ContentTitle = "#main-content > div > div > form > p:nth-child(3)"
    val sautr = "#main-content > div > div > form > dl:nth-child(5) > div > dt"
    val provideTRNLink = "#sautr-linkid"
    val body3 = "#main-content > div > div > form > h2:nth-child(6)"
    val body3Content = "#main-content > div > div > form > p:nth-child(7)"
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
        elementText(Selectors.body1ContentTitle) mustBe body1ContentTitle
        elementText(Selectors.sautr) mustBe sautr
        elementText(Selectors.provideTRNLink) mustBe provideTRN
        elementText(Selectors.body3) mustBe body3
        elementText(Selectors.body3Content) mustBe body3Content
        elementText(Selectors.continue) mustBe continue
      }
    }
  }

}
