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
import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.CommonFormValidators

final case class Email(value: String) {
  override def toString: String = value
  private val emailRegex = """[a-zA-Z0-9]+([-+.'][a-zA-Z0-9]+)*@[a-zA-Z0-9]+([-.][a-zA-Z0-9]+)*\.[a-zA-Z0-9]+([-.][a-zA-Z0-9]+)*"""
  def isValidEmail: Boolean = value.matches(emailRegex)
}

object Email extends CommonFormValidators {
  implicit val format: Format[Email] = Json.format[Email]
  lazy val emailEmptyError          = "email.empty.error"
  lazy val emailInvalidFormat       = "email.invalidFormat.error"
  val maxLength                     = 24
  val email                   = "email-value"

  def form(): Form[Email] =
    Form(
      mapping(
        email -> text()
          .verifying(emailEmptyError, isNonEmpty)
          .verifying(emailInvalidFormat, isValidEmail)
      )(Email.apply)(Email.unapply)
    )
}