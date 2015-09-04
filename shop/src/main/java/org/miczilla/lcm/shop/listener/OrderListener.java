package org.miczilla.lcm.shop.listener;

import java.util.Set;
import org.miczilla.lcm.JsonHelper;
import org.miczilla.lcm.domain.Order;
import org.miczilla.lcm.domain.Product;
import org.miczilla.lcm.shop.DataAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class OrderListener
{
  private static Logger logger = LoggerFactory.getLogger(OrderListener.class);

  @Autowired
  private DataAccess dataAccess;

  @JmsListener(destination = "orderPlacementQueue")
  public Message<String> onMessage(Message<String> message)
  {
    logger.info("Received order from customer...");

    final String payload = message.getPayload();
    logger.debug("Processing message: {}", message.getPayload());

    final Order order = JsonHelper.unmarshal(payload, Order.class);
    Set<Product> products = dataAccess.buyProducts(order.getOrderEntries());

    logger.info("Products: {}", products);
    logger.info("Payment details: {}", order.getPaymentDetails());
    logger.info("Shipping address: {}", order.getShippingAddress());

    return MessageBuilder.withPayload("SUCCESS").build();
  }
}
