import cats.data.{Kleisli, Writer}
import cats.effect.IO
import cats.effect.concurrent.Ref
import forex.domain.Rate

package object forex {

  // The model of the store where we store our forex rates
  type RateStore = Map[Rate.Pair, Rate]

  // Monad for live application
  type AppM[F[_], A] = Kleisli[F, Ref[F, RateStore], A]

  type App[A] = AppM[IO, A]

  // Monad for testing domain logic
  type TestM[L, A] = Writer[L, A]
}
