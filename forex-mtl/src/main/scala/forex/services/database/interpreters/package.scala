package forex.services.database

import cats.effect.Sync
import cats.effect.concurrent.Ref
import forex.RateStore
import forex.domain.Currency

package object interpreters {
  type TestM[A] = forex.TestM[List[Currency], A]

  def createStore[F[_]: Sync](): F[Ref[F, RateStore]] =
    Ref.of(Map.empty)
}
