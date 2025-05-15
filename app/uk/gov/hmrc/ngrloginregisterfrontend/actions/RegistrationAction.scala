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
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.{CitizenDetailsConnector, NGRConnector}
import uk.gov.hmrc.ngrloginregisterfrontend.models.cid.PersonDetails
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.{Address, Email, Name, Nino}
import uk.gov.hmrc.ngrloginregisterfrontend.models.{AuthenticatedUserRequest, Postcode, RatepayerRegistration}
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.{CredId, RatepayerRegistrationValuation, RatepayerRegistrationValuationRequest}
import uk.gov.hmrc.ngrloginregisterfrontend.repo.RatepayerRegistraionRepo
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import java.net.URL
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RegistrationActionImpl @Inject()(
                                    ngrConnector: NGRConnector,
                                    ratepayerRegistraionRepo: RatepayerRegistraionRepo,
                                    citizenDetailsConnector: CitizenDetailsConnector,
                                    authenticate: AuthRetrievals,
                                    appConfig: AppConfig,
                                    mcc: MessagesControllerComponents
                                  )(implicit ec: ExecutionContext)  extends  RegistrationAction{

  override def invokeBlock[A](request: Request[A], block: RatepayerRegistrationValuationRequest[A] => Future[Result]): Future[Result] = {

    authenticate.invokeBlock(request, { implicit authRequest: AuthenticatedUserRequest[A] =>
      val credId = CredId(authRequest.credId.getOrElse(""))

      ratepayerRegistraionRepo.findByCredId(credId).flatMap {
        case maybeRatepayer if maybeRatepayer.isDefined =>
          println(Console.GREEN + "Found In Frontend" + Console.RESET)
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

              val authNino = authRequest.nino.nino.getOrElse(throw new RuntimeException("No ratepayerRegistration found from mongo"))

             citizenDetailsConnector.getPersonDetails(Nino(authNino)).flatMap {
                case Left(error) =>
                  Future.successful(Status(error.code)(Json.toJson(error)))

                case Right(personDetails) =>
                  val nameValue = name(personDetails)
                  println(Console.MAGENTA + "UPSERT TO BACKEND" + Console.RESET)
                  val authData = Some(RatepayerRegistration(
                    name = if (authRequest.name.isDefined) {
                      authRequest.name.map{fullName => Name(fullName.name + fullName.lastName.getOrElse(""))}
                    } else Some(Name(nameValue)),
                    email = if (authRequest.email.isDefined) {
                      Some(Email(authRequest.email.getOrElse("").toString))
                    } else None,
                    nino = Some(Nino(authNino)),
                    address = Some(buildAddress(personDetails)) ,
                    isRegistered = Some(false)))
                  ratepayerRegistraionRepo.upsertRatepayerRegistration(RatepayerRegistrationValuation(credId, authData))
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
