package forex.services.database.interpreters

import cats.Functor
import cats.implicits._
import forex.RateStore
import forex.domain.Rate
import forex.services.database.Algebra
import forex.services.database.errors.StoreError
import cats.effect.concurrent.Ref
import forex.services.database.errors.StoreError.NotFoundError

class MemoryCache[F[_]: Functor](storeRef: Ref[F, RateStore]) extends Algebra[F] {
  override def get(key: Rate.Pair): F[StoreError Either Rate] =
    storeRef.get.map { rateStore =>
      rateStore.get(key) match {
        case Some(rate) => Right(rate)
        case _          => Left(NotFoundError)
      }
    }

  override def put(key: Rate.Pair, value: Rate): F[StoreError Either Unit] =
    storeRef.update(_.updated(key, value)).map(_.asRight)
}

object MemoryCache {
  def apply[F[_]: Functor](storeRef: Ref[F, RateStore]): MemoryCache[F] =
    new MemoryCache[F](storeRef)
}