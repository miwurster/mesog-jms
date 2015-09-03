package org.miczilla.lcm.customer;

import java.util.List;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;
import org.miczilla.lcm.JsonHelper;
import org.miczilla.lcm.domain.CategorySubscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component
@Order(Integer.MIN_VALUE)
public class CategorySubscriptionHandler implements CommandLineRunner
{
  private static Logger logger = LoggerFactory.getLogger(CategorySubscriptionHandler.class);

  @Autowired
  private Queue categorySubscriptionQueue;

  @Autowired
  private JmsTemplate jmsTemplate;

  @Value("${customer}")
  private String customer;

  @Value("${category}")
  private String[] categorySubscriptions;

  @Override
  public void run(final String... strings) throws Exception
  {
    logger.info("Creating category subscriptions for customer <{}> with categories <{}>",
                customer, categorySubscriptions);
    jmsTemplate.send(categorySubscriptionQueue, new MessageCreator()
    {
      @Override
      public Message createMessage(final Session session) throws JMSException
      {
        CategorySubscription subscription
          = new CategorySubscription(customer, categorySubscriptions);
        String messageBody = JsonHelper.marshal(subscription);
        logger.info("Sending simple text message: {}", messageBody);
        return session.createTextMessage(messageBody);
      }
    });
  }
}
