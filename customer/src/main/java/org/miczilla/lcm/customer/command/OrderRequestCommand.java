package org.miczilla.lcm.customer.command;

import java.util.Queue;
import java.util.Set;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.miczilla.lcm.ConsoleCommand;
import org.miczilla.lcm.ConsoleHelper;
import org.miczilla.lcm.JsonHelper;
import org.miczilla.lcm.domain.OrderRequest;
import org.miczilla.lcm.domain.SpecialOffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component
public class OrderRequestCommand implements ConsoleCommand
{
  private static Logger logger = LoggerFactory.getLogger(OrderRequestCommand.class);

  @Resource(name = "specialOffers")
  private Queue<SpecialOffer> specialOffers;

  @Autowired
  private JmsTemplate jmsTemplate;

  @Autowired
  private ActiveMQQueue orderApprovalQueue;

  @Override
  public void execute(final Set<String> args)
  {
    final SpecialOffer offer = specialOffers.peek();
    if (offer == null)
    {
      ConsoleHelper.printlnErr("There is no offer available");
      return;
    }
    logger.info("Sending order request for products <{}>; offer <{}>", args, offer.getId());
    jmsTemplate.send("orderRequestQueue", new MessageCreator()
    {
      @Override
      public Message createMessage(final Session session) throws JMSException
      {
        // Prepare order request
        OrderRequest orderRequest = new OrderRequest();
        for (String entry : args)
        {
          final String[] order = entry.split("=");
          orderRequest.addOrderEntry(order[0], Integer.parseInt(order[1]));
        }
        // Create and send message
        final TextMessage message = session.createTextMessage();
        message.setText(JsonHelper.marshal(orderRequest));
        message.setJMSCorrelationID(offer.getId());
        message.setJMSReplyTo(orderApprovalQueue);
        return message;
      }
    });
  }
}
