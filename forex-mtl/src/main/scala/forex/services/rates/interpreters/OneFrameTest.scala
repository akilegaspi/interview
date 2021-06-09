package forex.services.rates.interpreters

import cats.data.Writer
import cats.syntax.either._
import forex.domain.{Currency, Price, Rate, Timestamp}
import forex.services.rates.Algebra
import forex.services.rates.errors._

class OneFrameTest extends Algebra[TestM] {

  override def getRates(): TestM[ExchangeError Either List[Rate]] = {
    Writer.value(List.empty[Rate].asRight[ExchangeError])
  }

}

object OneFrameTest {

  def apply(): OneFrameTest =
    new OneFrameTest

  def createMockRate(from: Currency, to: Currency): Rate = {
    val ratePair = Rate.Pair(from, to)
    val ratePrice = Price.testPrice
    val timestamp = Timestamp.test
    Rate(ratePair, ratePrice, timestamp)
  }

  lazy val mockRates: Map[Currency, List[Rate]] =
    Currency.all.map { currency =>
      val otherCurrencies = Currency.allBut(currency)
      val rates = otherCurrencies.map { otherCurrency =>
        createMockRate(currency, otherCurrency)
      }
      currency -> rates
    }.toMap

}
