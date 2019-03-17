package me.payments.core

import me.payments.core.model.{CardData, Order, PaymentResult}

trait PaymentsService[F[_]] {

  def submitPayment(order: Order, payment: CardData): F[PaymentResult]

}

object PaymentsService {

  def apply[F[_]: PaymentsService]: PaymentsService[F] = implicitly[PaymentsService[F]]

}
