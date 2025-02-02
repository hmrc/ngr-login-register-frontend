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
import org.mockito.Mockito.when
import play.twirl.api.Html
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.{TestSupport, ViewBaseSpec}
import uk.gov.hmrc.ngrloginregisterfrontend.mocks.MockAppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.Layout

class LayoutSpec extends ViewBaseSpec {

  val injectedView: Layout = injector.instanceOf[Layout]
  val navTitle = "Manage your business rates valuation"
  val backLink = "Back"

  object Selectors {
    val navTitle = ".govuk-header__service-name"
    val languageSelector = "#content > nav > ul > li:nth-child(1) > span"
    val backLink = ".govuk-back-link"
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    mockConfig.features.welshLanguageSupportEnabled(false)
  }


  "The Layout template" when {

    "injected into the view" should {

      "show the nav title" in {
        lazy val view = injectedView(pageTitle = Some("Title of page"),showBackLink = false)(Html("Test"))(request,messages,mockConfig)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        elementText(Selectors.navTitle) mustBe navTitle
      }

      "should not display the language selector" in {
        lazy val view = injectedView(pageTitle = Some("Title of page"),showBackLink = false)(Html("Test"))(request,messages,mockConfig)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        elementExtinct(Selectors.languageSelector)
      }

      "should show a backlink" in {
        lazy val view = injectedView(pageTitle = Some("Title of page"),showBackLink = true)(Html("Test"))(request,messages,mockConfig)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        elementText(Selectors.backLink) mustBe backLink
      }

      "the language selector feature switch is turned on" in {
        mockConfig.features.welshLanguageSupportEnabled(true)
        lazy val view = injectedView(pageTitle = Some("Title of page"),showBackLink = false)(Html("Test"))(request,messages,  mockConfig)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        elementText(Selectors.languageSelector) mustBe "English"
        mockConfig.features.welshLanguageSupportEnabled(false)
      }
    }
  }

}
