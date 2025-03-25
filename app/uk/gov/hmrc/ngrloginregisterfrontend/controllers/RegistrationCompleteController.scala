package uk.gov.hmrc.ngrloginregisterfrontend.controllers

import com.google.inject.Inject
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.ngrloginregisterfrontend.config.AppConfig
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

@Singleton
class RegistrationCompleteController @Inject()(view: RegistrationCompleteView,
                                               mcc: MessagesControllerComponents)(implicit appConfig: AppConfig)extends FrontendController(mcc) with I18nSupport {


  def show(RecoveryId: Option[String]): Action[AnyContent] = Action { implicit request =>
    Ok(view(RecoveryId))
  }
}
