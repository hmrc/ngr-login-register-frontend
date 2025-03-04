package uk.gov.hmrc.ngrloginregisterfrontend.controllers.addressLookup

import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.JWTCookieDataCodec.JWTIDGenerator.generateId
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.AddressLookupConnector
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.httpParsers.ResponseHttpParser.HttpResult
import uk.gov.hmrc.ngrloginregisterfrontend.models.AlfResponse
import uk.gov.hmrc.ngrloginregisterfrontend.models.addressLookup.{AppLevelLabels, ConfirmPageConfig, EditPageLabels, JourneyConfig, JourneyLabels, JourneyOptions, LanguageLabels, LookupPageLabels, SelectPageConfig}
import uk.gov.hmrc.ngrloginregisterfrontend.util.AddressHelper
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddressLookupController  @Inject()(
                                                                       mcc: MessagesControllerComponents,
                                                                       addressLookupConnector: AddressLookupConnector,
                                                                       frontendAppConfig: AppConfig)(implicit ec: ExecutionContext)
  extends FrontendController(mcc) with AddressHelper with I18nSupport{

  def initJourney(journeyConfig: JourneyConfig)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResult[String]] = {
    addressLookupConnector.initJourney(journeyConfig)
  }

  def getAddresses(alfId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[AlfResponse] = {
    addressLookupConnector.getAddress(alfId).map {
      case Right(addressResponse) => addressResponse
      case Left(error) => throw new Exception(s"Error returned from ALF for $alfId ${error.status} ${error.message} for ${hc.requestId}")
    }
  }


  def initJourneyAndReturnOnRampUrl(ngrId: String = generateId)(implicit hc: HeaderCarrier, ec: ExecutionContext, messages: Messages): Future[String] = {
    val journeyConfig: JourneyConfig = createJourneyConfig(ngrId)
    initJourney(journeyConfig).map {
      case Right(onRampUrl) => onRampUrl
      case Left(error) => throw new Exception(s"Failed to init ALF ${error.message} with status ${error.status} for ${hc.requestId}")
    }
  }

  def createJourneyConfig(ngrId: String)(implicit messages: Messages): JourneyConfig = {
    JourneyConfig(
      version = frontendAppConfig.AddressLookupConfig.version,
      options = JourneyOptions(
        continueUrl = frontendAppConfig.AddressLookupConfig.ContactAddress.offRampUrl(ngrId),
        homeNavHref = None,
        signOutHref = None,
        accessibilityFooterUrl = Some(frontendAppConfig.accessibilityFooterUrl),
        phaseFeedbackLink = None,
        deskProServiceName = None,
        showPhaseBanner = Some(false),
        alphaPhase = None,
        includeHMRCBranding = Some(true),
        ukMode = Some(true),
        selectPageConfig = Some(SelectPageConfig(
          proposalListLimit = Some(frontendAppConfig.AddressLookupConfig.selectPageConfigProposalLimit),
          showSearchAgainLink = Some(true))),
        showBackButtons = Some(true),
        disableTranslations = Some(true),
        allowedCountryCodes = None,
        confirmPageConfig = Some(ConfirmPageConfig(
          showSearchAgainLink = Some(true),
          showSubHeadingAndInfo = Some(true),
          showChangeLink = Some(true),
          showConfirmChangeText = Some(true))),
        timeoutConfig = None,
        pageHeadingStyle = Some("govuk-heading-l")),
      labels = Some(
        JourneyLabels(
          en = Some(LanguageLabels(
            appLevelLabels = Some(AppLevelLabels(
              navTitle = Some(messages("service.name")),
              phaseBannerHtml = None)),
            selectPageLabels = None,
            lookupPageLabels = Some(
              LookupPageLabels(
                title = Some(messages("addressLookupFrontend.contactDetails.lookupPageLabels.title")),
                heading = Some(messages("addressLookupFrontend.contactDetails.lookupPageLabels.title")),
                postcodeLabel = Some(messages("addressLookupFrontend.contactDetails.lookupPageLabels.postcodeLabel")))),
            editPageLabels = Some(
              EditPageLabels(
                title = Some(messages("addressLookupFrontend.contactDetails.editPageLabels.title")),
                heading = Some(messages("addressLookupFrontend.contactDetails.editPageLabels.title")),
                line1Label = Some(messages("addressLookupFrontend.contactDetails.editPageLabels.line1Label")),
                line2Label = Some(messages("addressLookupFrontend.contactDetails.editPageLabels.line2Label")),
                line3Label = Some(messages("addressLookupFrontend.contactDetails.editPageLabels.line3Label")),
                townLabel = Some(messages("addressLookupFrontend.contactDetails.editPageLabels.townLabel")),
                postcodeLabel = Some(messages("addressLookupFrontend.contactDetails.editPageLabels.postcodeLabel")),
              )),
            confirmPageLabels = None,
            countryPickerLabels = None)))
      ),
      requestedVersion = None)
  }
}