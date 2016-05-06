package modules


import play.api.{Configuration, Environment}
import play.api.inject.{Binding, Module}
import play.api.libs.concurrent.AkkaGuiceSupport

import actors.CustomerCountBatchActor
import com.google.inject.AbstractModule
import schedulers.CustomerCountBatchScheduler
import akka.actor.ActorSystem


class CustomerCountBatchActorModule extends AbstractModule with AkkaGuiceSupport {
  override def configure() = {
    bindActor[CustomerCountBatchActor]("CustomerCountBatch-actor")
  }
}

class CustomerCountBatchScheduleModule extends Module {

  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
    Seq(
      bind[CustomerCountBatchScheduler].toSelf.eagerly
    )
  }
}