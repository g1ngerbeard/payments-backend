package me.payments.core

import cats.Id
import me.payments.core.TestData.{ TestCardData, TestItems, TestOrder }
import me.payments.core.model.OrderStatus.{ New, PaymentSubmitted }
import me.payments.core.model.PaymentResult.{ Accepted, Rejected }
import me.payments.core.model.{ CardData, Item, Order, PaymentSystem }
import org.scalatest.{ Inside, Matchers, WordSpec }

class OrderServiceSpec extends WordSpec with Matchers with Inside {

  trait Fixture {

    lazy val paymentsService: PaymentsProcessor[Id] = (_: Order, _: CardData) => Accepted

    lazy val orderRepository: OrderRepository[Id] = new OrderRepository[Id] {
      override def find(orderId: String): Id[Option[Order]] = Some(TestOrder)

      override def updateItems(orderId: String, items: Vector[model.Item]): Id[Option[Order]] =
        Some(TestOrder.copy(items = items))

      override def createOrUpdate(order: Order): Id[Order] = order

      override def delete(orderId: String): Id[Option[Order]] = None
    }

    lazy val orderService = new DefaultOrderService[Id](orderRepository, paymentsService)

  }

  "Order service" should {
    "create new order" in new Fixture {
      val order: Order = orderService.create(TestItems)

      order.items shouldBe TestItems
      order.orderStatus shouldBe New
    }

    "update order status on payment submission" in new Fixture {
      inside(orderService.submitPayment("test", TestCardData)) {
        case Some(order) =>
          order.items shouldBe TestItems
          order.orderStatus shouldBe PaymentSubmitted(Accepted)
      }
    }

    "propagate payment rejected to the order status" in new Fixture {
      val rejectedStatus = Rejected("test")

      override lazy val paymentsService: PaymentsProcessor[Id] = (_, _) => rejectedStatus

      inside(orderService.submitPayment("test", TestCardData)) {
        case Some(order) =>
          order.orderStatus shouldBe PaymentSubmitted(rejectedStatus)
      }
    }

  }
}
