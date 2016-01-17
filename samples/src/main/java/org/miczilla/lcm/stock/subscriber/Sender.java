package org.miczilla.lcm.stock.subscriber;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.miczilla.lcm.JsonHelper;
import org.miczilla.lcm.stock.StockAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component
public class Sender
{
  @Autowired
  private JmsTemplate m_jmsTemplate;

  public void buyStock(final StockAction action)
  {
    final MessageCreator message = createMessage(action);
    m_jmsTemplate.send("BuyOrders", message);
  }

  public void sellStock(final StockAction action)
  {
    final MessageCreator message = createMessage(action);
    m_jmsTemplate.send("SellOrders", message);
  }

  private MessageCreator createMessage(final StockAction action)
  {
    return new MessageCreator()
    {
      @Override
      public Message createMessage(final Session session) throws JMSException
      {
        final TextMessage message = session.createTextMessage();
        message.setText(JsonHelper.marshal(action));
        return message;
      }
    };
  }
}
