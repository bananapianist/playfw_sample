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
import utilities._
import java.util.Calendar
import java.util.UUID



class OauthClientController @Inject()(addToken: CSRFAddToken, checkToken: CSRFCheck, val userAccountService: UserAccountServiceLike, oauthClientDao: OauthClientDAO, oauthUserDao: OauthUserDAO, oauthClientForm:OauthClientForm, oauthClientSearchForm:OauthClientSearchForm, val messagesApi: MessagesApi) extends Controller with AuthActionBuilders with AuthConfigAdminImpl with I18nSupport  {
  val UserAccountSv = userAccountService

  val pageOffset = 5
  
  /** This result directly redirect to the application home.*/
  val Home = Redirect(controllers.admin.routes.OauthClientController.index(1))

  
  
  def index(page: Int = 1) = addToken{
    AuthorizationAction(Administrator).async { implicit request =>
     val urlquery: Map[String,String] = request.queryString.map { case (k,v) => k -> v.mkString }
     oauthClientSearchForm.form.bindFromRequest.fold(
        formWithErrors => {
          Logger.debug(formWithErrors.toString())
          oauthUserDao.getValidListForSelectOption().map(options => 
             BadRequest(views.html.oauthclient.listerror("エラー", formWithErrors, options))
          )
        },
        oauthclientsearch => {
         val pagenationOptions = for {
             (pagecount,oauthclient) <- oauthClientDao.paginglist(page, pageOffset, urlquery)
             options <- oauthUserDao.getValidListForSelectOption()
          } yield ((pagecount,oauthclient), options)
    
          pagenationOptions.map{case ((pagecount,oauthclient), options) =>
              Ok(views.html.oauthclient.list("", oauthclient.toList, oauthClientSearchForm.form.fill(oauthclientsearch), options, new PageNation(page, pagecount, pageOffset)))
    
          }
        }
     )
   }
  }
  def add = addToken{
    AuthorizationAction(Administrator).async {implicit request =>
      val oauthuserOptions = for {
         options <- oauthUserDao.getValidListForSelectOption()
      } yield (options)
      oauthuserOptions.map { case (options) =>
         Ok(views.html.oauthclient.regist("", oauthClientForm.form, options))
      }

    }
  }
  
  def create = checkToken{
    AuthorizationAction(Administrator).async { implicit request =>
      oauthClientForm.form.bindFromRequest.fold(
          formWithErrors => {
           oauthUserDao.getValidListForSelectOption().map(options => 
               BadRequest(views.html.oauthclient.regist("エラー", formWithErrors, options))
            )
          },
          oauthclient => {
            oauthClientDao.create(oauthclient).flatMap(cnt =>
                if (cnt != 0) Future.successful(Redirect(controllers.admin.routes.OauthClientController.index(1)))
                else Future.successful(Redirect(controllers.admin.routes.OauthClientController.index(1)))
             )
          }
      )
    }
  }
   
  def edit(oauthClientId: String) = addToken{
    AuthorizationAction(Administrator).async {implicit request =>
      val oauthclientOptions = for {
         oauthclient <- oauthClientDao.findById(java.util.UUID.fromString(oauthClientId))
         options <- oauthUserDao.getValidListForSelectOption()
      } yield (oauthclient, options)
      oauthclientOptions.map { case (oauthclient, options) =>
        oauthclient match {
          case Some(oauthclient) => Ok(views.html.oauthclient.edit("GET", oauthClientForm.form.fill(oauthclient), options))
          case _ => (Status(404)(views.html.errors.error404notfound("no found")))
        }
      }

      
      
    }
  }

  def update = checkToken {
    AuthorizationAction(Administrator).async { implicit request =>
      oauthClientForm.form.bindFromRequest.fold(
          formWithErrors => {
           oauthUserDao.getValidListForSelectOption().map(options => 
               BadRequest(views.html.oauthclient.edit("エラー2", formWithErrors, options))
            )
          },
          oauthclient => {
            oauthClientDao.update_mappinged(oauthclient).flatMap(cnt =>
              if (cnt != 0) Future.successful(Redirect(controllers.admin.routes.OauthClientController.index(1)))
               else 
                oauthUserDao.getValidListForSelectOption().map(options => 
                 BadRequest(views.html.oauthclient.edit("エラー3", oauthClientForm.form.fill(oauthclient), options))
                )
            )
          }
      )
    }
  }
  
    def delete(id: String) = checkToken{
      AuthorizationAction(Administrator).async {implicit request =>
        oauthClientDao.delete(java.util.UUID.fromString(id)).flatMap(cnt =>
          if (cnt != 0) Future.successful(Redirect(controllers.admin.routes.OauthClientController.index(1)))
          else Future.successful(Status(500)(views.html.errors.error500internalerror("error")))
        )
      }
    }

  def pagenation(currentPageNum: Int, pageName: String) = AuthorizationAction(NormalUser) { implicit request =>
    pageName match {
      case "index" => Redirect(ViewHelper.addRequestQuery(controllers.admin.routes.OauthClientController.index(currentPageNum), request))
      case _       => Redirect(ViewHelper.addRequestQuery(controllers.admin.routes.OauthClientController.index(currentPageNum), request))
    }
  }


}