package forex.domain

case class Price(value: BigDecimal) extends AnyVal

object Price {
  def apply(value: Integer): Price =
    Price(BigDecimal(value))

  def apply(value: Float): Price =
    Price(BigDecimal.decimal(value))

  val test: BigDecimal = BigDecimal(9001)

  val testPrice: Price =
    Price(test)
}
