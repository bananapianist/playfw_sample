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

class OauthClientSearchForm @Inject()(val messagesApi: MessagesApi) extends I18nSupport{
    val form = Form(
    mapping(
      "oauthUserId" -> optional(text),
      "oauthUserName" -> optional(text.verifying(Messages("error.maxLength", 255),{_.length <= 255 }))
      )
      (oauthclientsearchapply)(oauthclientsearchunapply)
  )
  private def oauthclientsearchapply(
      oauthUserId: Option[String], 
      oauthUserName:  Option[String]
       ) = new OauthClientSearchRow(oauthUserId, oauthUserName)
  private def oauthclientsearchunapply(n: OauthClientSearchRow) = Some(
      (n.oauthUserId, n.oauthUserName)
      )

}