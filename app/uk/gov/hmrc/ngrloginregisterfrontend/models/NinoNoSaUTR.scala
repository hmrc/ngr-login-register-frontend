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

package uk.gov.hmrc.ngrloginregisterfrontend.models

import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formatter
import play.api.data.validation.Constraint
import play.api.libs.json.{Reads, Writes}
import uk.gov.hmrc.domain.{SimpleName, SimpleObjectReads, SimpleObjectWrites, TaxIdentifier}
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.CommonFormValidators

sealed trait ConfirmTRN extends RadioEntry

final case class NinoNoSaUTR(nino: String, confirmTRN: ConfirmTRN) extends TaxIdentifier with SimpleName {

  private val LengthWithoutSuffix: Int = 8

  def value = nino

  val name = "nino"

  def formatted = value.grouped(2).mkString(" ")

  def withoutSuffix = value.take(LengthWithoutSuffix)
}

object NinoNoSaUTR extends CommonFormValidators {

  val formName: String = "NinoNoSaUTR"

  implicit val ninoWrite: Writes[Nino] = new SimpleObjectWrites[Nino](_.value)
  implicit val ninoRead: Reads[Nino]   = new SimpleObjectReads[Nino]("nino", Nino.apply)

  def isValid(nino: String): Boolean = nino.nonEmpty && ninoRegexPattern.matcher(nino).matches()

  private lazy val ninoInvalidFormat = "nino.invalidFormat.error"
  private lazy val ninoFailsMatch    = "confirmTrn.failsNinoMatch"
  val nino                           = "nino-value"

  case object Yes extends ConfirmTRN
  case object NoLater extends ConfirmTRN

  implicit val confirmTRNFormatter: Formatter[ConfirmTRN] = new Formatter[ConfirmTRN] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], ConfirmTRN] = {
      data.get(key) match {
        case Some("Yes")             => Right(Yes)
        case Some("NoLater")         => Right(NoLater)
        case _                       => Left(Seq(FormError(key, "confirmTrn.noSelectionError")))
      }
    }

    override def unbind(key: String, value: ConfirmTRN): Map[String, String] = {
      Map(key -> (value match {
        case Yes    => "Yes"
        case NoLater => "NoLater"
      }))
    }
  }

  def form(authNino: Option[String]): Form[NinoNoSaUTR] =
    Form(
      mapping(
        nino -> text()
          .verifying(
            firstError(
              formNameCheck(nino, formName, ninoInvalidFormat)
//              regexp(ninoRegexPattern.pattern(), ninoInvalidFormat),
//              isMatchingNino(authNino.getOrElse(""), nino, ninoFailsMatch)
            )
        ),
        formName -> Forms.of[ConfirmTRN]
      )(NinoNoSaUTR.apply)(NinoNoSaUTR.unapply)
        .verifying("nino.empty.error", { case NinoNoSaUTR(ninoOpt, confirmTRN) =>
          ninoOpt.isEmpty || confirmTRN == NoLater
        })
    )




}
