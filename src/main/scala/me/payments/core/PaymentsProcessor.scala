package me.payments.core

import cats.Monad
import me.payments.core.model.EligibilityResult.{ Eligible, NonEligible }
import me.payments.core.model.PaymentResult.Rejected
import me.payments.core.model.PaymentSystem.{ AmericanExpress, MasterCard, Visa }
import me.payments.core.model.{ CardData, Order, PaymentResult }

import cats.implicits._

trait PaymentsProcessor[F[_]] {

  def submitPayment(order: Order, cardData: CardData): F[PaymentResult]

}

class DefaultPaymentsProcessor[F[_]: Monad](
    visaClient: PaymentNetworkClient[F],
    mastercardClient: PaymentNetworkClient[F],
    americanExpressClient: PaymentNetworkClient[F]
) extends PaymentsProcessor[F] {

  override def submitPayment(order: model.Order, cardData: CardData): F[PaymentResult] = {
    val paymentNetwork = cardData.paymentSystem match {
      case Visa            => visaClient
      case MasterCard      => mastercardClient
      case AmericanExpress => americanExpressClient
    }

    for {
      eligibilityResult <- paymentNetwork.checkEligibility(cardData)
      paymentResult     <- eligibilityResult match {
        case Eligible            => paymentNetwork.processPayment(order.totalPrice, s"Order #${order.id}", cardData)
        case NonEligible(reason) => Rejected(s"Card is not eligible: $reason").pure[F]
      }
    } yield paymentResult
  }

}
