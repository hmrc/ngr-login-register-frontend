package uk.gov.hmrc.ngrloginregisterfrontend.mocks


import play.api.libs.json.{Format, JsError, JsString, JsSuccess, Reads, Writes}
import play.twirl.api.JavaScript
import uk.gov.hmrc.ngrloginregisterfrontend.models.RadioEntry

sealed trait YesNoItem extends RadioEntry

case object Yes extends YesNoItem
case object No extends YesNoItem

object MockYesNoRadioItem {

  implicit val format : Format[RadioEntry] = Format(
    Reads {
      case JsString("Yes") => JsSuccess(Yes)
      case JsString("No") => JsSuccess(No)
      case _ => JsError("Error")
    },
    Writes {
      case Yes => JsString("Yes")
      case No => JsString("No")
    }
  )
}