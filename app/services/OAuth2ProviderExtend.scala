package services

import play.api.libs.json._
import play.api.mvc._

import scalaoauth2.provider._
import scala.concurrent.{ ExecutionContext, Future }
import scala.language.implicitConversions
import services._

import models.TablesExtend._

import play.Logger

case class OAuth2ProviderExtendResult[U](
  authInfo: AuthInfo[U],
  tokenType: String,
  authorizationCode: String,
  expiresIn: Option[Long],
  scope: Option[String])

trait OAuth2ProviderExtend extends OAuth2BaseProvider  {

  def issueAuthCode(request: AuthorizationRequest, handler: Oauth2DataHandler)(implicit ctx: ExecutionContext): Future[Result] = {
    val authorizationCodeRequest = new AuthorizationCodeRequest(request)
    val clientCredential = authorizationCodeRequest.clientCredential.getOrElse(throw new InvalidRequest("Client credential is required"))
    val scope = authorizationCodeRequest.scope
    val redirect_uri = authorizationCodeRequest.redirectUri
    handler.findUser(authorizationCodeRequest).flatMap { optionalUser =>
      val user = optionalUser.getOrElse(throw new InvalidGrant("client_id or client_secret or scope is incorrect"))
      val authInfo = AuthInfo(user, Some(clientCredential.clientId), scope, redirect_uri)
      handler.getStoredAuthCode(authInfo).flatMap {
        case Some(authcode) if shouldRefreshAuthCode(authcode) => handler.createAuthCode(authInfo)
        case Some(authcode) => Future.successful(authcode)
        case None => handler.createAuthCode(authInfo)
      }.map(code =>
          Ok(Json.toJson(responseAuthCode(createOAuth2ProviderExtendResult(authInfo, code))))
         )
      }
    
  }
  protected def shouldRefreshAuthCode(code: Oauth2DataHandlerObj.AuthorizationCode) = code.isExpired
  
  protected def createOAuth2ProviderExtendResult(authInfo: AuthInfo[models.TablesExtend.OauthUserRow], authCode: Oauth2DataHandlerObj.AuthorizationCode) = OAuth2ProviderExtendResult(
    authInfo,
    "Bearer",
    authCode.code,
    authCode.expiresIn,
    authCode.scope
  )
  protected def responseAuthCode[OauthUserRow](r: OAuth2ProviderExtendResult[OauthUserRow]) = {
    Map[String, JsValue](
      "token_type" -> JsString(r.tokenType),
      "authorization_code" -> JsString(r.authorizationCode)
      ) ++ r.expiresIn.map {
        "expires_in" -> JsNumber(_)
      } ++ r.scope.map {
        "scope" -> JsString(_)
      }
  }
}