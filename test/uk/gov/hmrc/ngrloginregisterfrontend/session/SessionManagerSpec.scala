package uk.gov.hmrc.ngrloginregisterfrontend.session

import org.apache.pekko.http.scaladsl.model.HttpHeader.ParsingResult.Ok
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.TestSupport

import javax.inject.Inject
import play.api.mvc.{RequestHeader, Result}


class SessionManagerSpec extends TestSupport {
  val sessionManager: SessionManager = Inject[SessionManager]

  "SessionManager" must {
    "set a journey id" in {
//      sessionManager.setJourneyId(result, "uuid")
    }
  }

}
