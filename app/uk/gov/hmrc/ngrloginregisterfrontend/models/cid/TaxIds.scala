package uk.gov.hmrc.ngrloginregisterfrontend.models.cid

import play.api.libs.json.{Json, OFormat}


case class TaxIds(sautr: String,
                  nino: String)

  object TaxIds {
    implicit val format: OFormat[TaxIds] = Json.format[TaxIds]
  }


