package me.payments.core

import java.util.UUID

import cats.Monad
import cats.implicits._
import me.payments.core.model.OrderStatus.PaymentSubmitted
import me.payments.core.model.{ CardData, Item, Order, OrderStatus }

/**
  * Service for placing, updating and submitting of orders. This is the entry point to the payment core
  */
trait OrderService[F[_]] {

  def get(orderId: String): F[Option[Order]]

  def create(items: Vector[Item]): F[Order]

  def updateItems(orderId: String, items: Vector[Item]): F[Option[Order]]

  def submitPayment(orderId: String, cardData: CardData): F[Option[Order]]

}

class DefaultOrderService[F[_]: Monad](repo: OrderRepository[F], paymentProcessor: PaymentsProcessor[F])
    extends OrderService[F] {

  def get(orderId: String): F[Option[Order]] = repo.find(orderId)

  def create(items: Vector[Item]): F[Order] = {
    val id       = UUID.randomUUID.toString
    val newOrder = Order(id, 0, items, OrderStatus.New)

    repo.createOrUpdate(newOrder)
  }

  def updateItems(orderId: String, items: Vector[Item]): F[Option[Order]] = repo.updateItems(orderId, items)

  def submitPayment(orderId: String, cardData: CardData): F[Option[Order]] =
    for {
      orderOpt     <- get(orderId)
      updatedOrder <- orderOpt.traverse(processPayment(cardData))
    } yield updatedOrder

  private def processPayment(cardData: CardData)(order: Order): F[Order] =
    for {
      status       <- paymentProcessor.submitPayment(order, cardData)
      updatedOrder <- repo.createOrUpdate(order.updated(orderStatus = PaymentSubmitted(status)))
    } yield updatedOrder

}
