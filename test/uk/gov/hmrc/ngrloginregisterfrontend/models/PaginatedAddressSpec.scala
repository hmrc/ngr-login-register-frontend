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

package uk.gov.hmrc.ngrloginregisterfrontend.models

import org.scalatest.OptionValues
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class PaginatedAddressSpec extends AnyWordSpecLike with Matchers with OptionValues {

  val testAddressModel: Address =
    Address(line1 = "99",
      line2 = Some("Wibble Rd"),
      town = "Worthing",
      county = Some("West Sussex"),
      postcode = Postcode("BN110AA"),
      country = "UK",
    )

    val paginatedAddress = PaginatedAddress(
      currentPage = 1,
      total = 102,
      pageSize = 5,
      address = (1 to 5).map(x => testAddressModel),
      links = Seq.empty[PaginateLink]
    )

    "paginate method" should {
      "return pagination numbers according to the parameters of PaginatedUtrs class" in {
        paginatedAddress.paginate shouldBe Seq(1, 2, 3, 4, 5)
        paginatedAddress.copy(currentPage = 2).paginate shouldBe Seq(2, 3, 4, 5, 6)
        paginatedAddress.copy(currentPage = 3).paginate shouldBe Seq(3, 4, 5, 6, 7)
        paginatedAddress.copy(currentPage = 16).paginate shouldBe Seq(16, 17, 18, 19, 20)
        paginatedAddress.copy(currentPage = 19).paginate shouldBe Seq(17, 18, 19, 20, 21)
        paginatedAddress.copy(currentPage = 20).paginate shouldBe Seq(17, 18, 19, 20, 21)
        paginatedAddress
          .copy(currentPage = 21, address = (1 to 2).map(x => testAddressModel))
          .paginate shouldBe Seq(17, 18, 19, 20, 21)

        paginatedAddress.copy(currentPage = 0).paginate shouldBe Seq.empty
        paginatedAddress.copy(total = 0).paginate shouldBe Seq.empty

        paginatedAddress
          .copy(total = 21, currentPage = 1, pageSize = 3)
          .paginate shouldBe Seq(1, 2, 3, 4, 5)
        paginatedAddress
          .copy(total = 21, currentPage = 2, pageSize = 3)
          .paginate shouldBe Seq(2, 3, 4, 5, 6)
        paginatedAddress
          .copy(total = 21, currentPage = 3, pageSize = 3)
          .paginate shouldBe Seq(3, 4, 5, 6, 7)
        paginatedAddress
          .copy(total = 21, currentPage = 4, pageSize = 3)
          .paginate shouldBe Seq(3, 4, 5, 6, 7)
        paginatedAddress
          .copy(total = 21, currentPage = 5, pageSize = 3)
          .paginate shouldBe Seq(3, 4, 5, 6, 7)
        paginatedAddress
          .copy(total = 21, currentPage = 6, pageSize = 3)
          .paginate shouldBe Seq(3, 4, 5, 6, 7)
        paginatedAddress
          .copy(total = 21, currentPage = 7, pageSize = 3)
          .paginate shouldBe Seq(3, 4, 5, 6, 7)
      }
    }

    "resourcesFrom and resourcesTo methods" should {
      "return number of starting resources and end resources in each page according to the parameters of PaginatedUtrs class" in {
        paginatedAddress.resourcesFrom shouldBe 1
        paginatedAddress.resourcesTo shouldBe 5

        paginatedAddress.copy(currentPage = 2).resourcesFrom shouldBe 6
        paginatedAddress.copy(currentPage = 2).resourcesTo shouldBe 10

        paginatedAddress.copy(currentPage = 20).resourcesFrom shouldBe 96
        paginatedAddress.copy(currentPage = 20).resourcesTo shouldBe 100

        paginatedAddress
          .copy(currentPage = 21, address = (1 to 2).map(x => testAddressModel))
          .resourcesFrom shouldBe 101
        paginatedAddress
          .copy(currentPage = 21, address = (1 to 2).map(x => testAddressModel))
          .resourcesTo shouldBe 102
      }
    }
}
