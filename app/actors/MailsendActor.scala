package actors

import play.api.Logger

import akka.actor.{Props, Actor}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.mailer.{MailerClient, Email}
import com.typesafe.config.ConfigFactory
import javax.inject.Inject

object MailsendActor {
  def props(mc: MailerClient) = Props(classOf[MailsendActor], mc)
}

class MailsendActor @Inject()(mc: MailerClient) extends Actor {
  val config = ConfigFactory.load()
  val mailFrom = config.getString("notify.mail.from")
  val mailTo = config.getString("notify.mail.to")
  
  val Subject = "play テスト"
  val MailBody = "本文本文\n\nここここ"
  
  override def receive: Receive = {
    case "mailsend" => {
      Logger.info((new java.util.Date).toString)
      Logger.info("Mailsend Message received")
          val email = Email(
            Subject,
            mailFrom,
            Seq(mailTo),
            bodyText = Some(MailBody),
            bodyHtml = Some("")
          )
          //mc.send(email)
          Logger.info("Mail sent")
    }
  }

}