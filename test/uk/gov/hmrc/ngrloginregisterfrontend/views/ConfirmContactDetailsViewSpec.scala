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
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{SummaryList, SummaryListRow}
import uk.gov.hmrc.ngrloginregisterfrontend.controllers.ConfirmContactDetailsController
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.{TestData, ViewBaseSpec}
import uk.gov.hmrc.ngrloginregisterfrontend.models.AuthenticatedUserRequest
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.components.saveAndContinueButton
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.{ConfirmContactDetailsView, Layout}

class ConfirmContactDetailsViewSpec extends ViewBaseSpec with TestData {

  val layout: Layout = MockitoSugar.mock[Layout]
  val button: saveAndContinueButton = mock[saveAndContinueButton]
  val injectedView: ConfirmContactDetailsView = injector.instanceOf[ConfirmContactDetailsView]
  val summaryList: SummaryList = SummaryList(rows)
  lazy val controller: ConfirmContactDetailsController = inject[ConfirmContactDetailsController]
  lazy val rows: Seq[SummaryListRow] = controller.createSummaryRows(personDetailsResponse, AuthenticatedUserRequest(request, None, None, Some("yes@ef.com"), None, None, None, Nino(hasNino = true, Some(""))))

  val navTitle = "Manage your business rates valuation"
  val pageTitle = "Confirm your contact details"
  val body1 = "The Valuation Office Agency (VOA) will use these details to:"
  val bullet1 = "send you information related to the service and your account"
  val bullet2 = "confirm your identity if you contact the VOA"
  val body2 = "This account is registered to"
  val contactName = "Contact name"
  val emailAddress = "Email address"
  val phoneNumber = "Phone number"
  val address = "Address"
  val change = "Change"
  val continue = "Continue"

  object Selectors {
    val navTitle = ".govuk-header__service-name"
    val pageTitle = "#content > h1"
    val body1 = "#content > p:nth-child(3)"
    val body2 = "#content > p:nth-child(5)"
    val bullet1 = "#content > ul > li:nth-child(1)"
    val bullet2 = "#content > ul > li:nth-child(2)"
    val contactName = "#content > dl > div:nth-child(1) > dt"
    val email = "#content > dl > div:nth-child(2) > dt"
    val phone = "#content > dl > div.govuk-summary-list__row.govuk-summary-list__row--no-actions > dt"
    val address = "#content > dl > div:nth-child(4) > dt"
    val continue = "#continue"
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    lazy val view = injectedView(summaryList)
    val html: HtmlFormat.Appendable = view
    mockConfig.features.welshLanguageSupportEnabled(false)
  }

  "ConfirmContactDetailsView" when {

    "produce the same output for apply() and render()" in {
      val htmlApply = injectedView.apply(summaryList).body
      val htmlRender = injectedView.render(summaryList, request, messages, mockConfig).body
      val fFunction: HtmlFormat.Appendable = injectedView.f(summaryList)(request, messages, mockConfig)
      htmlApply mustBe htmlRender
    }

    "injected into the view" should {

      "render the correct page title" in {
        lazy val view = injectedView(summaryList)
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
