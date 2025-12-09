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

import uk.gov.hmrc.ngrloginregisterfrontend.helpers.ViewBaseSpec
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.mockito.MockitoSugar.mock
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.StartView
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.Layout
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.components.saveAndContinueButton

class StartSpec extends ViewBaseSpec {

  val layout: Layout = MockitoSugar.mock[Layout]
  val button: saveAndContinueButton = mock[saveAndContinueButton]
  val injectedView: StartView = injector.instanceOf[StartView]
  val navTitle = "Manage your business rates valuation"
  val heading = "Register for the business rates valuation service"
  val body1 = "Use this service to meet your obligations as the ratepayer of a non-domestic property."
  val body2 = "When you register, you can use this service to:"
  val bullet1 = "add a property to your account"
  val bullet2 = "report changes to your property"
  val bullet3 = "provide a valid Tax Reference Number"

  object Selectors {
    val navTitle = ".govuk-service-navigation__service-name"
    val languageSelector = "#main-content > div > div > nav > ul > li:nth-child(1) > span"
    val headingSelector = "#main-content > div > div > form > h1"
    val backLink = ".govuk-back-link"
    val button = "#continue"
    val body1Selector = "#main-content > div > div > form > p:nth-child(2)"
    val body2Selector = "#main-content > div > div > form > p:nth-child(3)"
    val bullet1 = "#main-content > div > div > form > ul > li:nth-child(1)"
    val bullet2 = "#main-content > div > div > form > ul > li:nth-child(2)"
    val bullet3 = "#main-content > div > div > form > ul > li:nth-child(3)"
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    mockConfig.features.welshLanguageSupportEnabled(false)
  }

  "StartView" when {

    "produce the same output for apply() and render()" in {
      val htmlApply = injectedView.apply().body
      val htmlRender = injectedView.render(messages, mockConfig, request).body
      val htmlF: HtmlFormat.Appendable = injectedView.f()(messages, mockConfig, request)
      htmlApply mustBe htmlRender
      htmlF.toString() must not be empty
    }

    "injected into the view" should {

      "render the correct page title" in {
        lazy val view = injectedView()
        lazy implicit val document: Document = Jsoup.parse(view.body)
        elementText(Selectors.navTitle) mustBe navTitle
      }

      "render the correct heading" in {
        lazy val view = injectedView()
        lazy implicit val document: Document = Jsoup.parse(view.body)
        elementText(Selectors.headingSelector) mustBe heading
      }

      "contain expected paragraph texts" in {
        lazy val view = injectedView()
        lazy implicit val document: Document = Jsoup.parse(view.body)
        elementText(Selectors.body1Selector) mustBe body1
        elementText(Selectors.body2Selector) mustBe body2
      }

      "contain the correct bullet points" in {
        lazy val view = injectedView()
        lazy implicit val document: Document = Jsoup.parse(view.body)
        elementText(Selectors.bullet1) mustBe bullet1
        elementText(Selectors.bullet2) mustBe bullet2
        elementText(Selectors.bullet3) mustBe bullet3
      }
    }
  }
}
