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
import java.util.UUID

import play.api.libs.json._

class OauthUserController @Inject()(addToken: CSRFAddToken, checkToken: CSRFCheck, val userAccountService: UserAccountServiceLike, oauthUserDao: OauthUserDAO, oauthUserForm:OauthUserForm, val messagesApi: MessagesApi) extends Controller with AuthActionBuilders with AuthConfigAdminImpl with I18nSupport  {
  val UserAccountSv = userAccountService

  
  /** This result directly redirect to the application home.*/
  val Home = Redirect(controllers.admin.routes.OauthUserController.index())

  
  
  def nameautocomplete(name: String) = addToken{
    AuthorizationAction(NormalUser).async {implicit request =>
      oauthUserDao.findByNameList(name).map{case oauthuser =>
        Ok(Json.toJson(oauthuser))
      }
    }
  }

  def index = addToken{
    AuthorizationAction(Administrator).async { implicit request =>
      oauthUserDao.all().map(accounts => Ok(views.html.oauthuser.list("", accounts)))
    }
  }
  def add = addToken{
    AuthorizationAction(Administrator) {implicit request =>
      Ok(views.html.oauthuser.regist("", oauthUserForm.form))
    }
  }
  
  def create = checkToken{
    AuthorizationAction(Administrator).async { implicit request =>
      oauthUserForm.form.bindFromRequest.fold(
          formWithErrors => {
            Logger.debug(formWithErrors.toString())
            Future(BadRequest(views.html.oauthuser.regist("エラー", formWithErrors)))
          },
          oauthuser => {
            oauthUserDao.create(oauthuser).flatMap(cnt =>
                if (cnt != 0) Future.successful(Redirect(controllers.admin.routes.OauthUserController.index))
                else oauthUserDao.all().map(notifications => BadRequest(views.html.oauthuser.edit("エラー", oauthUserForm.form.fill(oauthuser))))
             )
          }
      )
    }
  }
   
  def edit(accountId: String) = addToken{
    AuthorizationAction(Administrator).async {implicit request =>
      oauthUserDao.findById(java.util.UUID.fromString(accountId)).flatMap(option =>
        option match {
          case Some(oauthuser) => Future(Ok(views.html.oauthuser.edit("GET", oauthUserForm.form.fill(oauthuser))))
          case None => oauthUserDao.all().map(accounts => BadRequest(views.html.oauthuser.list("エラー", accounts)))
        }
      )
    }
  }

  def update = checkToken {
    AuthorizationAction(Administrator).async { implicit request =>
      oauthUserForm.form.bindFromRequest.fold(
          formWithErrors => {
            Future(BadRequest(views.html.oauthuser.edit("ERROR", formWithErrors)))
          },
          oauthuser => {
            oauthUserDao.update_mappinged(oauthuser).flatMap(cnt =>
              if (cnt != 0) oauthUserDao.all().map(oauthusers => Ok(views.html.oauthuser.list("更新しました", oauthusers)))
              //if (cnt != 0) Future.successful(Redirect(routes.OauthUserController.index))
              else oauthUserDao.all().map(notifications => BadRequest(views.html.oauthuser.edit("エラー", oauthUserForm.form.fill(oauthuser))))
            )
          }
      )
    }
  }
  
    def delete(id: String) = checkToken{
      AuthorizationAction(Administrator).async {implicit request =>
        oauthUserDao.delete(java.util.UUID.fromString(id)).flatMap(cnt =>
          if (cnt != 0) oauthUserDao.all().map(oauthusers => Ok(views.html.oauthuser.list("削除しました", oauthusers)))
          else oauthUserDao.all().map(oauthusers => BadRequest(views.html.oauthuser.list("エラー", oauthusers)))
        )
      }
    }



}