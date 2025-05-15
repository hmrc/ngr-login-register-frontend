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
import uk.gov.hmrc.ngrloginregisterfrontend.actions.{AuthRetrievals, HasMandotoryDetailsAction, RegistrationAction}
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.{CitizenDetailsConnector, NGRConnector}
import uk.gov.hmrc.ngrloginregisterfrontend.models._
import uk.gov.hmrc.ngrloginregisterfrontend.models.cid.PersonDetails
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.{Address, Name}
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.{CredId, RatepayerRegistrationValuation}
import uk.gov.hmrc.ngrloginregisterfrontend.utils.SummaryListHelper
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.ConfirmContactDetailsView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ConfirmContactDetailsController @Inject()(view: ConfirmContactDetailsView,
                                                authenticate: AuthRetrievals,
                                                isRegisteredCheck: RegistrationAction,
                                                hasMandotoryDetailsAction: HasMandotoryDetailsAction,
                                                connector: NGRConnector,
                                                mcc: MessagesControllerComponents,
                                                citizenDetailsConnector: CitizenDetailsConnector)(implicit appConfig: AppConfig, ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport with SummaryListHelper {

  def show(manualEmail: Option[String] = None): Action[AnyContent] = {
    (authenticate andThen isRegisteredCheck andThen hasMandotoryDetailsAction andThen hasMandotoryDetailsAction).async { implicit request =>
        val credId = CredId(request.credId.value)

        val authNino = request.ratepayerRegistration.flatMap{ ratePayer =>
          ratePayer match {
            case ratePayer if ratePayer.nino.isDefined == true => ratePayer.nino
            case _ => throw new RuntimeException("No nino found from auth")
          }
        }.getOrElse(throw new RuntimeException("No ratepayerRegistration found from mongo"))

        val email = request.ratepayerRegistration.flatMap{ ratePayer =>
          ratePayer match {
            case ratePayer if ratePayer.email.isDefined == true => ratePayer.email
            case _ => throw new RuntimeException("No email found found from mongo")
          }
        }.getOrElse(throw new RuntimeException("No ratepayerRegistration found from mongo"))


        connector.getRatepayer(credId).flatMap {
          case Some(ratepayer) =>
            val maybeReg = ratepayer.ratepayerRegistration
            val name = maybeReg.flatMap(_.name).map(_.value).getOrElse("")

            def render(ratepayerToRender: RatepayerRegistrationValuation) =
              Ok(view(createContactDetailSummaryRows(ratepayerToRender, "CCD"), name))

            if (manualEmail.nonEmpty) {
              maybeReg match {
                case Some(reg) =>
                  val updatedReg = reg.copy(email = Some(email))
                  val updatedRatepayer = RatepayerRegistrationValuation(credId, Some(updatedReg))
                  connector.changeEmail(credId, email).map(_ => render(updatedRatepayer))
                case None =>
                  Future.failed(new IllegalStateException("Missing ratepayerRegistration"))
              }
            } else {
              Future.successful(render(ratepayer))
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
    (authenticate andThen isRegisteredCheck andThen hasMandotoryDetailsAction).async {
      Future.successful(Redirect(routes.ProvideTRNController.show()))
    }
  }
}
