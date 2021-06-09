package forex.programs.rates

import forex.services.database.errors.StoreError

object errors {

  sealed trait Error extends Exception
  object Error {
    final case class RateLookupFailed(msg: String) extends Error
  }

  def toProgramError(error: StoreError): Error = error match {
    case StoreError.NotFoundError => Error.RateLookupFailed("Rate Pair not found")
  }
}
