package controllers.auth

import javax.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import java.util.Date
import scala.concurrent.Future
import javax.inject.Inject
import services._
import services.dao._
import models.TablesExtend._
import utilities._
import forms._
import views._
import play.api._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc._
import play.api.mvc.Action
import play.api.mvc.Controller
import skinny.util.JSONStringOps._
import java.sql.Timestamp
import org.joda.time.DateTime
import org.joda.time.format._
import play.api.i18n.{MessagesApi, I18nSupport}

import play.filters.csrf._
import play.filters.csrf.CSRF.Token

import jp.t2v.lab.play2.auth.LoginLogout


class LoginLogoutController @Inject()(addToken: CSRFAddToken, checkToken: CSRFCheck, val userAccountService: UserAccountServiceLike, LoginForm:LoginForm, val messagesApi: MessagesApi)
  extends Controller with LoginLogout with AuthConfigImpl with I18nSupport{

  val UserAccountSv = userAccountService
  
  def index = addToken{
    Action { implicit request =>
      Ok(views.html.auth.login(LoginForm.loginForm))
    }
  }

  def login() = checkToken {
    Action.async { implicit request =>
      LoginForm.loginForm.bindFromRequest.fold(
        formWithErrors => {
          Future.successful(BadRequest(views.html.auth.login(formWithErrors)))
        },
        account => {
          userAccountService.authenticate(account).flatMap {
            user => user match {
              case Some(user) =>
                gotoLoginSucceeded(account.id)
              case _ =>
                Future.successful(Unauthorized(views.html.auth.login(LoginForm.loginForm.fill(account))))
            }
          }
        }
      )
    }
  }

  def logout() = checkToken {
    Action.async { implicit request =>
      gotoLogoutSucceeded
    }
  }
}

