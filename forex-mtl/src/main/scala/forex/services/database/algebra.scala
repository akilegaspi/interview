package forex.services.database

import forex.domain.Rate

trait Algebra[F[_]] {
  def put(key: Rate.Pair, value: Rate): F[errors.StoreError Either Unit]
  def get(key: Rate.Pair): F[errors.StoreError Either Rate]
}
