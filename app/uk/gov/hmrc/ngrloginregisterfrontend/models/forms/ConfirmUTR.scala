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

package uk.gov.hmrc.ngrloginregisterfrontend.models.forms

import play.api.data.Forms.single
import play.api.data.format.Formatter
import play.api.data.{Form, FormError, Forms}
import uk.gov.hmrc.ngrloginregisterfrontend.models.RadioEntry

sealed trait ConfirmUTR extends RadioEntry

object ConfirmUTR {

  val formName: String = "confirmUTR"

  case class Yes(utr: String) extends ConfirmUTR
  case object NoNI extends ConfirmUTR
  case object NoLater extends ConfirmUTR

  implicit val confirmUTRFormatter: Formatter[ConfirmUTR] = new Formatter[ConfirmUTR] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], ConfirmUTR] = {
      data.get(key).collectFirst {
        case s"Yes($utr)" => Yes(utr)
        case "NoNI"       => NoNI
        case "NoLater"    => NoLater
      }.toRight(Seq(FormError(key, "confirmUtr.noSelectionError")))
    }

    override def unbind(key: String, value: ConfirmUTR): Map[String, String] = Map(
      key -> (value match {
        case Yes(utr) => s"Yes($utr)"
        case NoNI     => "NoNI"
        case NoLater  => "NoLater"
      })
    )
  }

  def form(): Form[ConfirmUTR] = Form(
    single(formName -> Forms.of[ConfirmUTR])
  )
}
