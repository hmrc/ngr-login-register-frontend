/*
 * Copyright 2024 HM Revenue & Customs
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

import com.google.common.base.Strings
import play.api.data.validation.{Constraint, Invalid, Valid}

import java.util.regex.Pattern

trait CommonFormValidators {

  val fullNameRegexPattern: Pattern     = Pattern.compile("^[A-Za-z .'-]{1,160}$")
  val phoneNumberRegexPattern: Pattern  = Pattern.compile("^(\\+)?[0-9() ]{9,16}$")
  val postcodeRegexPattern: Pattern = Pattern.compile("^([A-Za-z][A-Ha-hJ-Yj-y]?[0-9][A-Za-z0-9]? ?[0-9][A-Za-z]{2}|[Gg][Ii][Rr] ?0[Aa]{2})$")
  val maxLengthErrorMessage: Int => String = maxLength => s"No more than $maxLength characters allowed"
  val isNonEmpty: String => Boolean = value => !Strings.isNullOrEmpty(value) && value.trim.nonEmpty
  private val isMatchingPattern: (String, Pattern) => Boolean = (value, pattern) => pattern.matcher(value).matches()
  val isValidEmail: String => Boolean = (email: String) => email.isEmpty || isMatchingPattern(email, emailPattern)
  private val emailPattern: Pattern =
    Pattern.compile("""[a-zA-Z0-9]+([-+.'][a-zA-Z0-9]+)*@[a-zA-Z0-9]+([-.][a-zA-Z0-9]+)*\.[a-zA-Z0-9]+([-.][a-zA-Z0-9]+)*""")

  protected def firstError[A](constraints: Constraint[A]*): Constraint[A] =
    Constraint { input =>
      constraints
        .map(_.apply(input))
        .find(_ != Valid)
        .getOrElse(Valid)
    }

  protected def regexp(regex: String, errorKey: String): Constraint[String] =
    Constraint {
      case str if str.matches(regex) =>
        Valid
      case _                         =>
        Invalid(errorKey, regex)
    }

  protected def maxLength(maximum: Int, errorKey: String): Constraint[String] =
    Constraint {
      case str if str.length <= maximum =>
        Valid
      case _                            =>
        Invalid(errorKey, maximum)
    }

  protected def isNotEmpty(value: String, errorKey: String): Constraint[String] =
    Constraint {
      case str if str.trim.nonEmpty =>
        Valid
      case _                        =>
        Invalid(errorKey, value)
    }
}
