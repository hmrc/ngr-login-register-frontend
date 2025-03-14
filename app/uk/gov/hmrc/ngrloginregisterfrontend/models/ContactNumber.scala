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

import play.api.libs.json.{Format, Json}

final case class ContactNumber(value: String) {
  private val contactNumberRegex = "^(?:0|(\\+|00)\\d\\d\\s?)?(?:\\d\\s?){9,10}$"
  def isValidContactNumber: Boolean = value.trim.matches(contactNumberRegex)
}

object ContactNumber {
  implicit val format: Format[ContactNumber] = Json.format[ContactNumber]
}