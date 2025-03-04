package uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup

import play.api.libs.json.{Json, Writes}

case class LanguageLabels(
                           appLevelLabels: Option[AppLevelLabels] = None,
                           selectPageLabels: Option[SelectPageLabels] = None,
                           lookupPageLabels: Option[LookupPageLabels] = None,
                           editPageLabels: Option[EditPageLabels] = None,
                           confirmPageLabels: Option[ConfirmPageLabels] = None,
                           countryPickerLabels: Option[CountryPickerPageLabels] = None)

case class AppLevelLabels(
                           navTitle: Option[String] = None,
                           phaseBannerHtml: Option[String] = None)

case class SelectPageLabels(
                             title: Option[String] = None,
                             heading: Option[String] = None,
                             headingWithPostcode: Option[String] = None,
                             proposalListLabel: Option[String] = None,
                             submitLabel: Option[String] = None,
                             searchAgainLinkText: Option[String] = None,
                             editAddressLinkText: Option[String] = None)

case class LookupPageLabels(
                             title: Option[String] = None,
                             heading: Option[String] = None,
                             afterHeadingText: Option[String] = None,
                             filterLabel: Option[String] = None,
                             postcodeLabel: Option[String] = None,
                             submitLabel: Option[String] = None,
                             noResultsFoundMessage: Option[String] = None,
                             resultLimitExceededMessage: Option[String] = None,
                             manualAddressLinkText: Option[String] = None)

case class EditPageLabels(
                           title: Option[String] = None,
                           heading: Option[String] = None,
                           line1Label: Option[String] = None,
                           line2Label: Option[String] = None,
                           line3Label: Option[String] = None,
                           townLabel: Option[String] = None,
                           postcodeLabel: Option[String] = None,
                           countryLabel: Option[String] = None,
                           submitLabel: Option[String] = None,
                         )

case class ConfirmPageLabels(
                              title: Option[String] = None,
                              heading: Option[String] = None,
                              infoSubheading: Option[String] = None,
                              infoMessage: Option[String] = None,
                              submitLabel: Option[String] = None,
                              searchAgainLinkText: Option[String] = None,
                              changeLinkText: Option[String] = None,
                              confirmChangeText: Option[String] = None)

case class CountryPickerPageLabels(
                                    title: Option[String] = None,
                                    heading: Option[String] = None,
                                    countryLabel: Option[String] = None,
                                    submitLabel: Option[String] = None)

object LanguageLabels {

  implicit val appLevelWrites: Writes[AppLevelLabels] = Json.writes[AppLevelLabels]
  implicit val selectPageWrites: Writes[SelectPageLabels] = Json.writes[SelectPageLabels]
  implicit val lookupPageWrites: Writes[LookupPageLabels] = Json.writes[LookupPageLabels]
  implicit val editPageWrites: Writes[EditPageLabels] = Json.writes[EditPageLabels]
  implicit val confirmPageWrites: Writes[ConfirmPageLabels] = Json.writes[ConfirmPageLabels]
  implicit val countryPickerPageWrites: Writes[CountryPickerPageLabels] = Json.writes[CountryPickerPageLabels]
  implicit val languageLabelsWrites: Writes[LanguageLabels] = Json.writes[LanguageLabels]
}
