package forex.services.rates.interpreters

import forex.services.rates.Algebra
import cats.Applicative
import cats.data.Reader
import cats.syntax.applicative._
import cats.syntax.either._
import forex.domain.{Price, Rate, Timestamp}
import forex.services.rates.errors.Error.PairNotFound
import forex.services.rates.errors._
import forex.services.rates.interpreters.OneFrameReaderDummy.ForexReader

class OneFrameDummy[F[_]: Applicative] extends Algebra[F] {

  override def get(pair: Rate.Pair): F[Error Either Rate] =
    Rate(pair, Price(BigDecimal(100)), Timestamp.now).asRight[Error].pure[F]

}


object OneFrameReaderDummy {
  type ForexMap = Map[Rate.Pair, BigDecimal]
  type ForexReader[A] = Reader[ForexMap, A]
}

class OneFrameReaderDummy extends Algebra[ForexReader] {
  override def get(pair: Rate.Pair): ForexReader[Either[Error, Rate]] = Reader { forexMap =>
    forexMap.get(pair) match {
      case Some(rate) => Rate(pair, Price(rate), Timestamp.now).asRight
      case _ => PairNotFound.asLeft
    }
  }
}
