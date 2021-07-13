package forex.services

import cats.effect.{IO, Sync}
import cats.effect.concurrent.Ref
import TopTenWinnings._
import cats.Monad
import cats.implicits._

object TopTenWinnings {
  type UserId = Int
  type Winnings = BigDecimal
  type TopWinnersList = List[TopWinning]
  case class TopWinning(userId: UserId, winnings: Winnings)
}

class TopTenWinnings[F[_]: Sync : Monad](winnerListRef: Ref[F, TopWinnersList]) {

  def insertNewWinner(userId: UserId, winnings: Winnings): F[Unit] = winnerListRef.update { winnerList =>
    val newTopWinning = TopWinning(userId, winnings)
    val winnersBefore = winnerList.filter { winning =>
      winning.winnings > winnings
    }
    val winnersAfter = winnerList.slice(winnersBefore.length, winnerList.length)
    winnersBefore ++ (newTopWinning +: winnersAfter)
  }

  def getTopTenWinners(): F[List[TopWinning]] =
    winnerListRef.get.map(_.slice(0, 9))

}