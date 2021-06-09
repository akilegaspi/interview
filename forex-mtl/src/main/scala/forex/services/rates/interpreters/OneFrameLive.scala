package forex.services.rates.interpreters

import cats.effect.Sync
import forex.domain.{Currency, Price, Rate, Timestamp}
import forex.services.rates.Algebra
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import org.http4s.{EntityDecoder, Header, Headers, Query, Request, Uri}
import org.http4s.circe._
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.client.Client
import cats.implicits._
import forex.config.OneFrameConfig
import forex.services.rates.errors.ExchangeError
import org.http4s.Uri.RegName

import java.time.OffsetDateTime
import scala.util.Try

class OneFrameLive[F[_]: Sync](config: OneFrameConfig, httpClient: Client[F]) extends Algebra[F] {
  import OneFrameLive._

  private val tokenHeader: Header =
    Header("token", config.apiKey)

  private val pairsQuery: Query = Query((for {
    fromCurrency <- Currency.all
    toCurrency <- Currency.allBut(fromCurrency)
    currencyPairStr = fromCurrency.show + toCurrency.show
  } yield ("pair", currencyPairStr.some)): _*)

  private val oneFrameRatesUri: Uri = {
    Uri(
      path = "/rates",
      authority = Uri.Authority(host = RegName(config.host), port = config.port.some).some,
      query = pairsQuery
    )
  }

  private val request: Request[F] = Request[F](
    uri = oneFrameRatesUri,
    headers = Headers.of(tokenHeader)
  )

  override def getRates(): F[ExchangeError Either List[Rate]] =
    getOneFrameRates.map { rates =>
      rates.traverse { oneFrameRate =>
        val ratePair = Rate.Pair(oneFrameRate.from, oneFrameRate.to)
        val price = Price(oneFrameRate.price)
        Rate(ratePair, price, oneFrameRate.time_stamp).asRight[ExchangeError]
      }
    }


  private val getOneFrameRates: F[List[OneFrameRate]] =
    httpClient.expect(request)(implicitly[EntityDecoder[F, List[OneFrameRate]]])

}

object OneFrameLive {

  def apply[F[_]: Sync](config: OneFrameConfig, httpClient: Client[F]): Algebra[F] =
    new OneFrameLive(config, httpClient)

  final case class OneFrameRate(from: Currency,
                                to: Currency,
                                bid: BigDecimal,
                                ask: BigDecimal,
                                price: BigDecimal,
                                time_stamp: Timestamp)

  implicit val currencyDecoder: Decoder[Currency] = Decoder.decodeString.emapTry { currencyStr =>
    Try(Currency.fromString(currencyStr))
  }

  implicit val timestampDecoder: Decoder[Timestamp] = Decoder.decodeString.emapTry { timestampStr =>
    Try(Timestamp(OffsetDateTime.parse(timestampStr)))
  }

  implicit val rateDecoder: Decoder[OneFrameRate] =
    deriveDecoder[OneFrameRate]

  implicit def rateEntityDecoder[F[_]: Sync](): EntityDecoder[F, List[OneFrameRate]] =
    jsonOf[F, List[OneFrameRate]]

}