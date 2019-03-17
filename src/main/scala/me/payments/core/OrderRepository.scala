package me.payments.core

import me.payments.core.model.{Item, Order}

trait OrderRepository[F[_]] {

  def find(orderId: String): F[Option[Order]]

  def updateItems(orderId: String, items: Vector[Item]): F[Option[Order]]

  def createOrUpdate(order: Order): F[Order]

  def delete(orderId: String): F[Option[Order]]

}

object OrderRepository {

  def apply[F[_]: OrderRepository]: OrderRepository[F] = implicitly[OrderRepository[F]]

}
