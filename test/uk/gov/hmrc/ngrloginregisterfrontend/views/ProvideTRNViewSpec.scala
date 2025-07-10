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
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.ViewBaseSpec
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.ProvideTRNView

class ProvideTRNViewSpec extends ViewBaseSpec {

  val injectedView: ProvideTRNView = injector.instanceOf[ProvideTRNView]
  lazy val view: HtmlFormat.Appendable = injectedView()
  lazy implicit val document: Document = Jsoup.parse(view.body)
  val navTitle = "Manage your business rates valuation"
  val caption = "Register for the business rates valuation service"
  val heading = "Provide your tax reference number"
  val paragraph: String = "Your Tax Reference Number (TRN) is used to match your tax data with your property data. This information helps the " +
    "government provide targeted support and improve business rates compliance."
  val typesOfTrn = "Types of tax reference numbers"
  val typesOfTrnContent = "The type of TRN you provide will depend on whether you are paying business rates as an individual or organisation."
  val typeofTrnBulletContent = "Individual rate payers can provide either:"
  val bullet1 = "Self-assessment Unique Tax Reference (UTR)"
  val bullet2 = "National Insurance number (NINO)"
  val disclaimer = "It is recommended that you provide a TRN when you register for this service."
  val buttonText = "Continue"

  object Selectors {
    val navTitle = ".govuk-header__service-name"
    val caption  = "#main-content > div > div > form > span"
    val heading = "#main-content > div > div > form > h1"
    val paragraph = "#main-content > div > div > form > p:nth-child(3)"
    val typesOfTrn = "#main-content > div > div > form > h2"
    val typesOfTrnContent = "#main-content > div > div > form > p:nth-child(5)"
    val typeofTrnBulletContent = "#main-content > div > div > form > p:nth-child(6)"
    val bullet1 = "#main-content > div > div > form > ul > li:nth-child(1)"
    val bullet2 = "#main-content > div > div > form > ul > li:nth-child(2)"
    val disclaimer = "#main-content > div > div > form > p:nth-child(8)"
    val buttonText = "#continue"

  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    mockConfig.features.welshLanguageSupportEnabled(false)
  }

  "ProvideTRN View" when {

    "produce the same output for apply() and render()" in {
      val htmlApply = injectedView.apply().body
      val htmlRender = injectedView.render(messages, mockConfig, request).body
      val htmlF: HtmlFormat.Appendable = injectedView.f()(messages, mockConfig, request)
      htmlApply mustBe htmlRender
      htmlF.toString() must not be empty
    }

    "injected should include the correct content" in {
      elementText(Selectors.navTitle) mustBe navTitle
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading) mustBe heading
      elementText(Selectors.paragraph) mustBe paragraph
      elementText(Selectors.typesOfTrn) mustBe typesOfTrn
      elementText(Selectors.typesOfTrnContent) mustBe typesOfTrnContent
      elementText(Selectors.typeofTrnBulletContent) mustBe typeofTrnBulletContent
      elementText(Selectors.bullet1) mustBe bullet1
      elementText(Selectors.bullet2) mustBe bullet2
      elementText(Selectors.disclaimer) mustBe disclaimer
      elementText(Selectors.buttonText) mustBe buttonText
    }
  }
}
