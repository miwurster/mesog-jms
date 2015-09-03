package org.miczilla.lcm.shop.listener;

import org.miczilla.lcm.JsonHelper;
import org.miczilla.lcm.domain.CategorySubscription;
import org.miczilla.lcm.shop.DataAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class CategorySubscriptionListener
{
  private static Logger logger = LoggerFactory.getLogger(CategorySubscriptionListener.class);

  @Autowired
  private DataAccess dataAccess;

  @JmsListener(destination = "categorySubscriptionQueue")
  public void onMessage(Message<String> message)
  {
    String messagePayload = message.getPayload();
    logger.debug("Processing message: {}", message.getPayload());

    CategorySubscription subscription
      = JsonHelper.unmarshal(messagePayload, CategorySubscription.class);
    dataAccess.createOrUpdate(subscription);
  }
}
