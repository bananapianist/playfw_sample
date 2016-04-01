package controllers

import javax.inject.Inject
import securesocial.controllers.BaseLoginPage
import play.api.mvc.{ RequestHeader, AnyContent, Action }
import play.api.Logger
import securesocial.core.{ RuntimeEnvironment, IdentityProvider }
import securesocial.core.services.RoutesService


class CustomLoginController @Inject() (implicit override val env: RuntimeEnvironment) extends BaseLoginPage {
  override def login: Action[AnyContent] = {
    Logger.debug("Using CustomLoginController")
    super.login
  }
  override def logout: Action[AnyContent] = {
    Logger.debug("Using CustomLoginController")
    super.logout
  }
}

