package org.miczilla.lcm.shop.listener;

import com.google.common.cache.Cache;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import org.miczilla.lcm.JsonHelper;
import org.miczilla.lcm.domain.OrderRequest;
import org.miczilla.lcm.domain.SpecialOffer;
import org.miczilla.lcm.shop.DataAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.listener.adapter.JmsResponse;
import org.springframework.jms.support.JmsHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class OrderRequestListener
{
  private static Logger logger = LoggerFactory.getLogger(OrderRequestListener.class);

  @Autowired
  private Cache<String, SpecialOffer> specialOffers;

  @Autowired
  private JmsTemplate jmsTemplate;

  @Autowired
  private DataAccess dataAccess;

  @JmsListener(destination = "orderRequestQueue")
  public JmsResponse<Message<String>> onMessage(Message<String> message)
  {
    final String payload = message.getPayload();
    final MessageHeaders headers = message.getHeaders();

    logger.debug("Processing message: {}", payload);
    logger.debug("Headers: {}", headers);

    final TemporaryQueue replyTo = headers.get(JmsHeaders.REPLY_TO, TemporaryQueue.class);
    final String correlationId = headers.get(JmsHeaders.CORRELATION_ID, String.class);
    final OrderRequest orderRequest = JsonHelper.unmarshal(payload, OrderRequest.class);
    final SpecialOffer offer = specialOffers.getIfPresent(correlationId);
    if (offer == null)
    {
      // Prepare and reject response
      orderRequest.setType(OrderRequest.Type.APPROVAL);
      orderRequest.setStatus(OrderRequest.Status.REJECTED);
      final Message<String> response = MessageBuilder
        .withPayload(JsonHelper.marshal(orderRequest))
        .setHeader(JmsHeaders.CORRELATION_ID, correlationId)
        .build();
      return JmsResponse.forDestination(response, replyTo);
    }

//    Set<OrderEntry> order = new LinkedHashSet<>();
//    for (Map.Entry<String, Integer> entry : payload.entrySet())
//    {
//      String id = entry.getKey();
//      Integer amount = entry.getValue();
//      Product product = dataAccess.lookupProductById(id);
//      if (product != null)
//      {
//        order.add(new OrderEntry(product, amount));
//      }
//    }
//
//    boolean approveOrder = dataAccess.checkAvailability(order);
//    if (approveOrder)
//    {
//      // TODO: Send approval message
//    }
//    else
//    {
//      // TODO: Reject order
//    }

    return null;
  }

  private void approveOrderRequest()
  {

  }

  private void rejectOrderRequest(final MessageHeaders headers)
  {
    String replyToDestination = headers.get(JmsHeaders.REPLY_TO, String.class);
    jmsTemplate.send(replyToDestination, new MessageCreator()
    {
      @Override
      public javax.jms.Message createMessage(final Session session) throws JMSException
      {
        return null;
      }
    });
  }
}

//    return MessageBuilder
//      .withPayload("foo")
//      .setHeader("code", 1234)
//      .build();
// ;
// @SendTo("org.miczilla.lcm.shop.INVALID_ORDER_REQUEST")
