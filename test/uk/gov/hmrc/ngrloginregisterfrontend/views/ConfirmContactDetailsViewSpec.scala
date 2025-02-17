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
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.ViewBaseSpec
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.components.saveAndContinueButton
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.{ConfirmContactDetailsView, Layout}

class ConfirmContactDetailsViewSpec extends ViewBaseSpec {

  val layout: Layout = MockitoSugar.mock[Layout]
  val button: saveAndContinueButton = mock[saveAndContinueButton]
  val injectedView: ConfirmContactDetailsView = injector.instanceOf[ConfirmContactDetailsView]
  val summaryList: SummaryList = SummaryList(Seq())

  val navTitle = "Manage your business rates valuation"

  object Selectors {
    val navTitle = ".govuk-header__service-name"
    val languageSelector = "#content > nav > ul > li:nth-child(1) > span"
    val headingSelector = "#content > form > h1"
    val backLink = ".govuk-back-link"
    val button = "#continue"
    val body1Selector = "#content > form > p:nth-child(2)"
    val body2Selector = "#content > form > p:nth-child(3)"
    val bullet1 = "#content > form > ul > li:nth-child(1)"
    val bullet2 = "#content > form > ul > li:nth-child(2)"
    val bullet3 = "#content > form > ul > li:nth-child(3)"
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
      }

    }

  }

}
