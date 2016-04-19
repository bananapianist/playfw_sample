package forms

import java.util.Date
import play.api.data.Form
import play.api.data.Forms._
import models.TablesExtend._
import play.api.data.validation.Constraints._
import play.api.i18n.{I18nSupport, MessagesApi, Messages, Lang}
import javax.inject.Inject

class BillForm @Inject()(val messagesApi: MessagesApi) extends I18nSupport{
    val form = Form(
    mapping(
      "id" -> optional(longNumber),
      "billName" -> text.verifying(Messages("error.required", "名前"), {!_.isEmpty})
                            .verifying(Messages("error.maxLength", 10),{_.length <= 10 })
                            .verifying(Messages("error.minLength", 4),{_.length >=4}),
      "billEmail" -> email,
      "billTel" -> text.verifying(pattern("""[0-9a-zA-Z-]+""".r,error=Messages("error.alphabet"))),
      "billAddress" -> text.verifying(Messages("error.required", "住所"), {!_.isEmpty})
       )
      (billapply)(billunapply)
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