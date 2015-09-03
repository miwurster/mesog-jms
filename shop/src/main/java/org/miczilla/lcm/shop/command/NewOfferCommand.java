package org.miczilla.lcm.shop.command;

import com.google.common.cache.Cache;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import org.miczilla.lcm.ConsoleCommand;
import org.miczilla.lcm.ConsoleHelper;
import org.miczilla.lcm.JsonHelper;
import org.miczilla.lcm.domain.CategorySubscription;
import org.miczilla.lcm.domain.Product;
import org.miczilla.lcm.domain.SpecialOffer;
import org.miczilla.lcm.shop.DataAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component
public class NewOfferCommand implements ConsoleCommand
{
  private static Logger logger = LoggerFactory.getLogger(NewOfferCommand.class);

  @Autowired
  private DataAccess dataAccess;

  @Autowired
  private Cache<String, SpecialOffer> specialOffers;

  @Autowired
  private JmsTemplate jmsTemplate;

  @Override
  public void execute(final Set<String> args)
  {
    final SpecialOffer offer = new SpecialOffer();
    for (String id : args)
    {
      Product product = dataAccess.lookupProductById(id);
      if (product != null)
      {
        offer.setProducts(product);
      }
    }
    if (offer.getProducts().size() == 0)
    {
      ConsoleHelper.printlnErr("Empty offer, probably invalid product numbers.");
      return;
    }

    Set<String> categories = new LinkedHashSet<>();
    for (Product product : offer.getProducts())
    {
      categories.addAll(product.getCategory());
    }
    Set<CategorySubscription> subscriptions
      = dataAccess.lookupCategorySubscriptions(categories);
    if (subscriptions.size() == 0)
    {
      ConsoleHelper.printlnErr("No subscriptions from customers found for those products, do not send message.");
      return;
    }

    for (CategorySubscription subscription : subscriptions)
    {
      jmsTemplate.send(subscription.getReplyToQueueName(), new MessageCreator()
      {
        @Override
        public Message createMessage(final Session session) throws JMSException
        {
          return session.createTextMessage(JsonHelper.marshal(offer));
        }
      });
      ConsoleHelper.println("Message sent to queue <%s>", subscription.getReplyToQueueName());
    }

    specialOffers.put(offer.getId(), offer);
  }
}
