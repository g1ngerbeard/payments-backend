package me.payments.core

import me.payments.core.model.OrderStatus.New
import me.payments.core.model.{ CardData, Item, Order, PaymentSystem }

object TestData {

  val TestItems: Vector[Item] = Vector(Item("1", "monitor", 500), Item("2", "laptop", 1000), Item("3", "speakers", 200))

  val TestOrder = Order("123", 0, TestItems, New)

  val TestCardData = CardData("00110011", "John Mockman", Some("123"), PaymentSystem.Visa)

}
