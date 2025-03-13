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

import uk.gov.hmrc.govukfrontend.views.Aliases.{PaginationItem, PaginationLink}
import uk.gov.hmrc.govukfrontend.views.viewmodels.pagination.Pagination
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.ViewBaseSpec
import uk.gov.hmrc.ngrloginregisterfrontend.models.PaginationData

class PaginationDataSpec extends ViewBaseSpec {
  val mockPaginationData: PaginationData = PaginationData(totalPages = 5, currentPage = 1, baseUrl = "baseUrl", pageSize = 5)
  "PaginationData" when {
    "toPagination produces Pagination" in {
      mockPaginationData.toPagination mustBe Pagination(
        items = Some(
          Seq(
            PaginationItem("baseUrl/1", number = Some("1"), current = Some(true)),
            PaginationItem("baseUrl/2", Some("2")),
            PaginationItem("baseUrl/3", Some("3")),
            PaginationItem("baseUrl/4", Some("4")),
            PaginationItem("baseUrl/5", Some("5")))
        ),
        next = Some(PaginationLink("baseUrl/2")))
    }

    "empty data produces Pagination" in {
      mockPaginationData.copy(totalPages = 0, currentPage = 0, baseUrl = "", pageSize = 0).toPagination mustBe Pagination(Some(Seq()))
    }
  }
}
