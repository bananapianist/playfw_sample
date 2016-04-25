package actors

import play.api.Logger

import akka.actor.{Props, Actor}


object HelloWorldActor {
  def props = Props[HelloWorldActor]
}

class HelloWorldActor extends Actor {
  override def receive: Receive = {
    case _ => {
      Logger.info("Actor test --- Hello world!")
    }
  }

}