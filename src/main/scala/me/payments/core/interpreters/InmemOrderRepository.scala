package me.payments.core.interpreters

import java.util.concurrent.ConcurrentHashMap

import me.payments.core.OrderRepository
import me.payments.core.Problem.{ProblemOr, StorageFailure}
import me.payments.core.model.{Item, Order}

class InmemOrderRepository(failOnCall: Boolean = false) extends OrderRepository[ProblemOr] {

  private val orders = new ConcurrentHashMap[String, Order]()

  override def find(orderId: String): ProblemOr[Option[Order]] = doCall {
    Option(orders.get(orderId))
  }

  override def createOrUpdate(order: Order): ProblemOr[Order] = doCall {
    orders.put(order.id, order)
    order
  }

  override def delete(orderId: String): ProblemOr[Option[Order]] = doCall {
    Option(orders.remove(orderId))
  }

  override def updateItems(orderId: String, items: Vector[Item]): ProblemOr[Option[Order]] =
    doCall {
      Option(orders.computeIfPresent(orderId, (_, order) => order.updated(items = items)))
    }

  private def doCall[T](call: => T): ProblemOr[T] =
    Either.cond(!failOnCall, call, StorageFailure("this is mocked failure"))

}
