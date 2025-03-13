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

import uk.gov.hmrc.govukfrontend.views.viewmodels.pagination.{Pagination, PaginationItem, PaginationLink}

case class PaginationData(
                           totalPages: Int,
                           currentPage: Int,
                           baseUrl: String,
                           pageSize: Int
                         ) {
  def toPagination: Pagination = {

    val items = (1 to totalPages).map { pageNumber =>

      PaginationItem(
        href = s"$baseUrl/$pageNumber",
        number = Some(pageNumber.toString),
        current = if (pageNumber == currentPage) Some(true) else None
      )
    }

    Pagination(
      items = Some(items),
      previous = if (currentPage > 1) Some(PaginationLink(href = s"$baseUrl/${currentPage - 1}")) else None,
      next = if (currentPage < totalPages) Some(PaginationLink(href = s"$baseUrl/${currentPage + 1}")) else None
    )
  }
}

object PaginationData {

  def pageTop (currentPage: Int, pageSize: Int, totalLength: Int): Int= {
    if(currentPage * pageSize > totalLength) totalLength else currentPage * pageSize
  }

  def pageBottom (currentPage: Int, pageSize: Int): Int = {
    (currentPage * pageSize) - pageSize
  }

  def getPage[T](currentPage: Int, pageSize: Int, list: Seq[T]):Seq[T] = {
    list.slice(pageBottom(currentPage, pageSize), pageTop(currentPage, pageSize, list.length))
  }

}
