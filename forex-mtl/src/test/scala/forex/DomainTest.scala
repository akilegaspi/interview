package forex

import forex.services.rates.interpreters.OneFrameReaderDummy
import org.scalatest.flatspec._
import org.scalatest.matchers.should
import forex.services.RatesService
import forex.services.rates.interpreters.OneFrameReaderDummy.ForexReader
import forex.domain.{Currency, Rate}
import forex.programs.rates.Protocol.GetRatesRequest
import forex.programs.rates.errors.Error.RateNotFound

class DomainTest extends AnyFlatSpec with should.Matchers {
  import Currency._

  val USDAUDRATE = BigDecimal(3)

  val testMap = Map(Rate.Pair(USD, AUD) -> USDAUDRATE)

  val dummyRatesService: RatesService[ForexReader] = new OneFrameReaderDummy

  val forexProgram = forex.programs.rates.Program(dummyRatesService)


  "A request" should "return a rate if it exists" in {
    val testPairRequest = GetRatesRequest(USD, AUD)
    forexProgram.get(testPairRequest).run(testMap).map(_.price.value) should be(Right(USDAUDRATE))
  }

  "A request" should "return an error if it doesn't exist" in {
    val testPairRequest = GetRatesRequest(USD, USD)
    forexProgram.get(testPairRequest).run(testMap) should be(Left(RateNotFound))
  }

}
