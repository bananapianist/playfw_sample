package controllers.admin

import scala.concurrent.Future
import scala.concurrent.Future.{successful => future}
import javax.inject.Inject

import jp.t2v.lab.play2.auth.AuthElement
import utilities.auth.Role
import utilities.auth.Role._
import play.api.mvc._
import play.api.libs.json._
import controllers.auth.AuthConfigAdminImpl
import services.UserAccountServiceLike



class UserApi @Inject()(val userAccountService: UserAccountServiceLike) extends Controller with AuthElement with AuthConfigAdminImpl {
  val UserAccountSv = userAccountService
  def normaluser = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    val user = loggedIn
    Ok(Json.obj("id" -> user.id, "username" -> user.name, "email" -> user.email, "role" -> user.role))
  }
  def adminuser = StackAction(AuthorityKey -> Administrator) { implicit request =>
    val user = loggedIn
    Ok(Json.obj("id" -> user.id, "username" -> user.name, "email" -> user.email, "role" -> user.role))
  }
}