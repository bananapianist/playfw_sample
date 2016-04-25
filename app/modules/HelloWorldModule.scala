package modules


import play.api.{Configuration, Environment}
import play.api.inject.{Binding, Module}
import play.api.libs.concurrent.AkkaGuiceSupport

import actors.HelloWorldActor
import com.google.inject.AbstractModule
import schedulers.HelloWorldScheduler


class HelloWorldActorModule extends AbstractModule with AkkaGuiceSupport {
  override def configure() = {
    bindActor[HelloWorldActor]("hello-world-actor")
  }
}

class HelloWorldScheduleModule extends Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
    Seq(
      bind[HelloWorldScheduler].toSelf.eagerly
    )
  }
}