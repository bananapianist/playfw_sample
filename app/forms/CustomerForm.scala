package forms

import java.util.Date
import play.api.data.Form
import play.api.data.Forms._
import models.TablesExtend._
import play.api.data.validation.Constraints._
import play.api.i18n.{I18nSupport, MessagesApi, Messages, Lang}
import javax.inject.Inject

class CustomerForm @Inject()(val messagesApi: MessagesApi) extends I18nSupport{
    val form = Form(
    mapping(
      "id" -> optional(longNumber),
      "name" -> text.verifying(Messages("error.required", "名前"), {!_.isEmpty})
                            .verifying(Messages("error.maxLength", 10),{_.length <= 10 })
                            .verifying(Messages("error.minLength", 4),{_.length >=4}),
      "email" -> email,
      "tel" -> text.verifying(pattern("""[0-9a-zA-Z-]+""".r,error=Messages("error.alphabet"))),
      "address" -> text.verifying(Messages("error.required", "住所"), {!_.isEmpty}),
      "comment" -> text.verifying(Messages("error.maxLength", 10),{_.length <= 10 }),
      "actionDate" -> date("yyyy-MM-dd'T'HH:mm"),
//      "notificationPeriod" -> optional(number(min = 0, max = 100)),
      "notificationPeriod" -> number,
      "isDisabled" -> boolean
      )
      (customerapply)(customerunapply)
  )
  private def customerapply(
      id: Option[Long],
      name: String, 
      email:  String,
      tel:  String,
      address:  String,
      comment: String,
      actionDate: Date,
      notificationPeriod: Int,
      isDisabled: Boolean
       ) = new CustomerRow(id.getOrElse(0), Option(name), Option(email), Option(tel), Option(address), Option(comment), Option(actionDate), Option(notificationPeriod), Option(new Date), Option(isDisabled), Option(new Date),new Date)
  private def customerunapply(n: CustomerRow) = Some(
      (Option(n.id), n.name.getOrElse(null), n.email.getOrElse(null), n.tel.getOrElse(null), n.address.getOrElse(null), n.comment.getOrElse(null), n.actionDate.getOrElse(null), n.notificationPeriod.getOrElse(0), n.isDisabled.getOrElse(true))
      )

}