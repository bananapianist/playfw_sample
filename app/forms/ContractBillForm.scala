package forms

import java.util.Date
import play.api.data.Form
import play.api.data.Forms._
import models.TablesExtend._
import play.api.data.validation.Constraints._
import play.api.i18n.{I18nSupport, MessagesApi, Messages, Lang}
import javax.inject.Inject

class ContractBillForm @Inject()(val messagesApi: MessagesApi) extends I18nSupport{
    val form = Form(
    mapping(
      "id" -> optional(longNumber),
      "customerId" -> longNumber.verifying(Messages("error.required", "顧客"), {_ != null}),
      "status" -> text.verifying(Messages("error.required", "ステータス"), {!_.isEmpty}).verifying(Messages("error.maxLength", 50),{_.length <= 50 }),
      "comment" -> text.verifying(Messages("error.maxLength", 255),{_.length <= 255 }),
      "contractDate" -> optional(date("yyyy-MM-dd")),
      "cancelDate" -> optional(date("yyyy-MM-dd")),
      "isDisabled" -> boolean,
      "bill" -> mapping(
        "id" -> optional(longNumber),
        "billName" -> text.verifying(Messages("error.required", "名前"), {!_.isEmpty})
                              .verifying(Messages("error.maxLength", 10),{_.length <= 10 })
                              .verifying(Messages("error.minLength", 4),{_.length >=4}),
        "billEmail" -> email,
        "billTel" -> text.verifying(pattern("""[0-9a-zA-Z-]+""".r,error=Messages("error.alphabet"))),
        "billAddress" -> text.verifying(Messages("error.required", "住所"), {!_.isEmpty}) 
        )(billapply)(billunapply)
      )
      (contractapply)(contractunapply)
  )
  private def contractapply(
      id: Option[Long],
      customerId: Long, 
      status:  String,
      comment: String,
      contractDate: Option[Date],
      cancelDate: Option[Date],
      isDisabled: Boolean,
      bill: BillRow
       ) = new ContractBillRow(id.getOrElse(0), customerId, Option(status), Option(comment), contractDate, cancelDate, Option(isDisabled), Option(new Date),new Date, bill)
  private def contractunapply(n: ContractBillRow) = Some(
      (Option(n.id), n.customerId, n.status.getOrElse(null), n.comment.getOrElse(null), n.contractDate, n.cancelDate, n.isDisabled.getOrElse(true), n.bill)
      )
  private def billapply(
      id: Option[Long],
      billName: String, 
      billEmail:  String,
      billTel:  String,
      billAddress:  String
        ) = new BillRow(id.getOrElse(0), 0, Option(billName), Option(billEmail), Option(billTel), Option(billAddress), Option(new Date),new Date)
  private def billunapply(n: BillRow) = Some(
      (Option(n.id), n.billName.getOrElse(null), n.billEmail.getOrElse(null), n.billTel.getOrElse(null), n.billAddress.getOrElse(null))
      )

}