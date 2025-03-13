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

package uk.gov.hmrc.ngrloginregisterfrontend.views

import uk.gov.hmrc.govukfrontend.views.Aliases.{HeadCell, Table, TableRow, Text}
import uk.gov.hmrc.ngrloginregisterfrontend.helpers.ViewBaseSpec
import uk.gov.hmrc.ngrloginregisterfrontend.models.{TableData, TableHeader, TableRowText}

class TableDataSpec extends ViewBaseSpec{

  "TableData" when {
    "To table produces table" in {
      val mockTableData = TableData(headers = Seq(TableHeader("header 1", "gov-uk")),rows = Seq(Seq(TableRowText("Address 1"))),caption = Some("Caption"))
      mockTableData.toTable mustBe Table(
        rows = List(List(TableRow(Text("Address 1") , classes = ""))),
        head = Some(List(HeadCell(Text("header 1"), classes = "gov-uk"))),
        caption = Some("Caption"), captionClasses = "govuk-table__caption--m", firstCellIsHeader = false, classes = "govuk-!-width-full")
    }
  }
}
