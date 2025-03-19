package uk.gov.hmrc.ngrloginregisterfrontend.utils

import org.apache.commons.lang3.StringUtils

trait StringHelper {
  def maskString(input: String): String = {
    StringUtils.overlay(input, StringUtils.repeat("*", input.length - 3), 0, input.length - 3)
  }
}
