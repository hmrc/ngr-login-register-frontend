package uk.gov.hmrc.ngrloginregisterfrontend.connectors

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.http.Status.OK
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, status}
import uk.gov.hmrc.ngrloginregisterfrontend.controllers.{EmailController, FeatureNotImplementedController, routes}
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.FeatureNotImplementedView

import scala.concurrent.Future

class FeatureNotImplementedControllerSpec extends ControllerSpecSupport {

  lazy val featureNotImplementedId: String = routes.FeatureNotImplementedController.show(Some("12345")).url
  lazy val testView: FeatureNotImplementedView = inject[FeatureNotImplementedView]

 val pageTitle = "This part of the online service is not available yet"

  def controller() = new FeatureNotImplementedController(
    testView,
    mcc
  )

  "FeatureNotImplemented Controller" must {
    "method show" must {
      "Return OK and the correct view" in {
        val result = controller().show(Some("12345"))(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
      }
    }
  }
}
