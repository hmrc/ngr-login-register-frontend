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
import play.api.mvc.Results.Redirect
import play.api.mvc._
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.ngrloginregisterfrontend.connectors.NGRConnector
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.{Email, Name, Nino}
import uk.gov.hmrc.ngrloginregisterfrontend.models.{AuthenticatedUserRequest, RatepayerRegistration}
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.{CredId, RatepayerRegistrationValuation, RatepayerRegistrationValuationRequest}
import uk.gov.hmrc.ngrloginregisterfrontend.repo.RatepayerRegistraionRepo
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import java.net.URL
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RegistrationActionImpl @Inject()(
                                    ngrConnector: NGRConnector,
                                    ratepayerRegistraionRepo: RatepayerRegistraionRepo,
                                    authenticate: AuthRetrievals,
                                    appConfig: AppConfig,
                                    mcc: MessagesControllerComponents
                                  )(implicit ec: ExecutionContext)  extends  RegistrationAction{

  override def invokeBlock[A](request: Request[A], block: RatepayerRegistrationValuationRequest[A] => Future[Result]): Future[Result] = {

    authenticate.invokeBlock(request, { implicit authRequest: AuthenticatedUserRequest[A] =>
      val credId = CredId(authRequest.credId.getOrElse(""))

      ratepayerRegistraionRepo.findByCredId(credId).flatMap{ maybeRatepayer =>
        maybeRatepayer match {
          case maybeRatepayer if maybeRatepayer.isDefined == true =>
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
            ngrConnector.getRatepayer(credId).flatMap{ maybeRatepayer =>
              val isRegistered = maybeRatepayer
                .flatMap(_.ratepayerRegistration)
                .flatMap(_.isRegistered)
                .getOrElse(false)

              if (isRegistered) {
                redirectToDashboard()
              } else {
                println(Console.MAGENTA + "UPSERT TO BACKEND" + Console.RESET)
                val authData = Some(RatepayerRegistration(
                  name = if(authRequest.name.isDefined){Some(Name(authRequest.name.getOrElse("").toString))}else None ,
                  email = if(authRequest.email.isDefined){Some(Email(authRequest.email.getOrElse("").toString))}else None ,
                  nino = Some(Nino(authRequest.nino.nino.get)) ,
                  isRegistered = Some(false)))

                ratepayerRegistraionRepo.upsertRatepayerRegistration(RatepayerRegistrationValuation(credId,authData))
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

  def redirectToDashboard(): Future[Result] = {
    Future.successful(Redirect(s"${appConfig.dashboard}/ngr-dashboard-frontend/dashboard"))
  }
  // $COVERAGE-OFF$
  override def parser: BodyParser[AnyContent] = mcc.parsers.defaultBodyParser

  override protected def executionContext: ExecutionContext = ec
  // $COVERAGE-ON$

}

@ImplementedBy(classOf[RegistrationActionImpl])
trait RegistrationAction extends ActionBuilder[RatepayerRegistrationValuationRequest, AnyContent] with ActionFunction[Request, RatepayerRegistrationValuationRequest]
