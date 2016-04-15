package schedulers


import java.util.concurrent.TimeUnit
import javax.inject.{Singleton, Named, Inject}

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.{Logger, Configuration}
import play.api.inject.ApplicationLifecycle

import akka.actor.{ActorRef, ActorSystem}

@Singleton
class HelloWorldScheduler @Inject() (lifecycle: ApplicationLifecycle, configuration: Configuration, system: ActorSystem, @Named("hello-world-actor") actor: ActorRef){
 val initDelay = Duration(configuration.getNanoseconds("scheduler.helloWorldActor.initDelay")
    .getOrElse(0L), TimeUnit.NANOSECONDS)
  val interval = Duration(configuration.getMilliseconds("scheduler.helloWorldActor.interval")
    .getOrElse(5000L), TimeUnit.MILLISECONDS)
  val cancellable = system.scheduler.schedule(initDelay, interval, actor, None)

  lifecycle.addStopHook { () =>
    Future.successful {
      Logger.info("HelloWorldScheduler shutdown.")
      cancellable.cancel
    }
  }
  
}