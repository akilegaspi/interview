package forex.domain

import forex.services.database.errors.StoreError
import forex.services.DatabaseService

object Program {
  def getRateForPair[F[_]](currencyPair: Rate.Pair)(
    implicit dbService: DatabaseService[F]
  ): F[Either[StoreError, Rate]] = dbService.get(currencyPair)

}
