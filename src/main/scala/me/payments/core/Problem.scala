package me.payments.core

import me.payments.core.model.PaymentSystem

trait Problem extends Throwable {

  val message: String

}

object Problem {

  type ProblemOr[T] = Either[Problem, T]

  case class StorageFailure(message: String) extends Problem

  case class PaymentFailure(reason: String, paymentSystem: PaymentSystem) extends Problem {
    override val message: String = s"Request to $paymentSystem failed due to: $reason"
  }

  case class LogicalError(message: String) extends Problem

  case class OrderNotFound(id: String) extends Problem {
    override val message: String = s"Order #$id doesn't exist"
  }

}
