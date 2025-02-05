package uk.gov.hmrc.ngrloginregisterfrontend.models.cid

import play.api.libs.json.{Json, OFormat}

case class CidName(current: CurrentName)

object CidName {
  implicit val format: OFormat[CidName] = Json.format[CidName]
}
