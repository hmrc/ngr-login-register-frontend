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

package uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup

import play.api.libs.json.{Format, Json}

final case class AddressLookupResponses(addressLookupResponses: Seq[AddressLookupResponse])

object AddressLookupResponses {
  implicit val format: Format[AddressLookupResponses] = Json.format[AddressLookupResponses]
}

final case class AddressLookupResponse (
                                   id: String,
                                   uprn: Int,
                                   parentUprn: Option[Int],
                                   usrn: Option[Int],
                                   organisation: Option[String],
                                   address: Address,
                                   localCustodian: Option[LocalCustodian],
                                   location: Option[Seq[Int]],
                                   language: String,
                                   administrativeArea: Option[String],
                                   poBox: Option[String]
                                 )

object AddressLookupResponse {
  implicit val format: Format[AddressLookupResponse] = Json.format[AddressLookupResponse]
}

final case class Address(
                      lines: Seq[String],
                      town: String,
                      postcode: String,
                      subdivision: Option[Subdivision],
                      country: Subdivision
                    )

object Address {
  implicit val format: Format[Address] = Json.format[Address]
}


final case class Subdivision(
                          code: String,
                          name: String
                        )

object Subdivision {
  implicit val format: Format[Subdivision] = Json.format[Subdivision]
}

final case class LocalCustodian(
                             code: Int,
                             name: String
                           )

object LocalCustodian {
  implicit val format: Format[LocalCustodian] = Json.format[LocalCustodian]
}
