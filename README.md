# payments-backend

This is a module, which allows creating of purchase orders and submitting payments. 
Potentially it might be used in the backend of some marketplace or online shop.

The entry point is the `OrderService` class, orders are stored in-memory, all calls to payment systems are mocked

In order to achieve better readability and scalability tagless final pattern was used.
This abstracts away and enables potential usage of different effects like Future or IO 
for asynchronous and non-blocking processing of orders.

To run small test program execute the following command:

```bash
sbt run
```

Run tests:
```bash
sbt test
```

