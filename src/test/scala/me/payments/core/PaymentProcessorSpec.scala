package me.payments.core

import cats.Id
import me.payments.core.TestData.{ TestCardData, TestOrder }
import me.payments.core.model.EligibilityResult.{ Eligible, NonEligible }
import me.payments.core.model.PaymentResult.{ Accepted, Rejected }
import me.payments.core.model.{ CardData, EligibilityResult, PaymentResult }
import org.scalatest.{ Inside, Matchers, WordSpec }

class PaymentProcessorSpec extends WordSpec with Matchers with Inside {

  class MockClient extends PaymentNetworkClient[Id] {

    def checkEligibility(cardData: CardData): Id[EligibilityResult] = Eligible

    def processPayment(amount: Double, description: String, cardData: CardData): Id[PaymentResult] =
      Accepted

  }

  trait Fixture {

    lazy val visaClient: PaymentNetworkClient[Id]            = new MockClient()
    lazy val masterCardClient: PaymentNetworkClient[Id]      = new MockClient()
    lazy val americanExpressClient: PaymentNetworkClient[Id] = new MockClient()

    lazy val paymentProcessor = new DefaultPaymentsProcessor[Id](
      visaClient,
      masterCardClient,
      americanExpressClient
    )

  }

  "PaymentProcessor" should {
    "route payment to corresponding payment system" in new Fixture {

      var invoked = false

      override lazy val visaClient: PaymentNetworkClient[Id] = new MockClient {
        override def checkEligibility(cardData: CardData): Id[EligibilityResult] = {
          invoked = true
          Eligible
        }
      }

      paymentProcessor.submitPayment(TestOrder, TestCardData) shouldBe Accepted
      invoked shouldBe true
    }

    "reject non eligible cards" in new Fixture {

      override lazy val visaClient: PaymentNetworkClient[Id] = new MockClient {
        override def checkEligibility(cardData: CardData): Id[EligibilityResult] = NonEligible("test")
      }

      inside(paymentProcessor.submitPayment(TestOrder, TestCardData)) {
        case Rejected(reason) =>
          reason contains "test"
      }

    }

    "propagate payment rejection to response" in new Fixture {
      val result = Rejected("test")

      override lazy val visaClient: PaymentNetworkClient[Id] = new MockClient {
        override def processPayment(amount: Double, description: String, cardData: CardData): Id[PaymentResult] = result
      }

      paymentProcessor.submitPayment(TestOrder, TestCardData) shouldBe result
    }
  }

}
