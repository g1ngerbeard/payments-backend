package me.payments.core

import cats.implicits._
import me.payments.core.Problem.ProblemOr
import me.payments.core.interpreters.{DefaultPaymentsService, InmemOrderRepository}
import me.payments.core.model.{CardData, Item, PaymentSystem}

object MainApp extends App {

  val orderRepo       = new InmemOrderRepository
  val paymentsService = new DefaultPaymentsService

  val orderService = new OrderService[ProblemOr](orderRepo, paymentsService)

  val items = Vector(Item("1", "monitor", 500), Item("2", "laptop", 1000), Item("3", "speakers", 200))

  val cardData = CardData("00110011", "John Mockman", Some("123"), PaymentSystem.Visa)

  val res = for {
    order    <- orderService.create(items)
    newOrder <- orderService.submitPayment(order.id, cardData)
  } yield newOrder

  res.foreach(println)

}
