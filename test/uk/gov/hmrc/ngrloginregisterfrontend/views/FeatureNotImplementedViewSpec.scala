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
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.ViewBaseSpec
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.{FeatureNotImplementedView, Layout}

class FeatureNotImplementedViewSpec  extends ViewBaseSpec {

    val layout: Layout = MockitoSugar.mock[Layout]
    lazy val testView: FeatureNotImplementedView = inject[FeatureNotImplementedView]
    lazy val backLink = "Back"
    val title = "This part of the online service is not available yet"
    val heading = "This part of the online service is not available yet"
    val body1Id = "The Journey ID is 12345"
    val body1NoId = "The Journey ID is None"

  object Selectors {
    val navTitle = ".govuk-header__service-name"
    val headingSelector = "#content > h1"
    val body1Selector = "#content > p:nth-child(0)"
    val backLink = ".govuk-back-link"

    "The FeatureNotImplemented view" should {
        "Render a page with the appropriate message" when {
          "a journey ID is present" in {
            lazy implicit val document: Document = Jsoup.parse(testView(Some("12345"))(request, messages, mockConfig).body)
            elementText(Selectors.navTitle) mustBe title
            elementText(Selectors.backLink) mustBe backLink
            elementText(Selectors.headingSelector) mustBe heading
            elementText(Selectors. body1Selector) mustBe body1Id
          }
          "A journey ID is not present" in {
            lazy implicit val document: Document = Jsoup.parse(testView(None)(request, messages, mockConfig).body)
            elementText(Selectors.navTitle) mustBe title
            elementText(Selectors.backLink) mustBe backLink
            elementText(Selectors.headingSelector) mustBe heading
            elementText(Selectors. body1Selector) mustBe body1NoId
          }
        }
    }
}
}
