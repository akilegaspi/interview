package forex.services.database.interpreters

import forex.AppM
import forex.domain.Rate
import forex.services.database.Algebra
import forex.services.database.errors.DatabaseError
import dev.profunktor.redis4cats.Redis

class RedisCache extends Algebra[AppM]{
  override def get(key: Rate.Pair): AppM[DatabaseError Either Rate] = ???

  override def put(key: Rate.Pair, value: Rate): AppM[DatabaseError Either Unit] = ???
}