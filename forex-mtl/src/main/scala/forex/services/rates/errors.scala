package forex.services.rates

object errors {

  sealed trait ExchangeError
  object ExchangeError {
    final case class LookupError(msg: String) extends ExchangeError
    final case class NetworkError(msg: String) extends ExchangeError
    final case object RequestLimitError extends ExchangeError
  }

  def fromExchangeError(error: ExchangeError): String = error match {
    case ExchangeError.LookupError(msg) => s"Lookup Error encountered: $msg"
    case ExchangeError.NetworkError(msg) => s"Network Error encountered: $msg"
    case ExchangeError.RequestLimitError => s"Forex rate request limit reached"
  }

}
