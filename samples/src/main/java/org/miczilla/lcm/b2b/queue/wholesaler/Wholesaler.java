package org.miczilla.lcm.b2b.queue.wholesaler;

import java.util.Enumeration;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
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

  private final QueueConnection m_queueConnection;
  private final TopicConnection m_topicConnection;

  private final QueueSession m_buyOrdersQueueSession;
  private final QueueBrowser m_buyOrdersQueueBrowser;
  private final QueueReceiver m_buyOrdersQueueReceiver;

  private final TopicSession m_hotDealsTopicSession;
  private final TopicPublisher m_hotDealsTopicPublisher;

  public Wholesaler() throws Exception
  {
    final Context context = new InitialContext();

    final QueueConnectionFactory queueConnectionFactory = (QueueConnectionFactory) context.lookup("QueueConnectionFactory");
    m_queueConnection = queueConnectionFactory.createQueueConnection();

    final TopicConnectionFactory topicConnectionFactory = (TopicConnectionFactory) context.lookup("TopicConnectionFactory");
    m_topicConnection = topicConnectionFactory.createTopicConnection();

    final Queue queue = (Queue) context.lookup("BuyOrders");
    m_buyOrdersQueueSession = m_queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
    m_buyOrdersQueueBrowser = m_buyOrdersQueueSession.createBrowser(queue);
    m_buyOrdersQueueReceiver = m_buyOrdersQueueSession.createReceiver(queue);

    final Topic topic = (Topic) context.lookup("HotDeals");
    m_hotDealsTopicSession = m_topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
    m_hotDealsTopicPublisher = m_hotDealsTopicSession.createPublisher(topic);

    m_queueConnection.start();
    m_topicConnection.start();
  }

  private void publishMessage(final String... input) throws Exception
  {
    final StreamMessage message = m_hotDealsTopicSession.createStreamMessage();

    message.writeString(input[0]);
    message.writeFloat(Float.valueOf(input[1]));
    message.writeFloat(Float.valueOf(input[2]));

    m_hotDealsTopicPublisher.publish(message);
  }

  private void exit() throws Exception
  {
    m_queueConnection.stop();
    m_topicConnection.stop();
    m_queueConnection.close();
    m_topicConnection.close();
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
      else if (input.startsWith("peek"))
      {
        final Enumeration messages = m_buyOrdersQueueBrowser.getEnumeration();
        if (!messages.hasMoreElements())
        {
          ConsoleHelper.println("> We received no orders yet!");
        }
        while (messages.hasMoreElements())
        {
          final Message m = m_buyOrdersQueueReceiver.receiveNoWait();
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
            // !!! move enum pointer !!!
            messages.nextElement();
          }
        }
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
