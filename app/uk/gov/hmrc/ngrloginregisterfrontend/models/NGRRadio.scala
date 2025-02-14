package uk.gov.hmrc.ngrloginregisterfrontend.models

import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.{Fieldset, Legend, RadioItem, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.errormessage.ErrorMessage
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.Radios

case class NGRRadioName(key: String)
case class NGRRadioButtons(radioContent: String, radioValue: RadioEntry)
case class NGRRadioHeader(title: String, classes: String , isPageHeading: Boolean)

case class NGRRadio (radioGroupName: NGRRadioName, NGRRadioButtons: Seq[NGRRadioButtons], ngrTitle: Option[NGRRadioHeader] = None)

object NGRRadio {

  def buildRadios[A](
                      form:Form[A],
                      NGRRadios: NGRRadio
                    )(implicit messages: Messages): Radios = {
    Radios(
      fieldset = NGRRadios.ngrTitle.map(header =>
        Fieldset(
          legend = Some(Legend(
            content = Text(Messages(header.title)),
            classes = header.classes,
            isPageHeading = header.isPageHeading
          ))
        )
      ),
      idPrefix = Some(NGRRadios.radioGroupName.key),
      name = NGRRadios.radioGroupName.key,
      items = NGRRadios.NGRRadioButtons.map { item =>
        RadioItem(
          content = Text(Messages(item.radioContent)),
          value = Some(item.radioValue.toString),
          checked = form.data.values.toList.contains(item.radioValue)
        )
      },
      classes = "govuk-radios",
      errorMessage = form(NGRRadios.radioGroupName.key).error.map(err => ErrorMessage(content = Text(messages(err.message)))),

    )
  }
}
