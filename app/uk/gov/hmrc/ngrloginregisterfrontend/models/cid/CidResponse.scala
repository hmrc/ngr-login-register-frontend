package uk.gov.hmrc.ngrloginregisterfrontend.models.cid

import play.api.libs.json.{Json, OFormat}

final case class CidResponse(name: CidName,
                             ids: TaxIds,
                             dateOfBirth: String
                            )

object CidResponse {
  implicit val format: OFormat[CidResponse] = Json.format[CidResponse]
}