package services

import securesocial.core.providers.UsernamePasswordProvider
import securesocial.core.{ BasicProfile, RuntimeEnvironment }
import securesocial.core.services.UserService
import scala.collection.immutable.ListMap
import controllers._

  class MyEnvironment extends RuntimeEnvironment.Default {
    type U = DemoUser
    override implicit val executionContext = play.api.libs.concurrent.Execution.defaultContext
//    override lazy val routes = new CustomRoutesService()
    override lazy val userService: InMemoryUserService = new InMemoryUserService()
    override lazy val eventListeners = List(new MyEventListener())
  }
