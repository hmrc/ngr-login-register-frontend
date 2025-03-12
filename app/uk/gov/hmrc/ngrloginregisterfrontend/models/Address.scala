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
import play.api.data.Forms.{mapping, optional, text}
import play.api.libs.json.{Json, OFormat}
import play.api.data.validation.Constraints.maxLength
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.CommonFormValidators
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.mappings.Constraints

final case class Address(line1: String,
                         line2: Option[String],
                         town: String,
                         county: Option[String],
                         postcode: Postcode,
                         country: String = "GB") {
  override def toString: String = Seq(line1, line2.getOrElse(""), town, county.getOrElse(""), postcode.toString, country).mkString(", ")
}

object Address extends CommonFormValidators with Constraints{
  implicit val format: OFormat[Address] = Json.format[Address]

  private val maxLineLength: Int = 128
  private val maxCityLength: Int = 64

  def form():Form[Address] =
    Form(
      mapping(
        "AddressLine1" -> text()
            .verifying(
              firstError(maxLength(maxLineLength, "agentImporterManualAddress.line1.error.length"))),
        "AddressLine2" -> optional(
            text()
              .verifying(
                firstError(maxLength(maxLineLength, "agentImporterManualAddress.line2.error.length"))
              )
          ),
        "City" ->
          text()
            .verifying(
              firstError(maxLength(maxCityLength, "agentImporterManualAddress.city.error.length"))),
        "PostalCode" ->
          textNoSpaces("postcode.error.required")
            .verifying(
              firstError(
                minLength(minPostalCodeLength, "agentImporterManualAddress.postalCode.error.invalid"),
                maxLength(maxPostalCodeLength, "agentImporterManualAddress.postalCode.error.invalid")
              )
            )
      )(Address.apply)(Address.unapply)
    )
}