package uk.gov.hmrc.ngrloginregisterfrontend.models.cid

import play.api.libs.json.{JsPath, Reads}
import uk.gov.hmrc.ngrloginregisterfrontend.models.SaUtr

case class MatchingDetails(firstName: Option[String],
                           lastName: Option[String],
                           saUtr: Option[SaUtr] = None
                          )

object MatchingDetails {
  implicit val reads: Reads[MatchingDetails] = {
    val current = JsPath \ "name" \ "current"
    for {
      firstName <- (current \ "firstName").readNullable[String]
      lastName <- (current \ "lastName").readNullable[String]
      utr <- (JsPath \ "ids" \ "sautr").readNullable[SaUtr]
    } yield MatchingDetails(firstName,lastName,utr)

  }
}
