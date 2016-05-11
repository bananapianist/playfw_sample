package forms

import java.util.Date
import play.api.data.Form
import play.api.data.Forms._
import models.TablesExtend._
import play.api.data.validation.Constraints._
import play.api.i18n.{I18nSupport, MessagesApi, Messages, Lang}
import javax.inject.Inject
import utilities._

class AppAccountForm @Inject()(val messagesApi: MessagesApi) extends I18nSupport{
    val form = Form(
    mapping(
      "id" -> optional(longNumber),
      "name" -> text.verifying(Messages("error.required", "名前"), {!_.isEmpty})
                            .verifying(Messages("error.maxLength", 10),{_.length <= 50 })
                            .verifying(Messages("error.minLength", 4),{_.length >=4})
      )
      (appaccountapply)(appaccountunapply)
  )
  private def appaccountapply(
      id: Option[Long],
      name: String
       ) = new AppAccountRow(id.getOrElse(0), name, new Date)
  private def appaccountunapply(n: AppAccountRow) = Some(
      (Option(n.id), n.name)
      )

}