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
import org.apache.pekko.http.scaladsl.model.HttpHeader.ParsingResult.Ok
import play.api.mvc.Results.Redirect
import play.api.mvc._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.{Credentials, Name, Retrieval, ~}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.NGRConnector
import uk.gov.hmrc.ngrloginregisterfrontend.controllers.auth.AuthJourney
import uk.gov.hmrc.ngrloginregisterfrontend.controllers.routes
import uk.gov.hmrc.ngrloginregisterfrontend.models.AuthenticatedUserRequest
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.CredId
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AuthRetrievalsImpl @Inject()(
                               val authConnector: AuthConnector,
                               ngrConnector: NGRConnector,
                               mcc: MessagesControllerComponents,
                               authenticate: AuthJourney,
                              )(implicit ec: ExecutionContext) extends AuthRetrievals
  with AuthorisedFunctions {

  type RetrievalsType = Option[Credentials] ~ Option[String] ~ ConfidenceLevel ~ Option[String] ~ Option[AffinityGroup] ~ Option[Name]

  override def invokeBlock[A](request: Request[A], block: AuthenticatedUserRequest[A] => Future[Result]): Future[Result] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    val retrievals: Retrieval[RetrievalsType] =
      Retrievals.credentials and
      Retrievals.nino and
      Retrievals.confidenceLevel and
      Retrievals.email and
      Retrievals.affinityGroup and
      Retrievals.name

     authorised(ConfidenceLevel.L250).retrieve(retrievals){
        case credentials ~ Some(nino) ~ confidenceLevel ~ email ~ affinityGroup ~ name =>
          block(
              AuthenticatedUserRequest(
                request = request,
                confidenceLevel = Some(confidenceLevel),
                authProvider = credentials.map(_.providerType),
                nino = Nino(hasNino = true,Some(nino)),
                email = email.filter(_.nonEmpty),
                credId = credentials.map(_.providerId),
                affinityGroup = affinityGroup,
                name = name
              )
          )
        case _ ~ _ ~ confidenceLevel ~ _ => throw new Exception("confidenceLevel not met")
      }recoverWith {
      case ex: Throwable =>
        throw ex
    }
  }
  // $COVERAGE-OFF$
  override def parser: BodyParser[AnyContent] = mcc.parsers.defaultBodyParser

  override protected def executionContext: ExecutionContext = ec
  // $COVERAGE-ON$


  def isRegistered():Action[AnyContent] = authenticate.authWithUserDetails.async { implicit request =>
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
    val credId = CredId(request.credId.getOrElse(""))
    val isRegisteredResult = for {
      user <- ngrConnector.getRatepayer(credId)
      isRegistered <- user.map( ratepayerReg => ratepayerReg.ratepayerRegistration.map( registration => registration.isRegistered.getOrElse(false))).getOrElse(false)
    }yield isRegistered



  }

//  def isRegistered(route: Call): Action[AnyContent] = authenticate.authWithUserDetails.async { implicit request =>
//    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
//    val credId = CredId(request.credId.getOrElse(""))
//    for {
//      maybeUser <- ngrConnector.getRatepayer(credId)
//      result <- maybeUser match {
//        case Some(user) =>
//          for {
//            registration <- user.ratepayerRegistration
//            result <- if (registration.isRegistered.getOrElse(false)) {
//              redirectToDashboard()
//            } else {
//             routes.StartController.show
//            }
//          } yield result
//
//        case None =>
//          Future.successful(Redirect(routes.StartController.show))
//      }
//    } yield result
//  }
//
//  def redirectToDashboard(): Result = {
//   Redirect(routes.StartController.show)
//  }


//  def isRegistered(): Future[Result] = authenticate.authWithUserDetails.async { implicit request =>
//    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
//    val credId = CredId(request.credId.getOrElse(""))
//
//    ngrConnector.getRatepayer(credId).flatMap {
//      case Some(user) =>
//        user.ratepayerRegistration.flatMap { registration =>
//          if (registration.isRegistered.getOrElse(false)) {
//            redirectToDashboard()
//          } else {
//            redirectToDashboard()
//          }
//        }
//
//      case None =>
//        Future.successful(Redirect(routes.StartController.show))
//    }
//  }
//
//  def redirectToDashboard(): Future[Result] = {
//    Future.successful(Redirect(routes.StartController.show))
//  }

//  def isRegistered(): Action[AnyContent] = authenticate.authWithUserDetails.async { implicit request =>
//    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
//    val credId = CredId(request.credId.getOrElse(""))
//
//    ngrConnector.getRatepayer(credId).flatMap {
//      case Some(user) =>
//        for {
//          registration <- user.ratepayerRegistration
//        } yield {
//          if (registration.isRegistered.getOrElse(false)) {
//            Redirect(routes.StartController.show)
//          } else {
//
//            Future.successful(routes.StartController.show)
//          }
//        }
//
//      case None =>
//        Future.successful(Redirect(routes.StartController.show)) // Or handle user not found differently
//    }
//  }


}



@ImplementedBy(classOf[AuthRetrievalsImpl])
trait AuthRetrievals extends ActionBuilder[AuthenticatedUserRequest, AnyContent] with ActionFunction[Request, Request]