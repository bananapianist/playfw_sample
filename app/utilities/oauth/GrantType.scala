package utilities.oauth

import scalaoauth2.provider.OAuthGrantType

sealed trait GrantType

object GrantType {

  case object AuthorizationCode extends GrantType
  case object ClientCredentials extends GrantType
  case object RefreshToken extends GrantType
  case object Implicit extends GrantType
  case object Password extends GrantType
  case object JWTBearer extends GrantType

  def valueOf(value: String): GrantType = value match {
    case AuthorizationCodeGrantType => AuthorizationCode
    case ClientCredentialsGrantType => ClientCredentials
    case RefreshTokenGrantType => RefreshToken
    case ImplicitGrantType => Implicit
    case PasswordGrantType => Password
    case JWTBearerGrantType => JWTBearer
    case _ => throw new IllegalArgumentException()
  }
  
  val AuthorizationCodeGrantType = OAuthGrantType.AUTHORIZATION_CODE
  val ClientCredentialsGrantType = OAuthGrantType.CLIENT_CREDENTIALS
  val RefreshTokenGrantType = OAuthGrantType.REFRESH_TOKEN
  val ImplicitGrantType = OAuthGrantType.IMPLICIT
  val PasswordGrantType = OAuthGrantType.PASSWORD
  val JWTBearerGrantType = "JWTBearer"

 val typeSeq = Seq(AuthorizationCodeGrantType, ClientCredentialsGrantType, RefreshTokenGrantType, ImplicitGrantType, PasswordGrantType, JWTBearerGrantType)

}
