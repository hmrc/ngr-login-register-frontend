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

import org.scalatestplus.mockito.MockitoSugar
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.ViewBaseSpec
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.{Layout, StartView}

class ProvideTRNViewSpec extends ViewBaseSpec {

  val injectedView: StartView = injector.instanceOf[StartView]
  val navTitle = "Manage your business rates valuation"
  val caption = "Register for the business rates valuation service"
  val heading = "Provide your Tax Reference Number"
  val paragraph = "Your Tax Reference Number (TRN) is used to match your tax data with your property data. This information helps the government provide targeted support and improve business rates compliance."
  val typesOfTrn = "Types of Tax Reference Numbers"
  val typesOfTrnContent = "The type of TRN you provide will depend on whether you are paying business rates as an individual or organisation."
  val typeofTrnBulletContent = "Individual rate payers can provide either:"
  val bullet1 = "Self-assessment Unique Tax Reference (UTR)"
  val bullet2 = "National Insurance number (NINO)"
  val disclaimer = "It is a legal requirement to provide a TRN. While you can provide one later, we advise you to do so as soon as possible."
  val buttonText = "Continue"

  object Selectors {
    val navTitle = ".govuk-header__service-name"
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
  }
}
