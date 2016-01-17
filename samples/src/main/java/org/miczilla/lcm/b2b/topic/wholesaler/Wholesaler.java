package org.miczilla.lcm.b2b.topic.wholesaler;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TemporaryTopic;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
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
public class Wholesaler implements CommandLineRunner
{
  private static Logger s_log = LoggerFactory.getLogger(Wholesaler.class);

  @Autowired
  private ConfigurableApplicationContext m_context;

  private final TemporaryTopic m_buyOrdersTopic;
  private final TopicSession m_buyOrdersTopicSession;
  private final TopicSubscriber m_buyOrdersTopicSubscriber;

  private final TopicSession m_hotDealsTopicSession;
  private final TopicPublisher m_hotDealsTopicPublisher;

  private final Connection m_connection;

  public Wholesaler() throws Exception
  {
    final Context context = new InitialContext();

    final TopicConnectionFactory connectionFactory = (TopicConnectionFactory) context.lookup("ConnectionFactory");
    final TopicConnection connection = connectionFactory.createTopicConnection();

    m_buyOrdersTopicSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
    m_hotDealsTopicSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);

    m_buyOrdersTopic = m_buyOrdersTopicSession.createTemporaryTopic();
    m_buyOrdersTopicSubscriber = m_buyOrdersTopicSession.createSubscriber(m_buyOrdersTopic);
    m_buyOrdersTopicSubscriber.setMessageListener(new MessageListener()
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
            final int items = message.readInt();
            ConsoleHelper.println("> We sold '" + items + "' items of product '" + product + "'");
          }
          catch (JMSException e)
          {
            s_log.error(e.getMessage(), e);
          }
        }
      }
    });

    final Topic topic = (Topic) context.lookup("HotDeals");
    m_hotDealsTopicPublisher = m_hotDealsTopicSession.createPublisher(topic);

    m_connection = connection;
    m_connection.start();
  }

  private void publishMessage(final String... input) throws Exception
  {
    final StreamMessage message = m_hotDealsTopicSession.createStreamMessage();

    message.setJMSReplyTo(m_buyOrdersTopic);

    message.writeString(input[0]);
    message.writeFloat(Float.valueOf(input[1]));
    message.writeFloat(Float.valueOf(input[2]));

    m_hotDealsTopicPublisher.publish(message);
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
    ConsoleHelper.println("Enter product name, current price and new price; or enter 'exit' to exit the application.");
    ConsoleHelper.printSeparator();
    while (true)
    {
      final String input = ConsoleHelper.readLine();
      final String[] inputParts = input.split("\\s+");
      if (input.startsWith("exit"))
      {
        break;
      }
      else if (inputParts.length != 3)
      {
        ConsoleHelper.printlnErr("Wrong number of arguments given");
        ConsoleHelper.printlnErr("Use following format: <product> <old price> <new price>");
      }
      else
      {
        publishMessage(inputParts);
      }
    }
    exit();
  }

  public static void main(String[] args)
  {
    SpringApplication.run(Wholesaler.class, args);
  }
}
