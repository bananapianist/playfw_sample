package utilities.oauth


sealed trait GrantType

object GrantType {

  case object AuthorizationCode extends GrantType
  case object ClientCredentials extends GrantType
  case object RefreshToken extends GrantType
  case object Implicit extends GrantType
  case object JWTBearer extends GrantType

  def valueOf(value: String): GrantType = value match {
    case AuthorizationCodeGrantType => AuthorizationCode
    case ClientCredentialsGrantType => ClientCredentials
    case RefreshTokenGrantType => RefreshToken
    case ImplicitGrantType => Implicit
    case JWTBearerGrantType => JWTBearer
    case _ => throw new IllegalArgumentException()
  }
  
  val AuthorizationCodeGrantType = "AuthorizationCode"
  val ClientCredentialsGrantType = "ClientCredentials"
  val RefreshTokenGrantType = "RefreshToken"
  val ImplicitGrantType = "Implicit"
  val JWTBearerGrantType = "JWTBearer"

 val typeSeq = Seq(AuthorizationCodeGrantType, ClientCredentialsGrantType, RefreshTokenGrantType, ImplicitGrantType, JWTBearerGrantType)

}
