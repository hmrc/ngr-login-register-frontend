package uk.gov.hmrc.ngrloginregisterfrontend.models.registration

import uk.gov.hmrc.ngrloginregisterfrontend.helpers.TestSupport
import uk.gov.hmrc.ngrloginregisterfrontend.mocks.{MockYesNoRadioItem, No, Yes}
import uk.gov.hmrc.ngrloginregisterfrontend.models.{NGRRadio, NGRRadioButtons, NGRRadioName}

class NGRRadioSpec extends TestSupport {

  "buildRadios" should {

    "generate a minimum radio button" in {
      val ngrButton1 = NGRRadioButtons("Yes", Yes)
      val ngrButton2 = NGRRadioButtons("No", No)
      val ngrRadioName = NGRRadioName("radioName")
      val ngrRadios = NGRRadio(ngrRadioName, Seq(ngrButton1,ngrButton2))
    }


  }

}
