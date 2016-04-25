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
import skinny.util.JSONStringOps._
import views._
import utilities.auth.Role
import utilities.auth.Role._
import java.util.Calendar

import akka.actor._
import actors.MailsendActor
import play.api.libs.mailer.MailerClient
import scala.concurrent.duration._

class AccountController @Inject()(addToken: CSRFAddToken, checkToken: CSRFCheck, val userAccountService: UserAccountServiceLike, accountDao: AccountDAO, AccountForm:AccountForm, val messagesApi: MessagesApi, system: ActorSystem, mc: MailerClient) extends Controller with AuthActionBuilders with AuthConfigAdminImpl with I18nSupport  {
  val UserAccountSv = userAccountService

  Logger.info("start Actaor")
  val mailsendActor = system.actorOf(MailsendActor.props(mc), "mailclient-actor"+ scala.util.Random.alphanumeric.take(10).mkString )
  var cl = Calendar.getInstance
  cl.set(Calendar.SECOND, 0)
  cl.set(Calendar.MILLISECOND, 0)
  cl.add(Calendar.MINUTE, 1)
  system.scheduler.schedule(
      (cl.getTimeInMillis - System.currentTimeMillis).milliseconds,
      120.seconds,
      mailsendActor,
      "mailsend"
  )
  Logger.info("Actor has started")
  
  /** This result directly redirect to the application home.*/
  val Home = Redirect(controllers.admin.routes.AccountController.index())

  
  def getToken = addToken(Action { implicit request =>
    val Token(name, value) = CSRF.getToken.get
    Ok(s"$name=$value")
  })
  
  def index = addToken{
    AuthorizationAction(Administrator).async { implicit request =>
      accountDao.all().map(accounts => Ok(views.html.account.list("", accounts)))
    }
  }
  def add = addToken{
    AuthorizationAction(Administrator) {implicit request =>
      Ok(views.html.account.regist("", AccountForm.formnew))
    }
  }
  
  def create = checkToken{
    AuthorizationAction(Administrator).async { implicit request =>
      AccountForm.formnew.bindFromRequest.fold(
          formWithErrors => {
            Logger.debug(formWithErrors.toString())
            Future(BadRequest(views.html.account.regist("エラー", formWithErrors)))
          },
          account => {
            accountDao.create(account).flatMap(cnt =>
                //if (cnt != 0) accountDao.all().map(accounts => Ok(views.html.account.list("登録しました", accounts)))
                if (cnt != 0) Future.successful(Redirect(controllers.admin.routes.AccountController.index))
                else accountDao.all().map(notifications => BadRequest(views.html.account.edit("エラー", AccountForm.formnew.fill(account))))
             )
          }
      )
    }
  }
   
  def edit(accountId: Int) = addToken{
    AuthorizationAction(Administrator).async {implicit request =>
      accountDao.findById(accountId).flatMap(option =>
        option match {
          case Some(account) => Future(Ok(views.html.account.edit("GET", AccountForm.formedit.fill(account))))
          case None => accountDao.all().map(accounts => BadRequest(views.html.account.list("エラー", accounts)))
        }
      )
    }
  }

  def update = checkToken {
    AuthorizationAction(Administrator).async { implicit request =>
      AccountForm.formedit.bindFromRequest.fold(
          formWithErrors => {
            Future(BadRequest(views.html.account.edit("ERROR", formWithErrors)))
          },
          account => {
            accountDao.update_mappinged(account).flatMap(cnt =>
              if (cnt != 0) accountDao.all().map(accounts => Ok(views.html.account.list("更新しました", accounts)))
              //if (cnt != 0) Future.successful(Redirect(routes.AccountController.index))
              else accountDao.all().map(notifications => BadRequest(views.html.account.edit("エラー", AccountForm.formedit.fill(account))))
            )
          }
      )
    }
  }
  
    def delete(id: Int) = checkToken{
      AuthorizationAction(Administrator).async {implicit request =>
        accountDao.delete(id).flatMap(cnt =>
          if (cnt != 0) accountDao.all().map(accounts => Ok(views.html.account.list("削除しました", accounts)))
          else accountDao.all().map(accounts => BadRequest(views.html.account.list("エラー", accounts)))
        )
      }
    }



}