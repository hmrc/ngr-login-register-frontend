package uk.gov.hmrc.ngrloginregisterfrontend.models

import play.api.libs.json.{Json, OFormat}

case class CidAddress(line1: Option[String],
                      line2: Option[String],
                      postcode: Option[String],
                      startDate: Option[String],
                      country: Option[String],
                      cidType: String
                     )

object CidAddress {
  implicit val format: OFormat[CidAddress] = Json.format[CidAddress]
}
