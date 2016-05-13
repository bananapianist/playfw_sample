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
import skinny.util.JSONStringOps._

class CostomerApiController @Inject()(oauth2DataHandler: Oauth2DataHandler, customerDao: CustomerDAO) extends Controller with OAuth2Provider {
  
  def getlist =  Action.async { implicit request =>
    authorize(oauth2DataHandler) { authInfo =>
      val user = authInfo.user 
      customerDao.all().flatMap { customer => 
        Future.successful(Ok(Json.toJson(customer.toList.toString()))) 
      }
     
    }
  }
  def getlistsub =  Action.async { implicit request =>
    Future.successful(Ok(Json.toJson("hamatestt")))
  }
}