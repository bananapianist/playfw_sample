package forms

import java.util.Date
import play.api.data.Form
import play.api.data.Forms._
import models.TablesExtend._
import play.api.data.validation.Constraints._
import play.api.i18n.{I18nSupport, MessagesApi, Messages, Lang}
import javax.inject.Inject

class ContractForm @Inject()(val messagesApi: MessagesApi) extends I18nSupport{
    val form = Form(
    mapping(
      "id" -> optional(longNumber),
      "customerId" -> longNumber.verifying(Messages("error.required", "顧客"), {_ != null}),
      "status" -> text.verifying(Messages("error.required", "ステータス"), {!_.isEmpty}).verifying(Messages("error.maxLength", 50),{_.length <= 50 }),
      "comment" -> text.verifying(Messages("error.maxLength", 255),{_.length <= 255 }),
      "contract_date" -> date("yyyy-MM-dd'T'HH:mm"),
      "cancel_date" -> date("yyyy-MM-dd'T'HH:mm"),
      "isDisabled" -> boolean
      )
      (contractapply)(contractunapply)
  )
  private def contractapply(
      id: Option[Long],
      customerId: Long, 
      status:  String,
      comment: String,
      contractDate: Date,
      cancelDate: Date,
      isDisabled: Boolean
       ) = new ContractRow(id.getOrElse(0), customerId, Option(status), Option(comment), Option(new Date), Option(new Date), Option(isDisabled), Option(new Date),new Date)
  private def contractunapply(n: ContractRow) = Some(
      (Option(n.id), n.customerId, n.status.getOrElse(null), n.comment.getOrElse(null), n.contractDate.getOrElse(null), n.cancelDate.getOrElse(null), n.isDisabled.getOrElse(true))
      )

}