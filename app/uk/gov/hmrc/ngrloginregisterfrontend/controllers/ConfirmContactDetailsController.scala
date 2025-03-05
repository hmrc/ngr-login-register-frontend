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
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.{CitizenDetailsConnector, NGRConnector}
import uk.gov.hmrc.ngrloginregisterfrontend.controllers.auth.AuthJourney
import uk.gov.hmrc.ngrloginregisterfrontend.models.NGRSummaryListRow.summarise
import uk.gov.hmrc.ngrloginregisterfrontend.models._
import uk.gov.hmrc.ngrloginregisterfrontend.models.cid.PersonDetails
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.{CredId, RatepayerRegistrationValuation}
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.ConfirmContactDetailsView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ConfirmContactDetailsController @Inject()(view: ConfirmContactDetailsView,
                                                authenticate: AuthJourney,
                                                connector: NGRConnector,
                                                mcc: MessagesControllerComponents,
                                                citizenDetailsConnector: CitizenDetailsConnector)(implicit appConfig: AppConfig, ec: ExecutionContext) extends FrontendController(mcc) with I18nSupport {
  //TODO WE DONT'T HAVE TOWN FROM PERSON DETAILS BUT NEED IT IN MAKING A RATEPAYER

  def show(): Action[AnyContent] =
    authenticate.authWithUserDetails.async { implicit request =>
      val credId = CredId(request.credId.getOrElse(""))
      val nino = Nino(request.nino.nino.getOrElse(""))
      val email = Email(request.email.getOrElse(""))

      connector.getRatepayer(credId).flatMap {
        case Some(ratepayer) =>
          val name = ratepayer.ratepayerRegistration.flatMap(_.name).map(_.value).getOrElse("")
          Future.successful(Ok(view(SummaryList(createSummaryRowsFromRatePayer(ratepayer, request)), name)))

        case None =>
          citizenDetailsConnector.getPersonDetails(nino).flatMap {
            case Left(error) =>
              Future.successful(Status(error.code)(Json.toJson(error)))

            case Right(personDetails) =>
              val nameValue = name(personDetails)
              val ratepayerRegistration = RatepayerRegistration(
                name = Some(Name(nameValue)),
                email = Some(email),
                address = Some(buildAddress(personDetails))
              )

              val ratepayerData = RatepayerRegistrationValuation(credId, Some(ratepayerRegistration))

              connector.upsertRatepayer(ratepayerData).map { _ =>
                Ok(view(SummaryList(createSummaryRows(personDetails, request)), nameValue))
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

  private[controllers] def createSummaryRowsFromRatePayer(ratepayerRegistrationValuation: RatepayerRegistrationValuation, request: AuthenticatedUserRequest[AnyContent])(implicit messages: Messages): Seq[SummaryListRow] = {
      val address = ratepayerRegistrationValuation.ratepayerRegistration.flatMap(_.address).map { address => {
        Seq(
          address.line1,
          address.line2.getOrElse(""),
          address.postcode.value,
          address.country
        )
      }}.getOrElse(Seq.empty)

    val contactNumber = ratepayerRegistrationValuation.ratepayerRegistration.flatMap(_.contactNumber).map(number => number.value).getOrElse("")

    def getValue[T](extract: RatepayerRegistration => Option[T], default: String = ""): String =
      ratepayerRegistrationValuation.ratepayerRegistration.flatMap(extract).map(_.toString).getOrElse(default)

    def getUrl(route: String, linkId: String, messageKey: String): Option[Link] = {
      Some(Link(Call("GET", route), linkId, messageKey))
    }

    Seq(
      NGRSummaryListRow(messages("confirmContactDetails.contactName"), None, Seq(getValue(_.name.map(_.value))),
        getUrl(routes.NameController.show.url, "name-linkid", "confirmContactDetails.change")),

      NGRSummaryListRow(messages("confirmContactDetails.emailAddress"), None, Seq(getValue(_.email.map(_.value))),
        getUrl(routes.EmailController.show.url, "email-linkid", "confirmContactDetails.change")),

      NGRSummaryListRow(messages("confirmContactDetails.phoneNumber"), None, Seq(getValue(_.contactNumber.map(_.value))),
        getUrl(routes.PhoneNumberController.show.url, "number-linkid",
          if (contactNumber.isEmpty) "confirmContactDetails.add" else "confirmContactDetails.change")),

      NGRSummaryListRow(messages("confirmContactDetails.address"), Some(messages("confirmContactDetails.address.caption")),
        address, getUrl("url", "address-linkid", "confirmContactDetails.change")),

    ).map(summarise)

    }


  private[controllers] def createSummaryRows(personDetails: PersonDetails, request: AuthenticatedUserRequest[AnyContent])(implicit messages: Messages): Seq[SummaryListRow] = {

    val address: Seq[String] = Seq(
      personDetails.address.line1.getOrElse(""),
      personDetails.address.line2.getOrElse(""),
      personDetails.address.line3.getOrElse(""),
      personDetails.address.line4.getOrElse(""),
      personDetails.address.line5.getOrElse(""),
      personDetails.address.postcode.getOrElse(""),
      personDetails.address.country.getOrElse("")
    ).filter(_.nonEmpty)

    Seq(
      NGRSummaryListRow(messages("confirmContactDetails.contactName"), None, Seq(name(personDetails)), Some(Link(Call("GET", routes.NameController.show.url), "name-linkid", "Change"))),
      NGRSummaryListRow(messages("confirmContactDetails.emailAddress"), None, Seq(request.email.getOrElse("")), Some(Link(Call("GET", routes.EmailController.show.url), "email-linkid", "Change"))),
      NGRSummaryListRow(messages("confirmContactDetails.phoneNumber"), None, Seq.empty, Some(Link(Call("GET", routes.PhoneNumberController.show.url), "number-linkid", "Add"))),
      NGRSummaryListRow(messages("confirmContactDetails.address"), Some(messages("confirmContactDetails.address.caption")), address, Some(Link(Call("GET", "url"), "address-linkid", "Change")))
    ).map(summarise)
  }

}
