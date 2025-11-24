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

package controllers

import Stubs.{AuthStub, CitizenDetailsStub}
import helpers.{IntegrationSpecBase, IntegrationTestData}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import play.api.test.Injecting

class ConfirmUTRControllerISpec extends AnyWordSpec with IntegrationSpecBase with Injecting with IntegrationTestData {

  val utrSelector = "#self-assessment-unique-taxpayer-reference-id"
  val yesUtr: Map[String, Seq[String]] = Map("confirmUTR" -> Seq("Yes(1097133333)"))
  val noNino: Map[String, Seq[String]] = Map("confirmUTR" -> Seq("NoNI"))
  val noLater: Map[String, Seq[String]] = Map("confirmUTR" -> Seq("NoLater"))

  "GET /confirm-utr for an authenticated user" when {
    "the users contact details are confirmed" must {
      "return a 200 OK and display the users UTR displayed on the page" in {
        AuthStub.authorised
        CitizenDetailsStub.matchingStub
        CitizenDetailsStub.designatoryDetails
        val request: WSRequest = buildRequest("/confirm-utr")
        val response: WSResponse = await(request.get())
        val document: Document = Jsoup.parse(response.body)

        response.status shouldBe Status.OK
        document.title() shouldBe "Confirm your Self Assessment Unique Taxpayer Reference - GOV.UK"
        document.select(utrSelector).text() shouldBe "*******333"
      }
    }
    "user has no utr registered to there account" must {
      "return a 500" in {
        AuthStub.authorised
        CitizenDetailsStub.matchingStubNoUtr
        val request: WSRequest = buildRequest("/confirm-utr")
        val response: WSResponse = await(request.get())
        response.status shouldBe Status.INTERNAL_SERVER_ERROR

      }
    }
  }

  "Calling the POST /confirm-utr for an authenticated user" when {
    "the user selects Yes" must {
      "redirect to check your answers page" in {
        AuthStub.authorised
        val request: WSRequest = buildRequest("/confirm-utr").withHttpHeaders("Csrf-Token" -> "nocheck")
        val response: WSResponse = await(request.post(yesUtr))
        response.status shouldBe Status.SEE_OTHER
        response.header("Location").get shouldBe "/ngr-login-register-frontend/check-answers"
      }
    }
    "the user selects no i'll add my nino" must {
      "redirect to the provide national insurance number page" in {
        AuthStub.authorised
        val request: WSRequest = buildRequest("/confirm-utr").withHttpHeaders("Csrf-Token" -> "nocheck")
        val response: WSResponse = await(request.post(noNino))
        response.status shouldBe Status.SEE_OTHER
        response.header("Location").get shouldBe "/ngr-login-register-frontend/provide-national-insurance-number"
      }
      "the user selects no I'll add my utr or nino later" must {
        "redirect to the check your answers page" in {
          AuthStub.authorised
          val request: WSRequest = buildRequest("/confirm-utr").withHttpHeaders("Csrf-Token" -> "nocheck")
          val response: WSResponse = await(request.post(noLater))
          response.status shouldBe Status.SEE_OTHER
          response.header("Location").get shouldBe "/ngr-login-register-frontend/check-answers"
        }
      }
    }
  }
}

