package controllers.admin

import scala.concurrent.Future
import scala.concurrent.Future.{successful => future}

import controllers.auth.AuthConfigAdminImpl
import forms._
import javax.inject.Inject
import jp.t2v.lab.play2.auth.AuthActionBuilders
import models.TablesExtend._
import play.api._
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc._
import play.api.mvc.Action
import play.api.mvc.Controller
import play.filters.csrf._
import play.filters.csrf.CSRF.Token
import services.UserAccountServiceLike
import services.dao._
import views._
import utilities.auth.Role
import utilities.auth.Role._
import java.util.Calendar

import play.api.libs.json._

class AppAccountController @Inject()(addToken: CSRFAddToken, checkToken: CSRFCheck, val userAccountService: UserAccountServiceLike, appAccountDao: AppAccountDAO, appAccountForm:AppAccountForm, val messagesApi: MessagesApi) extends Controller with AuthActionBuilders with AuthConfigAdminImpl with I18nSupport  {
  val UserAccountSv = userAccountService

  
  /** This result directly redirect to the application home.*/
  val Home = Redirect(controllers.admin.routes.AppAccountController.index())

  
  
  def nameautocomplete(name: String) = addToken{
    AuthorizationAction(NormalUser).async {implicit request =>
      appAccountDao.findByNameList(name).map{case appaccount =>
        Ok(Json.toJson(appaccount))
      }
    }
  }

  def index = addToken{
    AuthorizationAction(Administrator).async { implicit request =>
      appAccountDao.all().map(accounts => Ok(views.html.appaccount.list("", accounts)))
    }
  }
  def add = addToken{
    AuthorizationAction(Administrator) {implicit request =>
      Ok(views.html.appaccount.regist("", appAccountForm.form))
    }
  }
  
  def create = checkToken{
    AuthorizationAction(Administrator).async { implicit request =>
      appAccountForm.form.bindFromRequest.fold(
          formWithErrors => {
            Logger.debug(formWithErrors.toString())
            Future(BadRequest(views.html.appaccount.regist("エラー", formWithErrors)))
          },
          appaccount => {
            appAccountDao.create(appaccount).flatMap(cnt =>
                if (cnt != 0) Future.successful(Redirect(controllers.admin.routes.AppAccountController.index))
                else appAccountDao.all().map(notifications => BadRequest(views.html.appaccount.edit("エラー", appAccountForm.form.fill(appaccount))))
             )
          }
      )
    }
  }
   
  def edit(accountId: Long) = addToken{
    AuthorizationAction(Administrator).async {implicit request =>
      appAccountDao.findById(accountId).flatMap(option =>
        option match {
          case Some(appaccount) => Future(Ok(views.html.appaccount.edit("GET", appAccountForm.form.fill(appaccount))))
          case None => appAccountDao.all().map(accounts => BadRequest(views.html.appaccount.list("エラー", accounts)))
        }
      )
    }
  }

  def update = checkToken {
    AuthorizationAction(Administrator).async { implicit request =>
      appAccountForm.form.bindFromRequest.fold(
          formWithErrors => {
            Future(BadRequest(views.html.appaccount.edit("ERROR", formWithErrors)))
          },
          appaccount => {
            appAccountDao.update_mappinged(appaccount).flatMap(cnt =>
              if (cnt != 0) appAccountDao.all().map(appaccounts => Ok(views.html.appaccount.list("更新しました", appaccounts)))
              //if (cnt != 0) Future.successful(Redirect(routes.AppAccountController.index))
              else appAccountDao.all().map(notifications => BadRequest(views.html.appaccount.edit("エラー", appAccountForm.form.fill(appaccount))))
            )
          }
      )
    }
  }
  
    def delete(id: Long) = checkToken{
      AuthorizationAction(Administrator).async {implicit request =>
        appAccountDao.delete(id).flatMap(cnt =>
          if (cnt != 0) appAccountDao.all().map(appaccounts => Ok(views.html.appaccount.list("削除しました", appaccounts)))
          else appAccountDao.all().map(appaccounts => BadRequest(views.html.appaccount.list("エラー", appaccounts)))
        )
      }
    }



}