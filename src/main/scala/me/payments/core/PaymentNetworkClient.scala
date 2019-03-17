package me.payments.core

import me.payments.core.model.{ CardData, EligibilityResult, PaymentResult }

trait PaymentNetworkClient[F[_]] {

  def checkEligibility(cardData: CardData): F[EligibilityResult]

  def processPayment(amount: Double, description: String, cardData: CardData): F[PaymentResult]

}
