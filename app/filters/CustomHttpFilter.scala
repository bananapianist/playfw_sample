package filters

import play.api.http.HttpFilters
import play.filters.csrf.CSRFFilter
import javax.inject.Inject

class CustomHttpFilter @Inject() (csrfFilter: CSRFFilter) extends HttpFilters {
  def filters = Seq(csrfFilter)
}