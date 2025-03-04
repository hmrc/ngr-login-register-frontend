package uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup

import play.api.libs.json.{Json, Writes}

case class JourneyConfig(
                          version: Int,
                          options: JourneyOptions,
                          labels: Option[JourneyLabels] = None,
                          requestedVersion: Option[Int] = None)

case class JourneyOptions(
                           continueUrl: String,
                           homeNavHref: Option[String] = None,
                           signOutHref: Option[String] = None,
                           accessibilityFooterUrl: Option[String] = None,
                           phaseFeedbackLink: Option[String] = None,
                           deskProServiceName: Option[String] = None,
                           showPhaseBanner: Option[Boolean] = None,
                           alphaPhase: Option[Boolean] = None,
                           showBackButtons: Option[Boolean] = None,
                           disableTranslations: Option[Boolean] = None,
                           includeHMRCBranding: Option[Boolean] = None,
                           ukMode: Option[Boolean] = None,
                           allowedCountryCodes: Option[Set[String]] = None,
                           selectPageConfig: Option[SelectPageConfig] = None,
                           confirmPageConfig: Option[ConfirmPageConfig] = None,
                           timeoutConfig: Option[TimeoutConfig] = None,
                           serviceHref: Option[String] = None,
                           pageHeadingStyle: Option[String] = None)

case class SelectPageConfig(
                             proposalListLimit: Option[Int] = None,
                             showSearchAgainLink: Option[Boolean] = None)

case class ConfirmPageConfig(
                              showSearchAgainLink: Option[Boolean] = None,
                              showSubHeadingAndInfo: Option[Boolean] = None,
                              showChangeLink: Option[Boolean] = None,
                              showConfirmChangeText: Option[Boolean] = None)

case class TimeoutConfig(
                          timeoutAmount: Int,
                          timeoutUrl: String,
                          timeoutKeepAliveUrl: Option[String] = None)

case class JourneyLabels(en: Option[LanguageLabels] = None)

object JourneyLabels {
  implicit val writes: Writes[JourneyLabels] = Json.writes[JourneyLabels]
}

object JourneyConfig {
  implicit val labelsFormat: Writes[JourneyLabels] = Json.writes[JourneyLabels]

  implicit val format: Writes[JourneyConfig] = Json.writes[JourneyConfig]
}

object JourneyOptions {
  implicit val format: Writes[JourneyOptions] = Json.writes[JourneyOptions]
}

object SelectPageConfig {
  implicit val format: Writes[SelectPageConfig] = Json.writes[SelectPageConfig]
}

object ConfirmPageConfig {
  implicit val format: Writes[ConfirmPageConfig] = Json.writes[ConfirmPageConfig]
}
object TimeoutConfig {
  implicit val format: Writes[TimeoutConfig] = Json.writes[TimeoutConfig]
}