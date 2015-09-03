package org.miczilla.lcm.customer.listener;

import java.util.List;
import java.util.Queue;
import javax.annotation.Resource;
import org.miczilla.lcm.ConsoleHelper;
import org.miczilla.lcm.JsonHelper;
import org.miczilla.lcm.domain.SpecialOffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class SpecialOfferListener
{
  private static Logger logger = LoggerFactory.getLogger(SpecialOfferListener.class);

  @Value("${customer}")
  private String customer;

  @Value("${category}")
  private List<String> categorySubscriptions;

  @Resource(name = "specialOffers")
  private Queue<SpecialOffer> specialOffers;

  @JmsListener(destination = "specialOfferQueue")
  public void onMessage(Message<String> message)
  {
    final String messagePayload = message.getPayload();
    logger.debug("Processing message: {}", message.getPayload());

    final SpecialOffer offer
      = JsonHelper.unmarshal(messagePayload, SpecialOffer.class);
    specialOffers.offer(offer);

    ConsoleHelper.println();
    ConsoleHelper.println("SPECIAL OFFER:\n%s", offer);
    ConsoleHelper.print("> ");
  }
}
