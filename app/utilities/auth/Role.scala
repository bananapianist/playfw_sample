package utilities.auth

import play.api.i18n.Messages.Implicits._
import play.api.Play.current

sealed trait Role

object Role {

  case object Administrator extends Role
  case object NormalUser extends Role

  def valueOf(value: String): Role = value match {
    case AdministratorRole => Administrator
    case NormalUserRole    => NormalUser
    case _ => throw new IllegalArgumentException()
  }
  
  val AdministratorRole = "Administrator"
  val NormalUserRole = "NormalUser"
  //val options = Seq("Administrator" -> Messages("view.formparts.administrator"), "NormalUser" -> Messages("view.formparts.normaluser"))

}
