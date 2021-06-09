package forex.services.rates

import forex.domain.Rate

package object interpreters {
  type TestM[A] = forex.TestM[List[Rate], A]
}
