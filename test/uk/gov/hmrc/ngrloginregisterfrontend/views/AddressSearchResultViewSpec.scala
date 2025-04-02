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
import uk.gov.hmrc.govukfrontend.views.Aliases.Table
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.ViewBaseSpec
import uk.gov.hmrc.ngrloginregisterfrontend.models.PaginationData
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.AddressSearchResultView

class AddressSearchResultViewSpec extends ViewBaseSpec {

  lazy val addressSearchResultView: AddressSearchResultView = app.injector.instanceOf[AddressSearchResultView]
  lazy val postcode: String = "BN110AA"

  lazy val backLink = "Back"
  lazy val caption = "Register for the business rates valuation service"
  private def  heading(postcode: String) = s"Search results for $postcode"
  lazy val previousButton = "Previous page"
  lazy val nextButton = "Next page"
  lazy val searchAgain = "Search again"

  object Selectors {
    val backLink = "body > div > a"
    val caption = "#main-content > div > div > span"
    val heading = "#main-content > div > div > h1"
    val previousButton = "#main-content > div > div > nav > div > a"
    val nextButton = "#main-content > div > div > nav > div.govuk-pagination__next > a"
    val searchAgain = "#main-content > div > div > p > a"
  }

  "Rendering the AddressSearchResultView on page 1 with 10 address's and 5 a page" should {
    val returnedAddressList = Seq(testAddressString, testAddressString, testAddressString, testAddressString, testAddressString, testAddressString, testAddressString, testAddressString, testAddressString)
    val currentPage:Int = 1
    val pageSize:Int = 5
    val totalPages: Int = math.ceil(returnedAddressList.length.toFloat / pageSize.toFloat).toInt
    lazy val view = addressSearchResultView(
      postcode = postcode,
      paginationData = PaginationData(totalPages = totalPages, currentPage = currentPage, baseUrl = "/ngr-login-register-frontend/address-search-results", pageSize = pageSize),
      totalAddress = returnedAddressList.length,
      pageTop = PaginationData.pageTop(currentPage = currentPage, pageSize = pageSize, returnedAddressList.length),
      pageBottom = PaginationData.pageBottom(currentPage = currentPage, pageSize = pageSize) + 1,
      addressSearchResultTable = Table(),
      mode = confirmContactDetailsMode
    )
    lazy implicit val document: Document = Jsoup.parse(view.body)
    lazy val htmlF = addressSearchResultView.f(
      postcode,
      PaginationData(totalPages = totalPages, currentPage = currentPage, baseUrl = "/ngr-login-register-frontend/address-search-results", pageSize = pageSize),
      returnedAddressList.length,
      PaginationData.pageTop(currentPage = currentPage, pageSize = pageSize, returnedAddressList.length),
      PaginationData.pageBottom(currentPage = currentPage, pageSize = pageSize) + 1,
      Table(),
      confirmContactDetailsMode)(request, messages, mockConfig
    )
    lazy val htmlRender = addressSearchResultView.render(
      postcode = postcode,
      paginationData = PaginationData(totalPages = totalPages, currentPage = currentPage, baseUrl = "/ngr-login-register-frontend/address-search-results", pageSize = pageSize),
      totalAddress = 0,
      pageTop = 0,
      pageBottom = 0,
      request = request,
      messages = messages,
      appConfig = mockConfig,
      addressSearchResultTable = Table(),
      mode = confirmContactDetailsMode
    )

    "htmlF is not empty" in {
      htmlF.toString() must not be empty
    }
    "render produces document" in {
      htmlRender.body must not be empty
    }
    "have the back link" in {
      elementText(Selectors.backLink) mustBe backLink
    }

    "contain the Search again link" in {
      elementText(Selectors.searchAgain) mustBe searchAgain
    }

    "have the correct caption and heading" in {
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading) mustBe heading(postcode)
    }

    "The paginate buttons are rendered" which{
      "should render the next Button" in {
        elementText(Selectors.nextButton) mustBe nextButton
      }
    }
  }
  "Rendering the AddressSearchResultView on page 2 with 10 address's and 5 a page" should {
    val returnedAddressList = Seq(testAddressString, testAddressString, testAddressString, testAddressString, testAddressString, testAddressString, testAddressString, testAddressString, testAddressString)
    val currentPage:Int = 2
    val pageSize:Int = 5
    val totalPages: Int = math.ceil(returnedAddressList.length.toFloat / pageSize.toFloat).toInt
    lazy val view = addressSearchResultView(
      postcode = postcode,
      paginationData = PaginationData(totalPages = totalPages, currentPage = currentPage, baseUrl = "/ngr-login-register-frontend/address-search-results", pageSize = pageSize),
      totalAddress = returnedAddressList.length,
      pageTop = PaginationData.pageTop(currentPage = currentPage, pageSize = pageSize, returnedAddressList.length),
      pageBottom = PaginationData.pageBottom(currentPage = currentPage, pageSize = pageSize) + 1,
      addressSearchResultTable = Table(),
      mode = confirmContactDetailsMode
    )
    lazy implicit val document: Document = Jsoup.parse(view.body)
    lazy val htmlF = addressSearchResultView.f(
      postcode,
      PaginationData(totalPages = totalPages, currentPage = currentPage, baseUrl = "/ngr-login-register-frontend/address-search-results", pageSize = pageSize),
      returnedAddressList.length,
      PaginationData.pageTop(currentPage = currentPage, pageSize = pageSize, returnedAddressList.length),
      PaginationData.pageBottom(currentPage = currentPage, pageSize = pageSize) + 1,
      Table(),
      confirmContactDetailsMode
    )(request, messages, mockConfig)

    "htmlF is not empty" in {
      htmlF.toString() must not be empty
    }
    "have the back link" in {
      elementText(Selectors.backLink) mustBe backLink
    }

    "have the correct caption and heading" in {
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading) mustBe heading(postcode)
    }

    "The paginate buttons are rendered" which{
      "should render the previous Button" in {
        elementText(Selectors.previousButton) mustBe previousButton
      }
    }
  }
  "Rendering the AddressSearchResultView on page 1 with 0 address's and 5 a page" should {
    val returnedAddressList = Seq()
    val currentPage:Int = 1
    val pageSize:Int = 5
    val totalPages: Int = math.ceil(returnedAddressList.length.toFloat / pageSize.toFloat).toInt
    lazy val view = addressSearchResultView(
      postcode = postcode,
      PaginationData(totalPages = totalPages, currentPage = currentPage, baseUrl = "/ngr-login-register-frontend/address-search-results", pageSize = pageSize),
      totalAddress = returnedAddressList.length,
      pageTop = PaginationData.pageTop(currentPage = currentPage, pageSize = pageSize, returnedAddressList.length),
      pageBottom = PaginationData.pageBottom(currentPage = currentPage, pageSize = pageSize) + 1,
      addressSearchResultTable = Table(),
      mode = confirmContactDetailsMode
    )
    lazy implicit val document: Document = Jsoup.parse(view.body)
    lazy val htmlF = addressSearchResultView.f(
      postcode,
      PaginationData(totalPages = totalPages, currentPage = currentPage, baseUrl = "/ngr-login-register-frontend/address-search-results", pageSize = pageSize),
      returnedAddressList.length,
      PaginationData.pageTop(currentPage = currentPage, pageSize = pageSize, returnedAddressList.length),
      PaginationData.pageBottom(currentPage = currentPage, pageSize = pageSize) + 1,
      Table(),
      confirmContactDetailsMode)(request, messages, mockConfig
    )

    "htmlF is not empty" in {
      htmlF.toString() must not be empty
    }
    "have the back link" in {
      elementText(Selectors.backLink) mustBe backLink
    }

    "have the correct caption and heading" in {
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading) mustBe heading(postcode)
    }
  }
}
