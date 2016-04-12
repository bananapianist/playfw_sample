package controllers.auth

import controllers.BaseAuthConfig
import play.api.mvc.RequestHeader
import play.api.mvc.Results._
import scala.concurrent.{Future, ExecutionContext}



trait AuthConfigImpl extends BaseAuthConfig {

  def loginSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext) = Future.successful(Redirect(controllers.admin.routes.CustomerController.index(1)))

  def logoutSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext) = Future.successful(Redirect(controllers.auth.routes.LoginLogoutController.index))

  def authenticationFailed(request: RequestHeader)(implicit ctx: ExecutionContext) = Future.successful(Redirect(controllers.auth.routes.LoginLogoutController.index))

}