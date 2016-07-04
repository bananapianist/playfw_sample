package controllers.api

import javax.inject.Inject

import play.api.mvc._
import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.libs.json.{Json, Writes}

import models.TablesExtend._
import services.{Oauth2DataHandler, MyTokenEndpoint}
import services.dao._


import scala.concurrent.Future
import scalaoauth2.provider._
import scalaoauth2.provider.OAuth2ProviderActionBuilders._

import play.api.libs.json._

class CostomerApiController @Inject()(oauth2DataHandler: Oauth2DataHandler, customerDao: CustomerDAO) extends Controller with OAuth2Provider {
  implicit val residentWrites = new Writes[CustomerRow] {
  def writes(customer: CustomerRow) = Json.obj(
    "id" -> customer.id,
    "name" -> customer.name,
    "email" -> customer.email,
    "tel" -> customer.tel,
    "address" -> customer.address,
    "comment" -> customer.comment,
    "actionDate" -> customer.actionDate,
    "notificationPeriod" -> customer.notificationPeriod,
    "notificationDate" -> customer.notificationDate,
    "isDisabled" -> customer.isDisabled,
    "createdDate" -> customer.createdDate,
    "updatedDate" -> customer.updatedDate  )
  }
  def getlist =  Action.async { implicit request =>
    authorize(oauth2DataHandler) { authInfo =>
      val user = authInfo.user 
      customerDao.all().flatMap { customers => 
        Future.successful(Ok(Json.toJson(customers))) 
      }
     
    }
  }
  def getlistsub =  Action.async { implicit request =>
    Future.successful(Ok(Json.toJson("hamatestt")))
  }
}