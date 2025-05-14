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

//package uk.gov.hmrc.ngrloginregisterfrontend.actions

//import com.google.inject.ImplementedBy
//import play.api.mvc.Results.Redirect
//import play.api.mvc._
//import uk.gov.hmrc.http.HeaderCarrier
//import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
//import uk.gov.hmrc.ngrloginregisterfrontend.connectors.NGRConnector
//import uk.gov.hmrc.ngrloginregisterfrontend.models.AuthenticatedUserRequest
//import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.{CredId, RatepayerRegistrationValuationRequest}
//import uk.gov.hmrc.play.http.HeaderCarrierConverter
//
//import javax.inject.Inject
//import scala.concurrent.{ExecutionContext, Future}

//class HasMandotoryDetailsActionImpl @Inject()(
//                                    ngrConnector: NGRConnector,
//                                    isRegistered: RegistrationAction,
//                                    appConfig: AppConfig,
//                                    mcc: MessagesControllerComponents
//                                  )(implicit ec: ExecutionContext)  extends  HasMandotoryDetailsAction{

//  override def invokeBlock[A](request: Request[A], block: RatepayerRegistrationValuationRequest[A] => Future[Result]): Future[Result] = {
//
//    isRegistered.invokeBlock(request, { implicit authRequest: AuthenticatedUserRequest[A]  =>
//      implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(authRequest, authRequest.session)
//
//      val credId = CredId(authrequest.credId.value)
//
//      //TODO check the frontend:
//      ngrConnector.getRatepayer(credId).flatMap{ maybeRatepayer =>
//        val isRegistered = maybeRatepayer
//          .flatMap(_.ratepayerRegistration)
//          .flatMap(_.isRegistered)
//          .getOrElse(false)
//
//        if (isRegistered) {
//          redirectToDashboard()
//        } else {
//          block(RatepayerRegistrationValuationRequest(request, credId))
//        }
//      }
//    })
//  }
//
//  def redirectToDashboard(): Future[Result] = {
//    Future.successful(Redirect(s"${appConfig.dashboard}/ngr-dashboard-frontend/dashboard"))
//  }
//   $COVERAGE-OFF$
//  override def parser: BodyParser[AnyContent] = mcc.parsers.defaultBodyParser
//
//  override protected def executionContext: ExecutionContext = ec
//  // $COVERAGE-ON$

//}
//
//@ImplementedBy(classOf[RegistrationActionImpl])
//trait HasMandotoryDetailsAction extends ActionBuilder[RatepayerRegistrationValuationRequest, AnyContent] with ActionFunction[Request, RatepayerRegistrationValuationRequest]
