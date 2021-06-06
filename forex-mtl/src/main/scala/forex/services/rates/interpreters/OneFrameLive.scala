package forex.services.rates.interpreters.currencyconverter

import forex.AppM
import forex.domain.Rate
import forex.services.rates.{Algebra, errors}

class OneFrameLive extends Algebra[AppM] {

  override def get(pair: Rate.Pair): AppM[Either[errors.ExchangeError, Rate]] = {

  }

}
