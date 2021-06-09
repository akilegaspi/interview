package forex.services.database

import cats.Functor
import cats.effect.concurrent.Ref
import forex.RateStore
import forex.services.database.interpreters.{MemoryCache, TestCache, TestM}

object Interpreters {
  val test: Algebra[TestM] =
    new TestCache()

  def live[F[_]: Functor](storeRef: Ref[F, RateStore]): Algebra[F] =
    MemoryCache(storeRef)
}
