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

package uk.gov.hmrc.ngrloginregisterfrontend.models.cid

import play.api.libs.json.{Format, Json, OFormat}
import uk.gov.hmrc.domain.Nino

import java.time.LocalDate

final case class Person(
                   title:       Option[String],
                   firstName:   Option[String],
                   middleName:  Option[String],
                   lastName:    Option[String],
                   honours:     Option[String],
                   sex:         Option[String],
                   dateOfBirth: Option[LocalDate],
                   nino:        Option[Nino]
                 )

object Person {
  implicit val format: Format[Person] = Json.format[Person]
}

final case class PersonAddress(
                    line1:             Option[String] = None,
                    line2:             Option[String] = None,
                    line3:             Option[String] = None,
                    line4:             Option[String] = None,
                    line5:             Option[String] = None,
                    postcode:          Option[String] = None,
                    country:           Option[String] = None,
                    startDate:         Option[LocalDate] = None,
                    `type`:            Option[String] = None)

object PersonAddress {
  implicit val format: OFormat[PersonAddress] = Json.format[PersonAddress]
}

final case class PersonDetails(
    person: Person,
    address: PersonAddress
                        )

object PersonDetails {
  implicit val format: OFormat[PersonDetails] = Json.format[PersonDetails]
}