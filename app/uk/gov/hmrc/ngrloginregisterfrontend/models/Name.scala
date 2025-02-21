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

import play.api.data.Form
import play.api.data.Forms.{mapping, text}
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.CommonFormValidators

final case class Name(value: String)

object Name extends CommonFormValidators{
  implicit val format: OFormat[Name] = Json.format[Name]

  lazy val nameEmptyError    = "name.empty.error"
  val name                   = "name.value"

  def form(): Form[Name] =
    Form(
      mapping(
        name -> text()
          .verifying(nameEmptyError, isNonEmpty)
      )(Name.apply)(Name.unapply)
    )
}