package me.payments.core

import cats.Id
import me.payments.core.OrderServiceSpec.{ TestCardData, TestItems, TestOrder }
import me.payments.core.model.OrderStatus.{ New, PaymentSubmitted }
import me.payments.core.model.PaymentResult.Accepted
import me.payments.core.model.{ CardData, Item, Order, PaymentSystem }
import org.scalatest.{ Inside, Matchers, WordSpec }

class OrderServiceSpec extends WordSpec with Matchers with Inside {

  trait Fixture {

    val paymentsService: PaymentsService[Id] = (_: Order, _: CardData) => Accepted

    val orderRepository: OrderRepository[Id] = new OrderRepository[Id] {
      override def find(orderId: String): Id[Option[Order]] = Some(TestOrder)

      override def updateItems(orderId: String, items: Vector[model.Item]): Id[Option[Order]] =
        Some(TestOrder.copy(items = items))

      override def createOrUpdate(order: Order): Id[Order] = order

      override def delete(orderId: String): Id[Option[Order]] = None
    }

    val orderService = new OrderService[Id](orderRepository, paymentsService)

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

  }
}

object OrderServiceSpec {

  val TestItems: Vector[Item] = Vector(Item("1", "monitor", 500), Item("2", "laptop", 1000), Item("3", "speakers", 200))

  val TestOrder = Order("123", 0, TestItems, New)

  val TestCardData = CardData("00110011", "John Mockman", Some("123"), PaymentSystem.Visa)

}
