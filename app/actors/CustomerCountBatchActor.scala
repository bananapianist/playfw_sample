package actors

import play.api.Logger

import akka.actor.{Props, Actor}

import services.dao._
import javax.inject.Inject
import scala.concurrent.ExecutionContext

object CustomerCountBatchActor {
  def props = Props[CustomerCountBatchActor]
}

class CustomerCountBatchActor @Inject()(customerDao: CustomerDAO)(implicit e :ExecutionContext)  extends Actor {
  override def receive: Receive = {
    case msg => {
      Logger.info(s"CustomerCountBatchActor --- $msg")
      customerDao.count().map{case count =>
         Logger.info(s"CustomerCountBatchActor --- $count")
      }
    }
  }

}