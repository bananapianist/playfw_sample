package modules


import play.api.{Configuration, Environment}
import play.api.inject.{Binding, Module}
import play.api.libs.concurrent.AkkaGuiceSupport

import actors.MailsendActor
import com.google.inject.AbstractModule
import schedulers.MailsendScheduler


class MailsendActorModule extends AbstractModule with AkkaGuiceSupport {
  override def configure() = {
    bindActor[MailsendActor]("mailsend-actor")
  }
}

class MailsendScheduleModule extends Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
    Seq(
      bind[MailsendScheduler].toSelf.eagerly
    )
  }
}