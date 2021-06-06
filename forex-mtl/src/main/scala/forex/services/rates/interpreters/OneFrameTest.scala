package forex.services.rates.interpreters.currencyconverter

import cats.data.Writer
import cats.syntax.either._
import forex.domain.{Price, Rate, Timestamp}
import forex.services.rates.Algebra
import forex.services.rates.errors._

class OneFrameTest extends Algebra[Writer[Rate.Pair, *]] {

  override def get(pair: Rate.Pair): Writer[Rate.Pair, Either[ExchangeError, List[Rate]]] =
    Writer.tell(pair).map { _ =>
      List.empty[Rate].asRight[ExchangeError]
    }

}
