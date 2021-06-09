package forex.services.database.interpreters

import cats.data.Writer
import cats.implicits.{catsSyntaxEitherId, toFunctorOps}
import forex.domain.{Price, Rate, Timestamp}
import forex.services.database.errors.StoreError
import forex.services.database.{Algebra, errors}


/*
* Test Class for the Forex Cache Algebra
* */
class TestCache extends Algebra[TestM] {
  import TestCache._

  override def put(key: Rate.Pair, value: Rate): TestM[errors.StoreError Either Unit] =
    Writer.tell(List(key.from, key.to)).map(_.asRight[StoreError])

  override def get(key: Rate.Pair): TestM[StoreError Either Rate] = {
    val testPrice = Price(mockPrice)
    val testTimestamp = Timestamp.test
    val testRate = Rate(key, testPrice, testTimestamp)
    Writer.tell(List(key.from, key.to)).as(testRate.asRight)
  }

}

object TestCache {
  // Mock static price to be used in testing the program
  val mockPrice: Integer = Integer.valueOf(9001)
}