package forms

import java.util.Date
import play.api.data.Form
import play.api.data.Forms._
import models.TablesExtend._
import play.api.data.validation.Constraints._
import play.api.i18n.{I18nSupport, MessagesApi, Messages, Lang}
import javax.inject.Inject
import utilities._

class OauthClientForm @Inject()(val messagesApi: MessagesApi) extends I18nSupport{
    val form = Form(
    mapping(
      "oauthClientId" -> optional(text),
      "applicationId" -> longNumber,
      "clientSecret" -> text.verifying(Messages("error.required", "シークレット"), {!_.isEmpty})
                            .verifying(Messages("error.maxLength", 255),{_.length <= 255 })
                            .verifying(Messages("error.minLength", 4),{_.length >=4}),
      "redirectUri" -> text.verifying(Messages("error.required", "リダイレクト先"), {!_.isEmpty})
                            .verifying(Messages("error.maxLength", 255),{_.length <= 255 })
                            .verifying(Messages("error.minLength", 4),{_.length >=4}),
      "grantType" -> text.verifying(Messages("error.required", "grant type"), {!_.isEmpty})
                            .verifying(Messages("error.maxLength", 255),{_.length <= 255 })
      )
      (oauthclientapply)(oauthclientunapply)
  )
  private def oauthclientapply(
      oauthClientId: Option[String],
      applicationId: Long, 
      clientSecret:  String,
      redirectUri:  String,
      grantType:  String
       ) = new OauthClientRow(oauthClientId.getOrElse(""), applicationId, Option(clientSecret), Option(redirectUri), Option(grantType), 0 ,new Date)
  private def oauthclientunapply(n: OauthClientRow) = Some(
      (Option(n.oauthClientId), n.applicationId, n.clientSecret.getOrElse(null), n.redirectUri.getOrElse(null), n.grantType.getOrElse(null))
      )

}