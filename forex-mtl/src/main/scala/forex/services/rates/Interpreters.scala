package forex.services.rates

import cats.effect.Sync
import forex.config.OneFrameConfig
import interpreters.{OneFrameTest, _}
import org.http4s.client.Client

object Interpreters {
  val test: Algebra[TestM] =
    OneFrameTest()
  def live[F[_]: Sync](config: OneFrameConfig, httpClient: Client[F]): Algebra[F] =
    OneFrameLive[F](config, httpClient)
}
