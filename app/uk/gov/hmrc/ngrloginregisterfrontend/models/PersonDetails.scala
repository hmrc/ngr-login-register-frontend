package uk.gov.hmrc.ngrloginregisterfrontend.models

import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.auth.core.Nino

final case class PersonDetails(firstName: String,
                               lastName: String,
                               title: String,
                               sex: String,
                               dateOfBrith: String,
                               nino: Nino,
                               deceased: Boolean)

object PersonDetails {
  implicit val format: Format[PersonDetails] = Json.format[PersonDetails]
}
