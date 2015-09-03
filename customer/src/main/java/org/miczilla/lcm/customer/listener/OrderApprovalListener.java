package org.miczilla.lcm.customer.listener;

import org.miczilla.lcm.JsonHelper;
import org.miczilla.lcm.domain.Order;
import org.miczilla.lcm.domain.OrderRequest;
import org.miczilla.lcm.domain.SpecialOffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.listener.adapter.JmsResponse;
import org.springframework.jms.support.JmsHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Session;
import java.util.Queue;

@Component
public class OrderApprovalListener {

  private static Logger logger = LoggerFactory.getLogger(OrderApprovalListener.class);

  @Value("${customer}")
  private String customer;

  @Resource(name = "specialOffers")
  private Queue<SpecialOffer> specialOffers;

  @Autowired
  private JmsTemplate jmsTemplate;

  @JmsListener(destination = "orderApprovalQueue")
  public void onMessage(Message<String> message) {
    final String payload = message.getPayload();
    logger.debug("Processing message: {}", payload);

    final OrderRequest orderRequest = JsonHelper.unmarshal(payload, OrderRequest.class);
    if (OrderRequest.Status.APPROVED.equals(orderRequest.getStatus())) {
      logger.info("Order request has been approved.");

      final Order.PaymentDetails paymentDetails = new Order.PaymentDetails();
      paymentDetails.setName("Customer " + customer);
      paymentDetails.setIban("ABCDEFGHIJ");
      paymentDetails.setBic("KLMNOPKQRST");

      final Order.ShippingAddress shippingAddress = new Order.ShippingAddress();
      shippingAddress.setName("Customer " + customer);
      shippingAddress.setStreet("ABC Str. 50");
      shippingAddress.setZipCode("12345");
      shippingAddress.setCity("Boeblingen");

      final Order order = new Order();
      order.setOrderEntries(orderRequest.getOrderEntries());
      order.setPaymentDetails(paymentDetails);
      order.setShippingAddress(shippingAddress);

      specialOffers.poll();
      logger.info("Placing order: {}", order);

      final Message<String> response = MessageBuilder
          .withPayload(JsonHelper.marshal(order)).build();
      jmsTemplate.convertAndSend("orderPlacementQueue", response);
    } else {
      logger.info("Order request has been rejected.");
    }
  }
}
