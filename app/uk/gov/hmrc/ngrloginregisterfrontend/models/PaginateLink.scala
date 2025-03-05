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

import play.api.libs.json.__
import play.api.libs.json.Json
import play.api.libs.json.Reads

sealed trait PaginateLink {
  val rel: String
  val href: String
}

case class SelfLink(override val href: String)     extends PaginateLink { val rel = "self"     }
case class PreviousLink(override val href: String) extends PaginateLink { val rel = "previous" }
case class NextLink(override val href: String)     extends PaginateLink { val rel = "next"     }
case class FirstLink(override val href: String)    extends PaginateLink { val rel = "first"    }
case class LastLink(override val href: String)     extends PaginateLink { val rel = "last"     }

object PaginateLink {
  implicit val selfLinkReads: Reads[SelfLink]         = Json.reads[SelfLink]
  implicit val previousLinkReads: Reads[PreviousLink] = Json.reads[PreviousLink]
  implicit val firstLinkReads: Reads[FirstLink]       = Json.reads[FirstLink]
  implicit val nextLinkReads: Reads[NextLink]         = Json.reads[NextLink]
  implicit val lastLinkReads: Reads[LastLink]         = Json.reads[LastLink]

  implicit val linkFormat: Reads[PaginateLink] =
    __.read[SelfLink]
      .map(x => x: PaginateLink)
      .orElse(__.read[PreviousLink].map(x => x: PaginateLink))
      .orElse(__.read[NextLink].map(x => x: PaginateLink))
      .orElse(__.read[FirstLink].map(x => x: PaginateLink))
      .orElse(__.read[LastLink].map(x => x: PaginateLink))
}
