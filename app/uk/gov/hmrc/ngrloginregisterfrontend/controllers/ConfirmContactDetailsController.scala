/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.ngrloginregisterfrontend.controllers

import play.api.i18n.{I18nSupport, Messages}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.{CitizenDetailsConnector, NGRConnector}
import uk.gov.hmrc.ngrloginregisterfrontend.controllers.auth.AuthJourney
import uk.gov.hmrc.ngrloginregisterfrontend.models._
import uk.gov.hmrc.ngrloginregisterfrontend.models.cid.PersonDetails
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.{CredId, RatepayerRegistrationValuation}
import uk.gov.hmrc.ngrloginregisterfrontend.utils.SummaryListHelper
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.ConfirmContactDetailsView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ConfirmContactDetailsController @Inject()(view: ConfirmContactDetailsView,
                                                authenticate: AuthJourney,
                                                connector: NGRConnector,
                                                mcc: MessagesControllerComponents,
                                                citizenDetailsConnector: CitizenDetailsConnector)(implicit appConfig: AppConfig, ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport with SummaryListHelper {

  def show(): Action[AnyContent] =
    authenticate.authWithUserDetails.async { implicit request =>
      val credId = CredId(request.credId.getOrElse(""))
      val authNino = Nino(request.nino.nino.getOrElse(throw new Exception("No nino found from auth")))
      val email = Email(request.email.getOrElse(""))

      connector.getRatepayer(credId).flatMap {
        case Some(ratepayer) =>
          val name = ratepayer.ratepayerRegistration.flatMap(_.name).map(_.value).getOrElse("")
          Future.successful(Ok(view(createContactDetailSummaryRows(ratepayer), name)))

        case None =>
          citizenDetailsConnector.getPersonDetails(authNino).flatMap {
            case Left(error) =>
              Future.successful(Status(error.code)(Json.toJson(error)))

            case Right(personDetails) =>
              val nameValue = name(personDetails)
              val ratepayerRegistration = RatepayerRegistration(
                nino = Some(authNino),
                name = Some(Name(nameValue)),
                email = Some(email),
                address = Some(buildAddress(personDetails))
              )

              val ratepayerData = RatepayerRegistrationValuation(credId, Some(ratepayerRegistration))

              connector.upsertRatepayer(ratepayerData).map { _ =>
                Ok(view(createSummaryRows(personDetails, request), nameValue))
              }
          }
      }
    }

  private def buildAddress(personDetails: PersonDetails): Address =
    Address(
      line1 = personDetails.address.line1.getOrElse(""),
      line2 = personDetails.address.line2,
      town = personDetails.address.line4.getOrElse(""),
      county = personDetails.address.line5,
      postcode = Postcode(personDetails.address.postcode.getOrElse("")),
      country = personDetails.address.country.getOrElse("")
    )

  def name(personDetails: PersonDetails): String = List(
    personDetails.person.firstName,
    personDetails.person.middleName,
    personDetails.person.lastName
  ).flatten.mkString(" ")

  private[controllers] def createSummaryRows(personDetails: PersonDetails, request: AuthenticatedUserRequest[AnyContent])(implicit messages: Messages): SummaryList = {
    val address: Seq[String] = Seq(
      personDetails.address.line1.getOrElse(""),
      personDetails.address.line2.getOrElse(""),
      personDetails.address.line3.getOrElse(""),
      personDetails.address.line4.getOrElse(""),
      personDetails.address.line5.getOrElse(""),
      personDetails.address.postcode.getOrElse(""),
      personDetails.address.country.getOrElse("")
    ).filter(_.nonEmpty)

    SummaryList(deriveNGRSummaryRows(Seq(name(personDetails)), Seq(request.email.getOrElse("")), Seq.empty, address))
  }
}
