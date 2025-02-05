package uk.gov.hmrc.ngrloginregisterfrontend.models.cid

import play.api.libs.json.{Json, OFormat}

case class CurrentName(firstName: String,
                       lastName: String)

object CurrentName {
  implicit val format: OFormat[CurrentName] = Json.format[CurrentName]
}