package uk.gov.hmrc.ngrloginregisterfrontend.controllers

import play.api.http.Status.OK
import play.api.test.Helpers.{defaultAwaitTimeout, status}
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.StartView

class StartControllerSpec extends ControllerSpecSupport {
  lazy val startView: StartView = inject[StartView]

  def controller =
    new StartController(
      startView,
      mcc
    )

  "Start Controller" must {
    "return OK and the correct view for a GET" in {
      val result = controller.show()(authenticatedFakeRequest)
      status(result) mustBe OK
    }
  }
}
