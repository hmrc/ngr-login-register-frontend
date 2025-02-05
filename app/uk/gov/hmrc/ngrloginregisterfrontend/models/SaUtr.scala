package uk.gov.hmrc.ngrloginregisterfrontend.models

import play.api.libs.json.{Json, OFormat}

case class SaUtr(value: String)

object SaUtr {
  implicit val format: OFormat[SaUtr] = Json.format[SaUtr]
}