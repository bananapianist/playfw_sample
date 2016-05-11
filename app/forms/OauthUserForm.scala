package forms

import java.util.Date
import play.api.data.Form
import play.api.data.Forms._
import models.TablesExtend._
import play.api.data.validation.Constraints._
import play.api.i18n.{I18nSupport, MessagesApi, Messages, Lang}
import javax.inject.Inject
import java.util.UUID
import utilities._

class OauthUserForm @Inject()(val messagesApi: MessagesApi) extends I18nSupport{
    val form = Form(
    mapping(
      "guid" -> optional(uuid),
      "name" -> text.verifying(Messages("error.required", "名前"), {!_.isEmpty})
                            .verifying(Messages("error.maxLength", 10),{_.length <= 50 })
                            .verifying(Messages("error.minLength", 4),{_.length >=4})
      )
      (oauthuserapply)(oauthuserunapply)
  )
  private def oauthuserapply(
      guid: Option[UUID],
      name: String
       ) = new OauthUserRow(guid.getOrElse(null), name, new Date)
  private def oauthuserunapply(n: OauthUserRow) = Some(
      (Option(n.guid), n.name)
      )

}