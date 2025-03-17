package uk.gov.hmrc.ngrloginregisterfrontend.views

import uk.gov.hmrc.ngrloginregisterfrontend.helpers.ViewBaseSpec
import uk.gov.hmrc.ngrloginregisterfrontend.views.html.FeatureNotImplementedView

class FeatureNotImplementedViewSpec  extends ViewBaseSpec {

    lazy val testView: FeatureNotImplementedView = inject[FeatureNotImplementedView]

    private lazy val pageWithId = testView(Some("12345"))(request, messages, mockConfig)
    private lazy val pageWithoutId = testView(None)(request, messages, mockConfig)


    "The FeatureNotImplemented view" should {
        "Show the appropriate message" when {
          "a journey ID is present" in {
            pageWithId
          }
          "A journey ID is not present" in {
            pageWithoutId
          }
        }
    }

}
