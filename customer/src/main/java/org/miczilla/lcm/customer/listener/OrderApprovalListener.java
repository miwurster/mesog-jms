package org.miczilla.lcm.customer.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class OrderApprovalListener
{
  private static Logger logger = LoggerFactory.getLogger(SpecialOfferListener.class);

  @JmsListener(destination = "orderApprovalQueue")
  public void onMessage(Message<String> message)
  {
    final String messagePayload = message.getPayload();
    logger.info("Processing message: {}", message.getPayload());
  }
}
