package forex.services.database

object errors {
  sealed trait StoreError
  object StoreError {
    final case object NotFoundError extends StoreError
  }

  def fromStoreError(error: StoreError): String = error match {
    case StoreError.NotFoundError => "Value for Key in Store not found"
  }
}
