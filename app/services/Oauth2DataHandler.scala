package services

import javax.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Success

import scalaoauth2.provider._
import scalaoauth2.provider.OAuth2ProviderActionBuilders._

import services.dao._
import models.TablesExtend._

import java.util.Date
import org.mindrot.jbcrypt.BCrypt
import java.util.UUID

class Oauth2DataHandler @Inject()(oauthClientDAO: OauthClientDAO, oauthUserDAO: OauthUserDAO, accessTokenDAO: AccessTokenDAO, authCodeDAO: AuthCodeDAO) extends DataHandler[OauthUserRow] {

  import scala.concurrent.ExecutionContext.Implicits.global
  
  override def validateClient(request: AuthorizationRequest): Future[Boolean] = {
    request.clientCredential.map { clientCredential =>
   
       oauthClientDAO.validate(java.util.UUID.fromString(clientCredential.clientId), clientCredential.clientSecret, request.grantType).flatMap(cnt =>
         if (cnt > 0) Future.successful(true)
         else Future.successful(false)
       )
    }.getOrElse(Future.successful(false))
   }

  override def getStoredAccessToken(authInfo: AuthInfo[OauthUserRow]): Future[Option[scalaoauth2.provider.AccessToken]] = {
   accessTokenDAO.findByToken(authInfo.user.guid, java.util.UUID.fromString(authInfo.clientId.getOrElse(""))).map { optToken =>
      optToken.map { token =>
        scalaoauth2.provider.AccessToken(token.accessToken, token.refreshToken, token.scope, Option(token.expiresIn.toLong), token.createdDate)
      }
    }
     
   }

  override def createAccessToken(authInfo: AuthInfo[OauthUserRow]): Future[scalaoauth2.provider.AccessToken] = {
    val accessTokenExpiresIn =  60L * 60L
    val refreshToken = Some(Crypto.generateToken)
    val accessToken = Crypto.generateToken
    val now = new Date
    
    val tokenObject = new AccessTokenRow(accessToken, refreshToken, authInfo.user.guid, java.util.UUID.fromString(authInfo.clientId.getOrElse("")), Option(""), authInfo.scope, accessTokenExpiresIn.toInt, new Date)
    accessTokenDAO.deleteExistingAndCreate(tokenObject, authInfo.user.guid, java.util.UUID.fromString(authInfo.clientId.getOrElse("")))
    Future.successful(scalaoauth2.provider.AccessToken(accessToken, refreshToken, authInfo.scope, Option(accessTokenExpiresIn), now))
   }

  override def findUser(request: AuthorizationRequest): Future[Option[OauthUserRow]] = {
      request match {
       case request: PasswordRequest =>
         Future.successful(None)
          //Future.successful(Account.authenticate(request.username, request.password))
         case request: ClientCredentialsRequest =>
          request.clientCredential.map { clientCredential =>
            oauthClientDAO.findByClientCredentialsReturnOauthUser(
              java.util.UUID.fromString(clientCredential.clientId),
              clientCredential.clientSecret.getOrElse("")
            )
          }.getOrElse(Future.successful(None))
        case _ =>
          Future.successful(None)
      }
   }

  override def findAuthInfoByRefreshToken(refreshToken: String): Future[Option[AuthInfo[OauthUserRow]]] = {
    val tokenuser = for {
       tokenusertuple <- accessTokenDAO.getWithOauthUserByRefreshToken(refreshToken)
    } yield (tokenusertuple)
    tokenuser.map { tuple =>
      tuple.map{ tokenusertuple =>
        AuthInfo(tokenusertuple._2, Option(tokenusertuple._1.oauthClientId.toString()), tokenusertuple._1.scope, tokenusertuple._1.redirectUri)
      }
    }
   }


  override def refreshAccessToken(authInfo: AuthInfo[OauthUserRow], refreshToken: String): Future[scalaoauth2.provider.AccessToken] = {
     createAccessToken(authInfo)
   }

  override def findAuthInfoByCode(code: String): Future[Option[AuthInfo[OauthUserRow]]] = {
    val codeuser = for {
       codeusertuple <- authCodeDAO.getWithOauthUserByCode(code)
    } yield (codeusertuple)
    codeuser.map { tuple =>
      tuple.map{ codeusertuple =>
        AuthInfo(codeusertuple._2, Option(codeusertuple._1.oauthClientId.toString()), codeusertuple._1.scope, codeusertuple._1.redirectUri)
      }
    }
  }

  override def deleteAuthCode(code: String): Future[Unit] = {
     Future.successful(authCodeDAO.delete(code))
   }


  override def findAccessToken(token: String): Future[Option[scalaoauth2.provider.AccessToken]] = {
    accessTokenDAO.findByAccessToken(token).map { optToken =>
      optToken.map { token =>
        scalaoauth2.provider.AccessToken(token.accessToken, token.refreshToken, token.scope, Option(token.expiresIn.toLong), token.createdDate)
      }
    }

  }


  override def findAuthInfoByAccessToken(accessToken: scalaoauth2.provider.AccessToken): Future[Option[AuthInfo[OauthUserRow]]]  ={
    val tokenuser = for {
       tokenusertuple <- accessTokenDAO.getWithOauthUserByAccessToken(accessToken.token)
    } yield (tokenusertuple)
    tokenuser.map { tuple =>
      tuple.map{ tokenusertuple =>
        AuthInfo(tokenusertuple._2, Option(tokenusertuple._1.oauthClientId.toString()), tokenusertuple._1.scope, tokenusertuple._1.redirectUri)
      }
    }
   }


}

object Crypto {
  def generateToken: String = {
    val key = java.util.UUID.randomUUID.toString
    new sun.misc.BASE64Encoder().encode(key.getBytes)
  }
}
