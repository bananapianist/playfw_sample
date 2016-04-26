package forms

import java.util.Date
import play.api.data.Form
import play.api.data.Forms._
import models.TablesExtend._
import play.api.data.validation.Constraints._
import play.api.i18n.{I18nSupport, MessagesApi, Messages, Lang}
import javax.inject.Inject
import utilities._

class LoginForm @Inject()(val messagesApi: MessagesApi) extends I18nSupport{
  val loginForm = Form(
    mapping(
      "email" -> text.verifying(Messages("email または password　が違います"), {!_.isEmpty})
        .verifying(ValidationHelper.emailAddress),
      "password" -> text.verifying(Messages("email または password　が違います"), {!_.isEmpty})
    )(loginormapply)(loginormunapply)
  )
  private def loginormapply(
      email: String, 
      password:  String
       ) = new AccountRow(0, email, password, null, null)
  private def loginormunapply(n: AccountRow) = Some(
      (n.email, n.password)
      )

}