package forms

import java.util.Date
import play.api.data.Form
import play.api.data.Forms._
import models.TablesExtend._
import play.api.data.validation.Constraints._
import play.api.i18n.{I18nSupport, MessagesApi, Messages, Lang}
import javax.inject.Inject

class ContractBillSearchForm @Inject()(val messagesApi: MessagesApi) extends I18nSupport{
    val form = Form(
    mapping(
      "customerId" -> optional(longNumber),
      "customerName" -> optional(text.verifying(Messages("error.maxLength", 255),{_.length <= 255 })),
      "status" -> optional(text.verifying(Messages("error.required", "ステータス"), {!_.isEmpty}).verifying(Messages("error.maxLength", 50),{_.length <= 50 })),
      "contractDateFrom" -> optional(date("yyyy-MM-dd")),
      "contractDateTo" -> optional(date("yyyy-MM-dd")),
      "cancelDateFrom" -> optional(date("yyyy-MM-dd")),
      "cancelDateTo" -> optional(date("yyyy-MM-dd")),
      "billName" -> optional(text
                            .verifying(Messages("error.maxLength", 10),{_.length <= 255 })),
      "billEmail" -> optional(email),
      "billTel" -> optional(text.verifying(pattern("""[0-9a-zA-Z-]+""".r,error=Messages("error.alphabet")))),
      "billAddress" -> optional(text.verifying(Messages("error.maxLength", 255),{_.length <= 255 }))
      )
      (contractbillsearchapply)(contractbillsearchunapply)
  )
  private def contractbillsearchapply(
      customerId: Option[Long], 
      customerName: Option[String], 
      status:  Option[String],
      contractDateFrom: Option[Date],
      contractDateTo: Option[Date],
      cancelDateFrom: Option[Date],
      cancelDateTo: Option[Date],
      billName: Option[String], 
      billEmail:  Option[String],
      billTel:  Option[String],
      billAddress:  Option[String]
       ) = new ContractBillSearchRow(customerId, customerName, status, contractDateFrom, contractDateTo, cancelDateFrom, cancelDateTo, billName, billEmail, billTel, billAddress)
  private def contractbillsearchunapply(n: ContractBillSearchRow) = Some(
      n.customerId, n.customerName, n.status, n.contractDateFrom, n.contractDateTo, n.cancelDateFrom, n.cancelDateTo, n.billName, n.billEmail, n.billTel, n.billAddress
      )
}