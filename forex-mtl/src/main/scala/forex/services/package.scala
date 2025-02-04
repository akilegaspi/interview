package forex

package object services {
  type RatesService[F[_]] = rates.Algebra[F]
  type DatabaseService[F[_]] = database.Algebra[F]
  final val RatesServices = rates.Interpreters
  final val DatabaseServices = database.Interpreters
}
