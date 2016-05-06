package schedulers


import java.util.concurrent.TimeUnit
import javax.inject.{Singleton, Named, Inject}

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.{Logger, Configuration}
import play.api.inject.ApplicationLifecycle

import akka.actor.{ActorRef, ActorSystem}
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension

@Singleton
class CustomerCountBatchScheduler @Inject() (lifecycle: ApplicationLifecycle, configuration: Configuration, system: ActorSystem, @Named("CustomerCountBatch-actor") actor: ActorRef){
  val cancellable = QuartzSchedulerExtension(system).schedule("15o'clockAnd18o'clock", actor, "バッチ起動")

  lifecycle.addStopHook { () =>
    Future.successful {
      Logger.info("CustomerCountBatchScheduler shutdown.")
      
    }
  }
  
}