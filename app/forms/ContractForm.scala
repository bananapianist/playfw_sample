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
      "contractDate" -> optional(date("yyyy-MM-dd")),
      "cancelDate" -> optional(date("yyyy-MM-dd")),
      "isDisabled" -> boolean
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
      isDisabled: Boolean
       ) = new ContractRow(id.getOrElse(0), customerId, Option(status), Option(comment), contractDate, cancelDate, Option(isDisabled), Option(new Date),new Date)
  private def contractunapply(n: ContractRow) = Some(
      (Option(n.id), n.customerId, n.status.getOrElse(null), n.comment.getOrElse(null), n.contractDate, n.cancelDate, n.isDisabled.getOrElse(true))
      )

}