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
import play.twirl.api.Html
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.ViewBaseSpec
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.Layout

class LayoutSpec extends ViewBaseSpec {
//TODO delete dead code
  val injectedView: Layout = injector.instanceOf[Layout]
  val navTitle = "Manage your business rates valuation"
  val backLink = "Back"

  object Selectors {
    val navTitleOld = ".govuk-header__service-name"
    val navTitle = ".govuk-service-navigation__service-name"
    val languageSelector = "nav.hmrc-service-navigation-language-select span[aria-current=\"true\"]"
    val languageSelectorOld = "#main-content > div > div > nav > ul > li:nth-child(1) > span"
    val backLink = ".govuk-back-link"
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    mockConfig.features.welshLanguageSupportEnabled(false)
  }

  private def printHtml(html: String, title: String = "Pretty Printed HTML"): Unit = {
    val doc = Jsoup.parse(html)
    doc.outputSettings(new Document.OutputSettings().prettyPrint(true).indentAmount(2))
    println(s"\n=== $title ===")
    println(doc.outerHtml())
  }


  "The Layout template" when {

    "produce the same output for apply() and render()" in {
      val htmlApply = injectedView.apply(pageTitle = Some("Title of page"), showBackLink = false, timeout = false)(Html("Test")).body
      val htmlRender = injectedView.render(pageTitle = Some("Title of page"), showBackLink = false, contentBlock = Html("Test"), request = request, messages = messages, appConfig = mockConfig, fullWidth = false, timeout = false).body
      val htmlF = injectedView.f(Some("Title of page"), false, false, false)(Html("Test"))(request, messages, mockConfig).body
      htmlApply mustBe htmlRender
      htmlF must not be empty
    }

    "injected into the view" should {

      "show the nav title" in {
        lazy val view = injectedView(pageTitle = Some("Title of page"),showBackLink = false)(Html("Test"))(request,messages,mockConfig)
        lazy implicit val document: Document = Jsoup.parse(view.body)
        printHtml(view.body, "LayoutSpec: nav title test view")

        elementText(Selectors.navTitle) mustBe navTitle
      }

      "should not display the language selector" in {
        lazy val view = injectedView(pageTitle = Some("Title of page"),showBackLink = false)(Html("Test"))(request,messages,mockConfig)
        lazy implicit val document: Document = Jsoup.parse(view.body)


        elementExtinct(Selectors.languageSelector)
      }

      "should show a backlink" in {
        lazy val view = injectedView(pageTitle = Some("Title of page"))(Html("Test"))(request,messages,mockConfig)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        elementText(Selectors.backLink) mustBe backLink
      }

      "the language selector feature switch is turned on" in {
        mockConfig.features.welshLanguageSupportEnabled(true)
        lazy val view = injectedView(pageTitle = Some("Title of page"),showBackLink = false)(Html("Test"))(request,messages,  mockConfig)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        elementText(Selectors.languageSelector) mustBe "ENG"
        mockConfig.features.welshLanguageSupportEnabled(false)
      }
    }
  }

}