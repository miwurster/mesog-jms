package org.miczilla.lcm.b2b.topic.retailer;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.miczilla.lcm.ConsoleHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Retailer implements CommandLineRunner
{
  private static Logger s_log = LoggerFactory.getLogger(Retailer.class);

  @Autowired
  private ConfigurableApplicationContext m_context;

  private final TopicSession m_hotDealsTopicSession;
  private final TopicSubscriber m_hotDealsTopicSubscriber;

  private final Connection m_connection;

  public Retailer() throws Exception
  {
    final String uuid = "Retailer";

    final Context context = new InitialContext();

    final TopicConnectionFactory connectionFactory = (TopicConnectionFactory) context.lookup("TopicConnectionFactory");
    final TopicConnection connection = connectionFactory.createTopicConnection();

    connection.setClientID(uuid);

    final Topic topic = (Topic) context.lookup("HotDeals");
    m_hotDealsTopicSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
    m_hotDealsTopicSubscriber = m_hotDealsTopicSession.createDurableSubscriber(topic, uuid);
    m_hotDealsTopicSubscriber.setMessageListener(new MessageListener()
    {
      @Override
      public void onMessage(final Message m)
      {
        if (m instanceof StreamMessage)
        {
          StreamMessage message = (StreamMessage) m;
          try
          {
            final String product = message.readString();
            final float oldPrice = message.readFloat();
            final float newPrice = message.readFloat();
            ConsoleHelper.println("> " + product + ": " + oldPrice + ", " + newPrice);
            if (compare(oldPrice, newPrice))
            {
              ConsoleHelper.println("> Good deal! Buying 1000 items!");
              orderProduct(product, 1000, message.getJMSReplyTo(), message.getJMSMessageID());
            }
            else
            {
              ConsoleHelper.println("> Bad price, waiting for better times...");
            }
            ConsoleHelper.printSeparator();
          }
          catch (JMSException e)
          {
            s_log.error(e.getMessage(), e);
          }
        }
      }

      private boolean compare(final float oldPrice, final float newPrice)
      {
        return newPrice <= oldPrice - (oldPrice * 0.1);
      }
    });

    m_connection = connection;
    m_connection.start();
  }

  private void orderProduct(final String product, final int amount, final Destination destination, final String id) throws JMSException
  {
    final StreamMessage message = m_hotDealsTopicSession.createStreamMessage();

    message.setJMSCorrelationID(id);

    message.writeString(product);
    message.writeInt(amount);

    m_hotDealsTopicSession.createPublisher(null).send(destination, message);
  }

  private void exit() throws Exception
  {
    m_connection.stop();
    m_connection.close();
    SpringApplication.exit(m_context);
    System.exit(0);
  }

  @Override
  public void run(final String... strings) throws Exception
  {
    ConsoleHelper.printSeparator();
    ConsoleHelper.println("Enter 'exit' to exit the application.");
    ConsoleHelper.printSeparator();
    while (true)
    {
      final String input = ConsoleHelper.readLine();
      if (input.startsWith("exit"))
      {
        break;
      }
    }
    exit();
  }

  public static void main(String[] args)
  {
    SpringApplication.run(Retailer.class, args);
  }
}
