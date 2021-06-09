package forex

import cats.data.EitherT

import scala.concurrent.ExecutionContext
import cats.effect._
import cats.effect.concurrent.Ref
import forex.config._
import forex.services.{DatabaseService, DatabaseServices, RatesService, RatesServices}
import fs2.Stream
import org.http4s.server.blaze.BlazeServerBuilder
import cats.implicits._
import forex.services.database.errors.fromStoreError
import forex.services.database.interpreters.createStore
import forex.services.rates.errors.fromExchangeError
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = BlazeClientBuilder[IO](executionContext).resource.use { client =>
    for {
      storeRef <- createStore[IO]()
      applicationStream = for {
        config <- Config.stream[IO]("app")
        app = new Application[IO](config, client, storeRef)
        scheduledRetrievalTaskStream = app.scheduleRetrievalTask().foreverM
        _ <- app.stream(executionContext).mergeHaltL(scheduledRetrievalTaskStream)
      } yield ()
      _ <- applicationStream.compile.drain
    } yield ExitCode.Success
  }

}

class Application[F[_]: ConcurrentEffect: Timer](config: ApplicationConfig, httpClient: Client[F], storeRef: Ref[F, RateStore]) {

  implicit private val dbService: DatabaseService[F] = DatabaseServices.live(storeRef)
  private val ratesService: RatesService[F] = RatesServices.live(config.oneFrame, httpClient)

  def scheduleRetrievalTask(): Stream[F, Unit] =
    Stream.eval(retrieveRatesTask()) *> Stream.sleep(config.oneFrame.dataValidity)


  def stream(ec: ExecutionContext): Stream[F, Unit] = {
    val module = new Module[F](config)
    BlazeServerBuilder[F](ec)
      .bindHttp(config.http.port, config.http.host)
      .withHttpApp(module.httpApp)
      .serve.map(_ => ())
  }

  private def retrieveRatesTask(): F[Unit] = (for {
    rates <- EitherT(ratesService.getRates()).leftMap(err => new Exception(fromExchangeError(err)))
    _ <- rates.traverse { rate =>
      val ratePair = rate.pair
      EitherT(dbService.put(ratePair, rate)).leftMap(err => new Exception(fromStoreError(err)))
    }
  } yield ()).value.flatMap(Sync[F].fromEither)

}
