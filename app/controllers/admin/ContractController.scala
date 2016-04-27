package controllers.admin

import scala.concurrent.Future
import scala.concurrent.Future.{ successful => future }

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
import play.api.cache._
import play.filters.csrf._
import play.filters.csrf.CSRF.Token
import services.UserAccountServiceLike
import services.dao._
import skinny.util.JSONStringOps._
import views._
import utilities.auth.Role
import utilities.auth.Role._
import utilities.config._
import utilities._

class ContractController @Inject()(addToken: CSRFAddToken, checkToken: CSRFCheck, cache: CacheApi, cached: Cached, val userAccountService: UserAccountServiceLike, contractDao: ContractDAO, billDao: BillDAO, customertDao: CustomerDAO, ContractBillForm:ContractBillForm, ContractBillSearchForm:ContractBillSearchForm, val messagesApi: MessagesApi) extends Controller with AuthActionBuilders with AuthConfigAdminImpl with I18nSupport  {
  
  val UserAccountSv = userAccountService
  
  val pageOffset = 5
  
  
  /** This result directly redirect to the application home.*/
  val Home = Redirect(controllers.admin.routes.ContractController.index(1))

  
//  def index(page: Int = 1) = addToken{
//    AuthorizationAction(NormalUser).async {implicit request =>
//      val urlquery: Map[String,String] = request.queryString.map { case (k,v) => k -> v.mkString }
//      val pagenationOptions = for {
//         (pagecount,contracts) <- contractDao.paginglist(page, pageOffset, urlquery)
//         options <- customertDao.getValidListForSelectOption()
//      } yield ((pagecount,contracts), options)
//
//      pagenationOptions.map{case ((pagecount,contracts), options) =>
//          Ok(views.html.contract.list("", contracts.toList, ContractBillSearchForm.form, options, new PageNation(page, pagecount, pageOffset)))
//
//      }
//   }
//  }
  def index(page: Int = 1) = addToken{
   AuthorizationAction(NormalUser).async {implicit request =>
     val urlquery: Map[String,String] = request.queryString.map { case (k,v) => k -> v.mkString }
     ContractBillSearchForm.form.bindFromRequest.fold(
        formWithErrors => {
          Logger.debug(formWithErrors.toString())
          customertDao.getValidListForSelectOption().map(options => 
             BadRequest(views.html.contract.listerror("エラー", formWithErrors, options))
          )
        },
        contractbillsearch => {
         val pagenationOptions = for {
             (pagecount,contracts) <- contractDao.paginglist(page, pageOffset, urlquery)
             options <- customertDao.getValidListForSelectOption()
          } yield ((pagecount,contracts), options)
    
          pagenationOptions.map{case ((pagecount,contracts), options) =>
              Ok(views.html.contract.list("", contracts.toList, ContractBillSearchForm.form.fill(contractbillsearch), options, new PageNation(page, pagecount, pageOffset)))
    
          }
        }
     )
   }
  }
  def add = addToken{
    AuthorizationAction(NormalUser).async {implicit request =>
      val customerOptions = for {
         options <- customertDao.getValidListForSelectOption()
      } yield (options)
      customerOptions.map { case (options) =>
         Ok(views.html.contract.regist("", ContractBillForm.form, options))
      }
    }
  }
  
  def create = checkToken{
    AuthorizationAction(NormalUser).async { implicit request =>
        ContractBillForm.form.bindFromRequest.fold(
          formWithErrors => {
            Logger.debug(formWithErrors.toString())
            customertDao.getValidListForSelectOption().map(options => 
               BadRequest(views.html.contract.regist("エラー", formWithErrors, options))
            )
          },
          contractbill => {
            contractDao.create(contractbill, contractbill.bill).flatMap(implicit cnt =>
                if (cnt != 0) Future.successful(Redirect(controllers.admin.routes.ContractController.index(1)))
                else Future.successful(Redirect(controllers.admin.routes.ContractController.index(1)))
             )
          }
        )
      
    }
  }

  def delete(id: Long) = checkToken{
    AuthorizationAction(NormalUser).async { implicit request =>
      cache.remove(FormConfig.FormCacheKey)
      contractDao.delete(id).flatMap(cnt =>
        if (cnt != 0) Future.successful(Redirect(controllers.admin.routes.ContractController.index(1)))
        else Future.successful(Status(500)(views.html.errors.error500internalerror("error")))
      )
    }
  }
  
  def edit(contractId: Long) = addToken{
    AuthorizationAction(NormalUser).async {implicit request =>
      cache.remove(FormConfig.FormCacheKey + "-contractedit-" + request.session.get("uuid"))
      val contactOptions = for {
         contract <- contractDao.findById(contractId)
         bill <- billDao.findByContractIdhasOne(contractId)
         options <- customertDao.getValidListForSelectOption()
      } yield (contract, bill, options)
      contactOptions.map { case (contract, bill, options) =>
        contract match {
          case Some(contract) => Ok(views.html.contract.edit("GET", ContractBillForm.form.fill(ContractBillForm.createContactBillRow(contract, bill.getOrElse(null))), options))
          case _ => (Status(404)(views.html.errors.error404notfound("no found")))
        }
      }
    }
  }
  def editback = addToken{
    AuthorizationAction(NormalUser).async {implicit request =>
      val formdata: Option[ContractBillRow] = cache.get[ContractBillRow](FormConfig.FormCacheKey + "-contractedit-" + request.session.get("uuid"))
      formdata match {
          case Some(contractbill) => 
            customertDao.getValidListForSelectOption().map(options => 
              Ok(views.html.contract.edit("GET", ContractBillForm.form.fill(contractbill), options))
              )
          case None => Future.successful(Status(404)(views.html.errors.error404notfound("no found")))
      }
    }
  }
  def editconfirm = addToken{
    AuthorizationAction(NormalUser).async {implicit request =>
        ContractBillForm.form.bindFromRequest.fold(
          formWithErrors => {
            Logger.debug(formWithErrors.toString())
            customertDao.getValidListForSelectOption().map(options => 
               BadRequest(views.html.contract.edit("エラー", formWithErrors, options))
            )
          },
          contractbill => {
            cache.set(FormConfig.FormCacheKey + "-contractedit-" + request.session.get("uuid"), contractbill, FormConfig.FormCacheTime)
            customertDao.getValidListForSelectOption().map(options => 
              Ok(views.html.contract.editconfirm("", cache, options))
            )
          }
       )
    }
  }
  def update = checkToken {
    AuthorizationAction(NormalUser).async { implicit request =>
      val postdata: Option[ContractBillRow] = cache.get[ContractBillRow](FormConfig.FormCacheKey + "-contractedit-" + request.session.get("uuid"))
      cache.remove(FormConfig.FormCacheKey + "-contractedit-" + request.session.get("uuid"))
      postdata match {
          case Some(contractbill) => 
             contractDao.update_mappinged(contractbill, contractbill.bill).flatMap(cnt =>
              if (cnt != 0) {
                Future.successful(Redirect(controllers.admin.routes.ContractController.editsuccess))
              }else {
                customertDao.getValidListForSelectOption().map(options => 
                   BadRequest(views.html.contract.edit("エラー", ContractBillForm.form.fill(contractbill), options))
                )
              }
            )
          case None => Future.successful(Status(500)(views.html.errors.error500internalerror("no formdata")))
      }
    }
  }
  def editsuccess = {
    AuthorizationAction(NormalUser).async {implicit request =>
        Future(Ok(views.html.contract.editsuccess("更新しました")))
    }
  }
  
  def pagenation(currentPageNum: Int, pageName: String) = AuthorizationAction(NormalUser) { implicit request =>
    pageName match {
      case "index" => Redirect(ViewHelper.addRequestQuery(controllers.admin.routes.ContractController.index(currentPageNum), request))
      case _       => Redirect(ViewHelper.addRequestQuery(controllers.admin.routes.ContractController.index(currentPageNum), request))
    }
  }
}