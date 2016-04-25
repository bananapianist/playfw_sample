package utilities.contract

import play.api.i18n.Messages.Implicits._


sealed trait Status

object Status {

  case object Active extends Status
  case object Approval extends Status
  case object Stopped extends Status
  case object InActive extends Status

  def valueOf(value: String): Status = value match {
    case ActiveStatus => Active
    case ApprovalStatus    => Approval
    case StoppedStatus    => Stopped
    case InActiveStatus    => InActive
    case _ => throw new IllegalArgumentException()
  }
  
  val ActiveStatus = "Active"
  val ApprovalStatus = "Approval"
  val StoppedStatus = "Stopped"
  val InActiveStatus = "InActive"
  
  val statusSeq = Seq(ActiveStatus, ApprovalStatus, StoppedStatus, InActiveStatus)

}
