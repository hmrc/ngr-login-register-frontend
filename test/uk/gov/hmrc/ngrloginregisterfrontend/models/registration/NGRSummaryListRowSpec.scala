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

package uk.gov.hmrc.ngrloginregisterfrontend.models.registration

import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{Empty, HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, Actions, Key, SummaryListRow, Value}
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.TestSupport
import uk.gov.hmrc.ngrloginregisterfrontend.models.{Link, NGRSummaryListRow}

class NGRSummaryListRowSpec extends TestSupport {

  val fakeGetRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/check-answers")
  val messagesAPI: MessagesApi = inject[MessagesApi]

  "buildSummaryListRow" when {

    "will generate a minimum SummaryListRow from a minimum VoaSummaryListRow" in {
      implicit val messages: Messages = messagesAPI.preferred(fakeGetRequest)
      val voaSummaryListRow: NGRSummaryListRow = NGRSummaryListRow("", None, Seq.empty, None)
      val result = NGRSummaryListRow.summarise(voaSummaryListRow)
      result shouldBe SummaryListRow(Key(Text(""), ""), Value(Empty, ""), "", None)
    }

    "will generate a minimum SummaryListRow with a key from English messages if the key is set in VoaSummaryListRow" in {
      implicit val messages: Messages = messagesAPI.preferred(fakeGetRequest)
      val voaSummaryListRow: NGRSummaryListRow = NGRSummaryListRow("checkYourAnswers.name", None, Seq.empty, None)
      val result = NGRSummaryListRow.summarise(voaSummaryListRow)
      result shouldBe SummaryListRow(Key(Text("Name"), ""), Value(Empty, ""), "", None)
    }

    "will generate a SummaryListRow with a value if the value is set in CheckYourAnswersRow" in {
      implicit val messages: Messages = messagesAPI.preferred(fakeGetRequest)
      val voaSummaryListRow: NGRSummaryListRow = NGRSummaryListRow("checkYourAnswers.name", None, Seq("Jimley Jackson"), None)
      val result = NGRSummaryListRow.summarise(voaSummaryListRow)
      result shouldBe SummaryListRow(Key(Text("Name"), ""), Value(HtmlContent("<span id=\"checkyouranswers.name-id\">Jimley Jackson</span>")), "", None)
    }

    "will generate a SummaryListRow with a separated lines if the value is set as multiple strings in CheckYourAnswersRow" in {
      implicit val messages: Messages = messagesAPI.preferred(fakeGetRequest)
      val voaSummaryListRow: NGRSummaryListRow = NGRSummaryListRow("checkYourAnswers.address", None, Seq("Line1", "Line2"), None)
      val result = NGRSummaryListRow.summarise(voaSummaryListRow)
      result shouldBe SummaryListRow(Key(Text("Address"), ""), Value(HtmlContent("<span id=\"checkyouranswers.address-id\">Line1</br>Line2</span>")), "", None)
    }

    "will generate a SummaryListRow with an Action" in {
      implicit val messages: Messages = messagesAPI.preferred(fakeGetRequest)
      val voaSummaryListRow: NGRSummaryListRow = NGRSummaryListRow("checkYourAnswers.address", None, Seq("Line1", "Line2"), Some(Link(Call("GET", "url"), "id", "Change")))
      val result = NGRSummaryListRow.summarise(voaSummaryListRow)
      result shouldBe SummaryListRow(Key(Text("Address"), ""), Value(HtmlContent("<span id=\"checkyouranswers.address-id\">Line1</br>Line2</span>")), "", Some(Actions("", List(ActionItem("url", Text("Change"), None, "", Map("id" -> "id"))))))
    }

    "will generate a minimum SummaryListRow with an Action" in {
      implicit val messages: Messages = messagesAPI.preferred(fakeGetRequest)
      val voaSummaryListRow: NGRSummaryListRow = NGRSummaryListRow("", None, Seq.empty, Some(Link(Call("GET", "url"), "id", "Change")))
      val result = NGRSummaryListRow.summarise(voaSummaryListRow)
      result shouldBe SummaryListRow(Key(Text(""), ""), Value(HtmlContent(s"""<a id="id" href="url" class="govuk-link">Change</a>"""), ""), "", None)
    }

  }
}