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

import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.{CitizenDetailsConnector, NGRConnector}
import uk.gov.hmrc.ngrloginregisterfrontend.controllers.auth.AuthJourney
import uk.gov.hmrc.ngrloginregisterfrontend.models._
import uk.gov.hmrc.ngrloginregisterfrontend.models.cid.PersonDetails
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.{Address, Email, Name, Nino}
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

  def show(manualEmail: Option[String] = None): Action[AnyContent] = {
    authenticate.authWithUserDetails.async { implicit request =>

      if (request.email.isEmpty && manualEmail.isEmpty) {
        Future.successful(Redirect(routes.EnterEmailController.show))
      } else {

        val credId = CredId(request.credId.getOrElse(""))
        val authNino = Nino(request.nino.nino.getOrElse(throw new RuntimeException("No nino found from auth")))
        val email = Email(manualEmail.getOrElse(request.email.getOrElse("")))

        connector.getRatepayer(credId).flatMap {
          case Some(ratepayer) =>

            if (manualEmail.nonEmpty) {
              connector.changeEmail(credId, email).flatMap { _ =>
                connector.getRatepayer(credId).flatMap {
                  case Some(updatedRatepayer) =>
                    val name = updatedRatepayer.ratepayerRegistration.flatMap(_.name).map(_.value).getOrElse("")
                    Future.successful(Ok(view(createContactDetailSummaryRows(updatedRatepayer, "CCD"), name)))

                  case None => Future.successful(Status(NOT_FOUND))
                }
              }
            } else {
              val name = ratepayer.ratepayerRegistration.flatMap(_.name).map(_.value).getOrElse("")
              Future.successful(Ok(view(createContactDetailSummaryRows(ratepayer, "CCD"), name)))
            }

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
                  Ok(view(createContactDetailSummaryRows(ratepayerData, "CCD"), nameValue))
                }
            }
        }
      }
    }
  }

  private def buildAddress(personDetails: PersonDetails): Address =
    Address(
      line1 = personDetails.address.line1.getOrElse(""),
      line2 = personDetails.address.line2,
      town = personDetails.address.line4.getOrElse(""),
      county = None,
      postcode =  Postcode(personDetails.address.postcode.getOrElse("")),
    )

  def name(personDetails: PersonDetails): String = List(
    personDetails.person.firstName,
    personDetails.person.middleName,
    personDetails.person.lastName
  ).flatten.mkString(" ")

  def submit(): Action[AnyContent] = {
    authenticate.authWithUserDetails.async {
      Future.successful(Redirect(routes.ProvideTRNController.show()))
    }
  }
}
