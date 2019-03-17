package me.payments.core.interpreters

import me.payments.core.PaymentNetworkClient
import me.payments.core.Problem.ProblemOr
import me.payments.core.model.EligibilityResult.Eligible
import me.payments.core.model.PaymentResult.Accepted
import me.payments.core.model.{ CardData, EligibilityResult, PaymentResult }

class MockPaymentSystemClient() extends PaymentNetworkClient[ProblemOr] {

  override def checkEligibility(cardData: CardData): ProblemOr[EligibilityResult] = Right(Eligible)

  override def processPayment(amount: Double, description: String, cardData: CardData): ProblemOr[PaymentResult] =
    Right(Accepted)

}
