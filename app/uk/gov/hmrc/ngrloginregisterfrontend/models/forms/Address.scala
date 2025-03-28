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

final case class Address(line1: String,
                         line2: Option[String],
                         town: String,
                         county: Option[String],
                         postcode: Postcode,
                         ) {
  override def toString: String = Seq(line1, line2.getOrElse(""), town, county.getOrElse(""), postcode.toString).mkString(", ")
}

object Address extends CommonFormValidators {
  implicit val format: OFormat[Address] = Json.format[Address]

  private val maxLineLength: Int = 128
  private val maxCityLength: Int = 64
  private lazy val postcodeEmptyError    = "ManualAddressSearch.postalCode.error.invalid"
  private lazy val invalidPostcodeError  = "ManualAddressSearch.postalCode.error.invalid"
  val postcode                   = "postcode-value"

  def form():Form[Address] =
    Form(
      mapping(
        "AddressLine1" -> text()
            .verifying(
              firstError(maxLength(maxLineLength, maxLengthErrorMessage(maxLineLength)))),
        "AddressLine2" -> optional(
            text()
              .verifying(
                firstError(maxLength(maxLineLength, maxLengthErrorMessage(maxLineLength)))
              )
          ),
        "City" ->
          text()
            .verifying(
              firstError(maxLength(maxCityLength, maxLengthErrorMessage(maxCityLength)))),
        "County" -> optional(
          text()
            .verifying(
              firstError(maxLength(maxLineLength, maxLengthErrorMessage(maxLineLength)))
            )
        ),
        "PostalCode" ->
          text()
            .verifying(
              firstError(
                isNotEmpty(postcode, postcodeEmptyError),
                regexp(postcodeRegexPattern.pattern(), invalidPostcodeError)
              )
            ) .transform[Postcode](Postcode.apply, _.value)
      )(Address.apply)(Address.unapply)
    )
}