package services

import securesocial.core.services.RoutesService
import play.api.mvc.{ RequestHeader, AnyContent, Action }
import securesocial.core.{ RuntimeEnvironment, IdentityProvider }
import controllers._

class CustomRoutesService extends RoutesService.Default {
  override def loginPageUrl(implicit req: RequestHeader): String = controllers.routes.CustomLoginController.login().absoluteURL(IdentityProvider.sslEnabled)
}