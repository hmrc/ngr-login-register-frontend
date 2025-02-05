package uk.gov.hmrc.ngrloginregisterfrontend.models

import play.api.libs.json.{Json, OFormat}

case class Nino(value: String)

object Nino {
  implicit val format: OFormat[Nino] = Json.format[Nino]
}