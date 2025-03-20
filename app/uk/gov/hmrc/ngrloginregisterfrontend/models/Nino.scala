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

import play.api.data.Form
import play.api.data.Forms.{mapping, text}
import play.api.libs.json.{Reads, Writes}
import uk.gov.hmrc.domain.{SimpleName, SimpleObjectReads, SimpleObjectWrites, TaxIdentifier}
import uk.gov.hmrc.ngrloginregisterfrontend.models.forms.CommonFormValidators

final case class Nino(nino: String) extends TaxIdentifier with SimpleName {

  private val LengthWithoutSuffix: Int = 8

  def value = nino

  val name = "nino"

  def formatted = value.grouped(2).mkString(" ")

  def withoutSuffix = value.take(LengthWithoutSuffix)
}

object Nino extends CommonFormValidators {

  implicit val ninoWrite: Writes[Nino] = new SimpleObjectWrites[Nino](_.value)
  implicit val ninoRead: Reads[Nino] = new SimpleObjectReads[Nino]("nino", Nino.apply)

  private val validNinoFormat = "[[A-Z]&&[^DFIQUV]][[A-Z]&&[^DFIQUVO]] ?\\d{2} ?\\d{2} ?\\d{2} ?[A-D]{1}"
  private val invalidPrefixes = List("BG", "GB", "NK", "KN", "TN", "NT", "ZZ")

  private def hasValidPrefix(nino: String) = invalidPrefixes.find(nino.startsWith).isEmpty

  def isValid(nino: String) = nino != null && hasValidPrefix(nino) && nino.matches(validNinoFormat)

  private val validFirstCharacters = ('A' to 'Z').filterNot(List('D', 'F', 'I', 'Q', 'U', 'V').contains).map(_.toString)
  private val validSecondCharacters = ('A' to 'Z').filterNot(List('D', 'F', 'I', 'O', 'Q', 'U', 'V').contains).map(_.toString)
  val validPrefixes = validFirstCharacters.flatMap(a => validSecondCharacters.map(a + _)).filterNot(invalidPrefixes.contains(_))
  val validSuffixes = ('A' to 'D').map(_.toString)

  private lazy val ninoEmptyError    = "nino.empty.error"
  private lazy val ninoInvalidFormat = "nino.invalidFormat.error"
  val nino                           = "nino-value"

  def form(): Form[Nino] =
    Form(
      mapping(
        nino -> text()
          .verifying(
            firstError(
              isNotEmpty(nino, ninoEmptyError),
              regexp(ninoRegexPattern.pattern(), ninoInvalidFormat)
            )
          )
      )(Nino.apply)(Nino.unapply)
    )
}
