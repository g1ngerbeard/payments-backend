package me.payments.core.interpreters

import scala.util.Random

object MockUtil {

  def isSuccessfulCall(failRate: Double): Boolean = failRate == 0.0 || Random.nextDouble() > failRate

}
