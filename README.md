## Messaging‐based Special Offer Gadget (MeSOG) Shop

> Lab exercise of [Loose Coupling & Messaging](http://www.iaas.uni-stuttgart.de/lehre/vorlesung) lecture.

This is a very simple, command-line based shop implementation. We are using **Apache ActiveMQ** and **JMS** for messaging and **Spring** and **Spring Boot** as our application framework.

#### Requirements:

* Customers can subscribe to multiple product categories offered by the Shop.
* The Shop publishes special offers (which are only valid for a fixed amount of time, e.g. 1 hour) to subscribed Customers.
* Each offer contains product(s) that are bound to at least one product category, so only those Customers are notified, which are subscribed to one of the corresponding product categories.
* Each product in an offer is described by all the product properties listed previously.
* Customers can then request to buy product(s) that are offered by responding to the Shop with a list of product IDs, plus the amount of items per product ID.
* The Shop checks its internal Inventory for availability of the requested products and the amount of items to either approve or reject a Customer's request.
* Once confirmed by the Shop, a Customer can buy the product(s) by providing the following information: product IDs, number of items per product ID, shipping address, and payment details.

## Usage

```
mvn clean install
docker-compose up
java -jar shop\target\shop-1.0-SNAPSHOT.jar
java -jar customer\target\customer-1.0-SNAPSHOT.jar --customer=foo --category=Flashlights
java -jar customer\target\customer-1.0-SNAPSHOT.jar --customer=bar --category=HDD
```

## License

[MIT](http://opensource.org/licenses/MIT) © [Michael Wurster](http://miwurster.com)
