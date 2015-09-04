package org.miczilla.lcm.customer.listener;

import java.util.Queue;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
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
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class OrderApprovalListener
{
  private static Logger logger = LoggerFactory.getLogger(OrderApprovalListener.class);

  @Value("${customer}")
  private String customer;

  @Resource(name = "specialOffers")
  private Queue<SpecialOffer> specialOffers;

  @Autowired
  private JmsTemplate jmsTemplate;

  @JmsListener(destination = "orderApprovalQueue")
  public void onMessage(Message<String> message) throws Exception
  {
    final String payload = message.getPayload();
    logger.debug("Processing message: {}", payload);

    final OrderRequest orderRequest = JsonHelper.unmarshal(payload, OrderRequest.class);
    if (OrderRequest.Status.APPROVED.equals(orderRequest.getStatus()))
    {
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

      final javax.jms.Message shopResponse
        = jmsTemplate.sendAndReceive("orderPlacementQueue", new MessageCreator()
      {
        @Override
        public javax.jms.Message createMessage(final Session session) throws JMSException
        {
          return session.createTextMessage(JsonHelper.marshal(order));
        }
      });

      if (shopResponse != null && shopResponse instanceof TextMessage)
      {
        TextMessage response = (TextMessage) shopResponse;
        logger.info("Shop response: {}", response.getText());
      }
      else
      {
        logger.info("Not sure if the order could be delivered...");
      }
    }
    else
    {
      logger.info("Order request has been rejected.");
    }
  }
}
