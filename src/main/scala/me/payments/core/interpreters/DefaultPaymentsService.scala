package me.payments.core.interpreters

import me.payments.core.PaymentsService
import me.payments.core.Problem.{ PaymentFailure, ProblemOr }
import me.payments.core.interpreters.MockUtil.isSuccessfulCall
import me.payments.core.model.PaymentResult.Accepted
import me.payments.core.model.{ CardData, Order, PaymentResult }

class DefaultPaymentsService(failRate: Double = 0.0) extends PaymentsService[ProblemOr] {

  override def submitPayment(order: Order, payment: CardData): ProblemOr[PaymentResult] =
    Either.cond(
      isSuccessfulCall(failRate),
      Accepted,
      PaymentFailure("this is mocked failure", payment.paymentSystem)
    )

}
