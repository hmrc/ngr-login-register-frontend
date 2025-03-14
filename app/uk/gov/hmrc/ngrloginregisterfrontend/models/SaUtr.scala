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

import play.api.libs.json._
import uk.gov.hmrc.domain.{SimpleObjectReads, SimpleObjectWrites}

final case class SaUtr(value: String) {
  require(SaUtr.isValid(value), s"$value is not a valid sautr.")
}

object SaUtr {

  implicit val saUtrWrite: Writes[SaUtr] = new SimpleObjectWrites[SaUtr](_.value)
  implicit val saUtrRead: Reads[SaUtr] = new SimpleObjectReads[SaUtr]("sautr", SaUtr.apply)

  private val validSaUtrFormat = "[0-9]{10}"

  def isValid(saUtr: String): Boolean = saUtr.matches(validSaUtrFormat)


}
