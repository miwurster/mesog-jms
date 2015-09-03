package org.miczilla.lcm.shop.listener;

import com.google.common.cache.Cache;
import javax.jms.JMSException;
import javax.jms.Queue;
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
    logger.info("Received order request...");

    final String payload = message.getPayload();
    final MessageHeaders headers = message.getHeaders();

    logger.debug("Processing message: {}", payload);
    logger.debug("Headers: {}", headers);

    final Queue replyTo = headers.get(JmsHeaders.REPLY_TO, Queue.class);
    final String correlationId = headers.get(JmsHeaders.CORRELATION_ID, String.class);
    final OrderRequest orderRequest = JsonHelper.unmarshal(payload, OrderRequest.class);
    final SpecialOffer offer = specialOffers.getIfPresent(correlationId);
    if (offer == null)
    {
      logger.info("Order request is invalid, probably because order has been expired.");
      return rejectOrderRequest(replyTo, correlationId, orderRequest);
    }

    boolean approveOrder = dataAccess.checkAvailability(orderRequest);
    if (approveOrder)
    {
      logger.info("Approving order.");
      return approveOrderRequest(replyTo, correlationId, orderRequest);
    }

    logger.info("Rejecting order request, products may not be available as requested.");
    return rejectOrderRequest(replyTo, correlationId, orderRequest);
  }

  private JmsResponse<Message<String>> approveOrderRequest(final Queue replyTo, final String correlationId, final OrderRequest orderRequest) {
    orderRequest.setType(OrderRequest.Type.APPROVAL);
    orderRequest.setStatus(OrderRequest.Status.APPROVED);
    return createReplyMessage(replyTo, correlationId, orderRequest);
  }

  private JmsResponse<Message<String>> rejectOrderRequest(final Queue replyTo, final String correlationId, final OrderRequest orderRequest) {
    orderRequest.setType(OrderRequest.Type.APPROVAL);
    orderRequest.setStatus(OrderRequest.Status.REJECTED);
    return createReplyMessage(replyTo, correlationId, orderRequest);
  }

  private JmsResponse<Message<String>> createReplyMessage(final Queue replyTo, final String correlationId, final OrderRequest orderRequest) {
    final Message<String> response = MessageBuilder
        .withPayload(JsonHelper.marshal(orderRequest))
        .setHeader(JmsHeaders.CORRELATION_ID, correlationId)
        .build();
    return JmsResponse.forDestination(response, replyTo);
  }
}
