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

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class PaginatedAddress(currentPage: Int, total: Int, pageSize: Int, address: Seq[String], links: Seq[PaginateLink]) {
  val totalPages: Int = math.ceil(total.toFloat / pageSize.toFloat).toInt
  def paginate: Seq[Int] = {
    val fixedPaginationSetNumber = 5
    val slider                   = 4

    if (totalPages <= fixedPaginationSetNumber) {
      1 to totalPages
    } else {
      currentPage match {
        case 0 => Seq.empty
        case current if current > (totalPages - fixedPaginationSetNumber) && current <= totalPages =>
          (totalPages - slider) to totalPages
        case current if current > totalPages => Seq.empty
        case _                               => currentPage to (slider + currentPage)
      }
    }
  }
  def resourcesFrom: Int = currentPage * pageSize - pageSize + 1
  def resourcesTo: Int   = currentPage * pageSize - (pageSize - address.size)
}

object PaginatedAddress {
  implicit val paginatedUtrsReads: Reads[PaginatedAddress] = {
    (__ \ "page")
      .read[Int]
      .and((__ \ "total").read[Int])
      .and((__ \ "pageSize").read[Int])
      .and((__ \ "_links" \ "self").readNullable[SelfLink])
      .and((__ \ "_links" \ "first").readNullable[FirstLink])
      .and((__ \ "_links" \ "previous").readNullable[PreviousLink])
      .and((__ \ "_links" \ "next").readNullable[NextLink])
      .and((__ \ "_links" \ "last").readNullable[LastLink])
      .and((__ \ "resources").read[Seq[String]]) { (page, total, pageSize, self, first, prev, next, last, resources) =>
        PaginatedAddress(page, total, pageSize, resources, Seq(self, first, prev, next, last).flatten)
      }
  }

  def displayPaginateLinks (currentPage: Int, total: Int, pageSize: Int): Seq[PaginateLink] = {
    val totalPages: Int = math.ceil(total.toFloat / pageSize.toFloat).toInt
    totalPages match {
      case totalPage if totalPage == currentPage => Seq(SelfLink(""), PreviousLink(uk.gov.hmrc.ngrloginregisterfrontend.controllers.routes.AddressSearchResultController.show(currentPage - 1).url), FirstLink(""), LastLink(""))
      case totalPage if currentPage == 1 => Seq(SelfLink(""), NextLink(uk.gov.hmrc.ngrloginregisterfrontend.controllers.routes.AddressSearchResultController.show(currentPage + 1).url), FirstLink(""), LastLink(""))
      case _ => Seq(SelfLink(""), PreviousLink(uk.gov.hmrc.ngrloginregisterfrontend.controllers.routes.AddressSearchResultController.show(currentPage - 1).url), FirstLink(""), NextLink(uk.gov.hmrc.ngrloginregisterfrontend.controllers.routes.AddressSearchResultController.show(currentPage + 1).url), LastLink(""))
    }
  }

   def pageTop (currentPage: Int, pageSize: Int, totalAddress: Int): Int= {
    if(currentPage * pageSize > totalAddress) totalAddress else currentPage * pageSize
  }

   def pageBottom (currentPage: Int, pageSize: Int): Int = {
    (currentPage * pageSize) - pageSize
  }

  def pageAddress(currentPage: Int, pageSize: Int, address: Seq[String]):Seq[String] = {
    address.slice(pageBottom(currentPage, pageSize), pageTop(currentPage, pageSize, address.length))
  }

}