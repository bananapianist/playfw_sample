package controllers

import java.util.Date
import scala.concurrent.Future
import dao.CustomerDAO
import javax.inject.Inject
import models.Tables._
import common._
import forms._
import play.api._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc._
import play.api.mvc.Action
import play.api.mvc.Controller
import skinny.util.JSONStringOps._
import java.sql.Timestamp
import org.joda.time.DateTime
import org.joda.time.format._
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.filters.csrf._
import play.filters.csrf.CSRF.Token


class CustomerController @Inject()(addToken: CSRFAddToken, checkToken: CSRFCheck, customerDao: CustomerDAO, CustomerForm:CustomerForm) extends Controller  {
  /** This result directly redirect to the application home.*/
  val Home = Redirect(routes.CustomerController.index())

  def getToken = addToken(Action { implicit request =>
    val Token(name, value) = CSRF.getToken.get
    Ok(s"$name=$value")
  })
  
  def index = addToken{
    Action.async { 
      customerDao.all().map(customers => Ok(views.html.customerlist("", customers)))
    }
  }
  def add = addToken{
    Action {implicit request =>
      Ok(views.html.customerregist("", CustomerForm.form))
    }
  }
  
  def create = checkToken{
    Action.async { implicit request =>
      CustomerForm.form.bindFromRequest.fold(
          formWithErrors => {
            Logger.debug(formWithErrors.toString())
            Future(BadRequest(views.html.customerregist("エラー", formWithErrors)))
          },
          customer => {
            customerDao.create(customer).flatMap(cnt =>
                //if (cnt != 0) customerDao.all().map(customers => Ok(views.html.customerlist("登録しました", customers)))
                if (cnt != 0) Future.successful(Redirect(routes.CustomerController.index))
                else customerDao.all().map(notifications => BadRequest(views.html.customeredit("エラー", CustomerForm.form.fill(customer))))
             )
          }
      )
    }
  }
   
  def edit(customerId: Long) = addToken{
    Action.async {implicit request =>
      customerDao.findById(customerId).flatMap(option =>
        option match {
          case Some(customer) => Future(Ok(views.html.customeredit("GET", CustomerForm.form.fill(customer))))
          case None => customerDao.all().map(customers => BadRequest(views.html.customerlist("エラー", customers)))
        }
      )
    }
  }

  def update = checkToken {
    Action.async { implicit request =>
      CustomerForm.form.bindFromRequest.fold(
          formWithErrors => {
            Future(BadRequest(views.html.customeredit("ERROR", formWithErrors)))
          },
          customer => {
            customerDao.update_mappinged(customer).flatMap(cnt =>
              if (cnt != 0) customerDao.all().map(customers => Ok(views.html.customerlist("更新しました", customers)))
              //if (cnt != 0) Future.successful(Redirect(routes.CustomerController.index))
              else customerDao.all().map(notifications => BadRequest(views.html.customeredit("エラー", CustomerForm.form.fill(customer))))
            )
          }
      )
    }
  }
  
    def delete(id: Long) = checkToken{
      Action.async {
        customerDao.delete(id).flatMap(cnt =>
          if (cnt != 0) customerDao.all().map(customers => Ok(views.html.customerlist("削除しました", customers)))
          else customerDao.all().map(customers => BadRequest(views.html.customerlist("エラー", customers)))
        )
      }
    }
    //  def register() = Action.async { implicit request =>
//    val query = Customer.map(_.id)
//    val resultingCustomers = dbConfig.db.run(query.result)
//  }



}