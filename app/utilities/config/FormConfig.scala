package utilities.config

import scala.concurrent.duration._

object FormConfig {
  val FormCacheTime: Duration = 60.minutes
  val FormCacheKey = "formdata"

}