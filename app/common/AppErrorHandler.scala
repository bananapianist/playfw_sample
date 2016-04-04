package common

import play.api.http.HttpErrorHandler
import play.api.mvc._
import play.api.mvc.Results._
import scala.concurrent._
import play.Logger

class AppErrorHandler extends HttpErrorHandler {

  def onClientError(request: RequestHeader, statusCode: Int, message: String) = {
    Logger.debug("onClientError")
    Future.successful(
        //Ok(errors.views.html.404notfound())
      //Status(statusCode)("A client error occurred: " + message)
      Status(statusCode)(views.html.errors.error404notfound(message))
    )
  }

  def onServerError(request: RequestHeader, exception: Throwable) = {
    Logger.debug("server error")
    Future.successful(
      InternalServerError("A server error occurred: " + exception.getMessage)
    )
  }
}