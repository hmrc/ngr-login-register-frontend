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

/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http:www.apache.org/licenses/LICENSE-2.0
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
import uk.gov.hmrc.ngrloginregisterfrontend.controllers.routes
import uk.gov.hmrc.ngrloginregisterfrontend.models.registration.{CredId, RatepayerRegistrationValuationRequest}
import uk.gov.hmrc.ngrloginregisterfrontend.repo.RatepayerRegistrationRepo

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class HasMandotoryDetailsActionImpl @Inject()(
                                               mongo: RatepayerRegistrationRepo,
                                               isRegistered: RegistrationAction,
                                               mcc: MessagesControllerComponents
                                  )(implicit ec: ExecutionContext)  extends  HasMandotoryDetailsAction{

  override def invokeBlock[A](request: Request[A], block: RatepayerRegistrationValuationRequest[A] => Future[Result]): Future[Result] = {

    isRegistered.invokeBlock(request, { implicit registration: RatepayerRegistrationValuationRequest[A]  =>

      val credId = CredId(registration.credId.value)
        block(RatepayerRegistrationValuationRequest(request, credId, registration.ratepayerRegistration))
      }
    )
  }

  override def parser: BodyParser[AnyContent] = mcc.parsers.defaultBodyParser

  override protected def executionContext: ExecutionContext = ec


}

@ImplementedBy(classOf[HasMandotoryDetailsActionImpl])
trait HasMandotoryDetailsAction extends ActionBuilder[RatepayerRegistrationValuationRequest, AnyContent] with ActionFunction[Request, RatepayerRegistrationValuationRequest]
