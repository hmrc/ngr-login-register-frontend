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

package uk.gov.hmrc.ngrloginregisterfrontend.helpers

import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.scalatest.matchers.must.Matchers
import org.scalatest.{Assertion, BeforeAndAfterEach}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.{Lang, Messages, MessagesApi, MessagesImpl}
import play.api.inject.Injector
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.{FakeRequest, Injecting}
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow
import uk.gov.hmrc.ngrloginregisterfrontend.mocks.MockAppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.models.{Email, Link, NGRSummaryListRow}
import uk.gov.hmrc.ngrloginregisterfrontend.models.NGRSummaryListRow.summarise

trait ViewBaseSpec extends PlaySpec with GuiceOneAppPerSuite with Injecting with BeforeAndAfterEach with Matchers with TestData {
  def injector: Injector = app.injector
  implicit lazy val messages: Messages = MessagesImpl(Lang("en"), messagesApi)
  lazy val messagesApi: MessagesApi             = inject[MessagesApi]

  def element(cssSelector: String)(implicit document: Document): Element = {
    val elements = document.select(cssSelector)

    if(elements.size == 0) {
      fail(s"No element exists with the selector '$cssSelector'")
    }

    document.select(cssSelector).first()
  }

  lazy implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()


  def elementText(selector: String)(implicit document: Document): String = {
    element(selector).text()
  }

  def asDocument(html: Html): Document = Jsoup.parse(html.toString())

  def elementExtinct(cssSelector: String)(implicit document: Document): Assertion = {
    val elements = document.select(cssSelector)

    if (elements.size == 0) {
      succeed
    } else {
      fail(s"Element with selector '$cssSelector' was found!")
    }
  }

  lazy implicit val mockConfig: MockAppConfig = new MockAppConfig(app.configuration)

  val testEmail: String = Email("test@test.co.uk").toString


  val personNameSummaryListDetails: String = Seq(
    personDetailsResponse.person.firstName,
    personDetailsResponse.person.middleName,
    personDetailsResponse.person.lastName
  ).flatten.mkString(" ")

  val addressSummaryListDetails: Seq[String] = Seq(
    personDetailsResponse.address.line1.getOrElse(""),
    personDetailsResponse.address.line2.getOrElse(""),
    personDetailsResponse.address.line3.getOrElse(""),
    personDetailsResponse.address.line4.getOrElse(""),
    personDetailsResponse.address.line5.getOrElse(""),
    personDetailsResponse.address.postcode.getOrElse(""),
    personDetailsResponse.address.country.getOrElse("")
  ).filter(_.nonEmpty)

  def createSummaryListRows()(implicit messages: Messages): Seq[SummaryListRow] =
  Seq(
    NGRSummaryListRow("confirmContactDetails.contactName", None, Seq(personNameSummaryListDetails), Some(Link(Call("GET", "url"), "changeName", "Change"))),
    NGRSummaryListRow("confirmContactDetails.emailAddress", None, Seq(testEmail), Some(Link(Call("GET", "url"), "changeEmail", "Change"))),
    NGRSummaryListRow("confirmContactDetails.phoneNumber", None, Seq.empty, Some(Link(Call("GET", "url"), "addPhoneNumber", "Add"))),
    NGRSummaryListRow("confirmContactDetails.address", None, addressSummaryListDetails, Some(Link(Call("GET", "url"), "changeAddress", "Change")))
  ).map(summarise)

}
