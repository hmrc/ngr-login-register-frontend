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

package uk.gov.hmrc.ngrloginregisterfrontend.models.forms

import play.api.data.Form
import play.api.data.Forms.{mapping, optional, text}
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.ngrloginregisterfrontend.models.Postcode

final case class FindAddress(postcode: Postcode, propertyName: Option[String]) {
  override def toString: String = Seq(propertyName, postcode.value).mkString(",")
}

object FindAddress extends CommonFormValidators {
  implicit val format: OFormat[FindAddress] = Json.format[FindAddress]

  private lazy val postcodeEmptyError    = "postcode.empty.error"
  private lazy val invalidPostcodeError  = "postcode.invalid.error"
  private val postcode                   = "postcode-value"
  private val propertyName               = "property-name-value"

  def form(): Form[FindAddress] =
    Form(
      mapping(
        postcode -> text()
          .transform[String](_.strip(), identity)
          .verifying(
            firstError(
              isNotEmpty(postcode, postcodeEmptyError),
              regexp(postcodeRegexPattern.pattern(), invalidPostcodeError)
            )
          )
          .transform[Postcode](Postcode.apply, _.value),
        propertyName -> optional(text
          .verifying(maxLength(100, maxLengthErrorMessage(100))))
      )(FindAddress.apply)(FindAddress.unapply)
    )
}