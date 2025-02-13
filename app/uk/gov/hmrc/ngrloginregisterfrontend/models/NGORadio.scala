package uk.gov.hmrc.ngrloginregisterfrontend.models

import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.{Fieldset, Legend, RadioItem, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.errormessage.ErrorMessage
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.Radios

case class NGORadio (radioGroupName: VoaRadioName, voaRadionButtons: Seq[VoaRadioButtons], voaTitle: Option[VoaRadioHeader] = None)

object NGORadio {

  def buildRadios[A](
                      form:Form[A],
                      ngoRadios: NGORadio
                    )(implicit messages: Messages): Radios = {
    Radios(
      fieldset = ngoRadios.voaTitle.map(header =>
        Fieldset(
          legend = Some(Legend(
            content = Text(Messages(header.title)),
            classes = header.classes,
            isPageHeading = header.isPageHeading
          ))
        )
      ),
      idPrefix = Some(ngoRadios.radioGroupName.key),
      name = ngoRadios.radioGroupName.key,
      items = ngoRadios.voaRadionButtons.map { item =>
        RadioItem(
          content = Text(Messages(item.radioContent)),
          value = Some(item.radioValue.toString),
          checked = form.data.values.toList.contains(item.radioValue)
        )
      },
      classes = "govuk-radios",
      errorMessage = form(ngoRadios.radioGroupName.key).error.map(err => ErrorMessage(content = Text(messages(err.message)))),

    )
  }
}
