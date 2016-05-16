package controllers.oauth

import javax.inject.Inject

import play.api.mvc._
import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.libs.json.{Json, Writes}

import models.TablesExtend._
import services.{Oauth2DataHandler, MyTokenEndpoint, OAuth2ProviderExtend, OAuth2ProviderExtendResult}


import scala.concurrent.Future
import scalaoauth2.provider._
import scalaoauth2.provider.OAuth2ProviderActionBuilders._

import play.Logger

class OAuthController @Inject()(oauth2DataHandler: Oauth2DataHandler) extends Controller with OAuth2Provider with OAuth2ProviderExtend {
  override val tokenEndpoint: TokenEndpoint = MyTokenEndpoint
  
  implicit val authInfoWrites = new Writes[AuthInfo[OauthUserRow]] {
    def writes(authInfo: AuthInfo[OauthUserRow]) = {
      Json.obj(
        "account" -> Json.obj(
          "name" -> authInfo.user.name
        ),
        "clientId" -> authInfo.clientId,
        "redirectUri" -> authInfo.redirectUri
      )
    }
  }

  def accessToken = Action.async { implicit request =>
    Logger.debug(request.toString())
    Logger.debug(request.body.asFormUrlEncoded.getOrElse("").toString())
    issueAccessToken(oauth2DataHandler)
  }

  def resources = AuthorizedAction(oauth2DataHandler) { request =>
    Ok(Json.toJson(request.authInfo))
  }
  
  def authcode = Action.async { implicit request =>
    Logger.debug(request.toString())
    Logger.debug(request.body.asFormUrlEncoded.getOrElse("").toString())
    issueAuthCode(request, oauth2DataHandler)
  }

  
}