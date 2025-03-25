package uk.gov.hmrc.ngrloginregisterfrontend.controllers

import play.api.http.Status.OK
import play.api.test.Helpers.{contentAsString, status}
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.ControllerSpecSupport

class RegistrationCompleteControllerSpec extends ControllerSpecSupport {

  lazy val RegistrationCompleteRecoveryId: String = routes.RegistrationCompleteController.show(Some("12345xyz")).url
  lazy val testView: RegistrationCompleteView = inject[RegistrationCompleteView]

  val pageTitle = "Registration Successful"

  def controller() = new RegistrationCompleteController(
    testView,
    mcc
  )

  "RegistrationComplete Controller" must {
    "method show" must {
      "Return OK and the correct view" in {
        val result = controller().show(Some("12345xyz"))(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
      }
    }
  }
}
