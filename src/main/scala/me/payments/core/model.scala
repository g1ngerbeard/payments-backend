package me.payments.core

object model {

  case class Item(id: String, name: String, price: Double)

  case class CardData(cardNumber: String, cardholder: String, cvv: Option[String], paymentSystem: PaymentSystem)

  sealed trait PaymentSystem

  object PaymentSystem {
    case object Visa            extends PaymentSystem
    case object MasterCard      extends PaymentSystem
    case object AmericanExpress extends PaymentSystem
  }

  sealed trait PaymentResult

  object PaymentResult {
    case object Accepted extends PaymentResult
    case class Rejected(reason: String) extends PaymentResult
  }

  sealed trait OrderStatus

  object OrderStatus {
    case object New                                           extends OrderStatus
    case class PaymentSubmitted(paymentStatus: PaymentResult) extends OrderStatus
  }

  case class Order(id: String, version: Int, items: Vector[Item], orderStatus: OrderStatus) {

    def updated(items: Vector[Item] = items, orderStatus: OrderStatus = orderStatus): Order =
      copy(items = items, orderStatus = orderStatus, version = version + 1)

    lazy val totalPrice: Double = items.map(_.price).sum

  }

}
