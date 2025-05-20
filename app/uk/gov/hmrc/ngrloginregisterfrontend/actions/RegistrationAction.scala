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

package uk.gov.hmrc.ngrloginregisterfrontend.actions

import com.google.inject.ImplementedBy
import play.api.libs.json.Json
import play.api.mvc.Results.{Redirect, Status}
import play.api.mvc._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.{CitizenDetailsConnector, NGRConnector}
import uk.gov.hmrc.ngrloginregisterfrontend.models.cid.PersonDetails
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.{Address, Email, Name}
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.{RatepayerRegistrationValuation, RatepayerRegistrationValuationRequest}
import uk.gov.hmrc.ngrloginregisterfrontend.models.{Postcode, RatepayerRegistration}
import uk.gov.hmrc.ngrloginregisterfrontend.repo.RatepayerRegistraionRepo
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RegistrationActionImpl @Inject()(
                                    ngrConnector: NGRConnector,
                                    mongo: RatepayerRegistraionRepo,
                                    citizenDetailsConnector: CitizenDetailsConnector,
                                    authenticate: AuthRetrievals,
                                    appConfig: AppConfig,
                                    mcc: MessagesControllerComponents
                                  )(implicit ec: ExecutionContext)  extends  RegistrationAction{

  override def invokeBlock[A](request: Request[A], block: RatepayerRegistrationValuationRequest[A] => Future[Result]): Future[Result] = {

    authenticate.invokeBlock(request, { implicit authRequest: RatepayerRegistrationValuationRequest[A] =>
      val credId = authRequest.credId

      mongo.findByCredId(credId).flatMap {
        case maybeRatepayer if maybeRatepayer.isDefined =>
          val isRegistered = maybeRatepayer
            .flatMap(_.ratepayerRegistration)
            .flatMap(_.isRegistered)
            .getOrElse(false)
          if (isRegistered) {
            redirectToDashboard()
          } else {
            block(RatepayerRegistrationValuationRequest(request, credId, maybeRatepayer.get.ratepayerRegistration))
          }
        case _ =>
          implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(authRequest, authRequest.session)
          ngrConnector.getRatepayer(credId).flatMap { maybeRatepayer =>
            val isRegistered = maybeRatepayer
              .flatMap(_.ratepayerRegistration)
              .flatMap(_.isRegistered)
              .getOrElse(false)
            if (isRegistered) {
              redirectToDashboard()
            } else {

              val authNino = authRequest.ratepayerRegistration.get.nino.getOrElse(throw new RuntimeException("No ratepayerRegistration found from mongo"))

             citizenDetailsConnector.getPersonDetails(authNino).flatMap {
                case Left(error) =>
                  Future.successful(Status(error.code)(Json.toJson(error)))

                case Right(personDetails) =>
                  val nameValue = name(personDetails)
                  val authData = Some(RatepayerRegistration(
                    name = if (authRequest.ratepayerRegistration.get.name.isDefined) {
                      authRequest.ratepayerRegistration.get.name
                    } else Some(Name(nameValue)),
                    email = if (authRequest.ratepayerRegistration.get.email.isDefined) {
                      Some(Email(authRequest.ratepayerRegistration.get.email.getOrElse("").toString))
                    } else None,
                    nino = authRequest.ratepayerRegistration.get.nino,
                    address = Some(buildAddress(personDetails)) ,
                    isRegistered = Some(false)))
                  mongo.upsertRatepayerRegistration(RatepayerRegistrationValuation(credId, authData))
                  block(
                    RatepayerRegistrationValuationRequest(
                      request,
                      credId,
                      authData))
              }
            }
          }
      }
    })
  }

  private def buildAddress(personDetails: PersonDetails): Address =
    Address(
      line1 = personDetails.address.line1.getOrElse(""),
      line2 = personDetails.address.line2,
      town = personDetails.address.line4.getOrElse(""),
      county = None,
      postcode =  Postcode(personDetails.address.postcode.getOrElse("")),
    )

  private def name(personDetails: PersonDetails): String = List(
    personDetails.person.firstName,
    personDetails.person.middleName,
    personDetails.person.lastName
  ).flatten.mkString(" ")

  private def redirectToDashboard(): Future[Result] = {
    Future.successful(Redirect(s"${appConfig.dashboard}/ngr-dashboard-frontend/dashboard"))
  }
  // $COVERAGE-OFF$
  override def parser: BodyParser[AnyContent] = mcc.parsers.defaultBodyParser

  override protected def executionContext: ExecutionContext = ec
  // $COVERAGE-ON$

}

@ImplementedBy(classOf[RegistrationActionImpl])
trait RegistrationAction extends ActionBuilder[RatepayerRegistrationValuationRequest, AnyContent] with ActionFunction[Request, RatepayerRegistrationValuationRequest]
