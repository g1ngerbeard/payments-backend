package me.payments.core

import cats.implicits._
import me.payments.core.Problem.ProblemOr
import me.payments.core.interpreters.{ InmemOrderRepository, MockPaymentSystemClient }
import me.payments.core.model.{ CardData, Item, PaymentSystem }

trait AppModule {
  lazy val orderRepo = new InmemOrderRepository

  lazy val visaClient            = new MockPaymentSystemClient()
  lazy val masterCardClient      = new MockPaymentSystemClient()
  lazy val americanExpressClient = new MockPaymentSystemClient()

  lazy val paymentsService = new DefaultPaymentsProcessor(visaClient, masterCardClient, americanExpressClient)

  lazy val orderService = new DefaultOrderService[ProblemOr](orderRepo, paymentsService)

}

object MainApp extends App with AppModule {

  val items = Vector(Item("1", "monitor", 500), Item("2", "laptop", 1000), Item("3", "speakers", 200))

  println("Placing the order for items:")

  items.foreach { case Item(id, name, price) => println(s"id: $id, name: $name, price: $price") }

  val cardNumber    = "00110011"
  val cardholder    = "John Mockman"
  val cvv           = "123"
  val paymentSystem = PaymentSystem.Visa

  val cardData = CardData(cardNumber, cardholder, Some(cvv), paymentSystem)

  println("\nCard data:")
  println(s"card number: $cardNumber, cardholder: $cardholder, cvv: $cvv, payment system: $paymentSystem")

  val res = for {
    order    <- orderService.create(items)
    newOrder <- orderService.submitPayment(order.id, cardData)
  } yield newOrder

  println("\nRaw response:")
  println(res)

  println("\nOrder status:")
  res.foreach(order => order.map(_.orderStatus).foreach(println))

}
