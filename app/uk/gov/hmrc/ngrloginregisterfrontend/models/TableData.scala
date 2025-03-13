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

import uk.gov.hmrc.govukfrontend.views.Aliases.Table
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.{HeadCell, TableRow}

sealed trait TableRowData
case class TableRowText(value: String) extends TableRowData
case class TableRowLink(value: String) extends TableRowData

final case class TableData(headers: Seq[String], rows: Seq[Seq[TableRowData]], caption: Option[String] = None, captionClasses: String = "govuk-table__caption--m") {
  def toTable: Table = {
    Table(
      head = Some(headers.map(header => HeadCell(content = Text(header)))),
      rows = rows.map(
        row => row.map(
          cell => TableRow(
            cell match {
              case text: TableRowText => Text(text.value)
              case link: TableRowLink => HtmlContent(s"""<a href="${link.value}" class="govuk-link">Select property</a>""")
            })
        )
      ),
      caption = caption,
      captionClasses = captionClasses,
      firstCellIsHeader = false
    )
  }
}