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
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.ViewBaseSpec
import uk.gov.hmrc.ngrloginregisterfrontend.models.{Address, PaginatedAddress, Postcode}
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.AddressSearchResultView

class AddressSearchResultViewSpec extends ViewBaseSpec {

  lazy val addressSearchResultView: AddressSearchResultView = inject[AddressSearchResultView]
  lazy val backLink = "Back"
  lazy val caption = "Register for the business rates valuation service"
  private def  heading(postcode: String) = s"Search results for ${postcode}"
  lazy val previousButton = "Previous"
  lazy val nextButton = "Next"

  object Selectors {
    val backLink = "#content > a"
    val caption = "#content > form > span"
    val heading = "#content > form > h1"
    val previousButton = "#content > nav > div > a"
    val nextButton = "#content > nav > div.govuk-pagination__next > a"
  }

  override val testAddressModel: Address =
    Address(line1 = "99",
      line2 = Some("Wibble Rd"),
      town = "Worthing",
      county = Some("West Sussex"),
      postcode = Postcode("BN110AA"),
      country = "UK",
    )

  val testAddressModel2: Address =
    Address(line1 = "100",
      line2 = Some("Croft Rd"),
      town = "Uxbridge",
      county = Some("Hillingdon"),
      postcode = Postcode("BN110AA"),
      country = "UK",
    )

  val testAddressModel3: Address =
    Address(line1 = "20",
      line2 = Some("Long Rd"),
      town = "Bournemouth",
      county = Some("Dorset"),
      postcode = Postcode("BN110AA"),
      country = "UK",
    )

  lazy val testAddressList = Seq(testAddressModel3, testAddressModel, testAddressModel2, testAddressModel, testAddressModel, testAddressModel2, testAddressModel, testAddressModel, testAddressModel, testAddressModel, testAddressModel, testAddressModel, testAddressModel, testAddressModel)
  lazy val testPostcode = testAddressModel.postcode.value

  def mockPaginatedAddress(page: Int, defaulPageSize: Int) =  PaginatedAddress(
    currentPage = page,
    total = testAddressList.length,
    pageSize = defaulPageSize,
    address = PaginatedAddress.pageAddress(currentPage = page,  pageSize = defaulPageSize, address = testAddressList),
    links =   PaginatedAddress.displayPaginateLinks(currentPage = page, total = testAddressList.length, pageSize = defaulPageSize)
  )

  val mockAddressTotal = testAddressList.length


  "AddressSearchResultView" must {

    "produce the same output for apply() and render()" in {
      val testPage = 1
      val testDefaultPageSize = 5





      val htmlApply = addressSearchResultView.apply(
        postcode = testPostcode,
        Some(mockPaginatedAddress(page = testPage, defaulPageSize = testDefaultPageSize)),
        mockAddressTotal,
        if(PaginatedAddress.pageTop(currentPage = testPage, pageSize = testDefaultPageSize)> testAddressList.length){testAddressList.length}
        else{PaginatedAddress.pageTop(currentPage = testPage, pageSize = testDefaultPageSize)},
        PaginatedAddress.pageBottom(currentPage = testPage, pageSize = testDefaultPageSize) + 1
      ).body

      val htmlRender = addressSearchResultView.render(postcode = testPostcode,
        Some(mockPaginatedAddress(page = testPage, defaulPageSize = testDefaultPageSize)),
        mockAddressTotal,
        if(PaginatedAddress.pageTop(currentPage = testPage, pageSize = testDefaultPageSize)> testAddressList.length){testAddressList.length}
        else{PaginatedAddress.pageTop(currentPage = testPage, pageSize = testDefaultPageSize)},
        PaginatedAddress.pageBottom(currentPage = testPage, pageSize = testDefaultPageSize) + 1, request, messages, mockConfig).body

      htmlApply mustBe htmlRender
      lazy implicit val document: Document = Jsoup.parse(addressSearchResultView( postcode = testPostcode,
        Some(mockPaginatedAddress(page = testPage, defaulPageSize = testDefaultPageSize)),
        mockAddressTotal,
        if(PaginatedAddress.pageTop(currentPage = testPage, pageSize = testDefaultPageSize)> testAddressList.length){testAddressList.length}
        else{PaginatedAddress.pageTop(currentPage = testPage, pageSize = testDefaultPageSize)},
        PaginatedAddress.pageBottom(currentPage = testPage, pageSize = testDefaultPageSize) + 1)(request, messages, mockConfig).body)
      elementText(Selectors.backLink) mustBe backLink
      elementText(Selectors.caption) mustBe caption
      elementText(Selectors.heading)   mustBe heading(testPostcode)
    }
  }
}
