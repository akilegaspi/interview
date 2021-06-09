package forex.programs.rates

import cats.Functor
import cats.data.EitherT
import errors._
import forex.domain._
import forex.services.DatabaseService

class Program[F[_]: Functor](databaseService: DatabaseService[F]) extends Algebra[F] {

  override def get(request: Protocol.GetRatesRequest): F[Error Either Rate] = {
    val ratePair = Rate.Pair(request.from, request.to)
    EitherT(databaseService.get(ratePair)).leftMap(toProgramError).value
  }

}

object Program {

  def apply[F[_]: Functor](implicit
      dbService: DatabaseService[F]
  ): Algebra[F] = new Program[F](dbService)

}
