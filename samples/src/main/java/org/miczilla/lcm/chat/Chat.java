package org.miczilla.lcm.chat;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.apache.commons.lang.StringUtils;
import org.miczilla.lcm.ConsoleCommand;
import org.miczilla.lcm.ConsoleHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Chat implements CommandLineRunner, MessageListener
{
  private static Logger s_log = LoggerFactory.getLogger(Chat.class);

  public static final String COMMAND_PUBLISH = "publish";
  public static final String COMMAND_EXIT = "exit";

  @Autowired
  ConfigurableApplicationContext m_context;

  public static void main(String[] args)
  {
    SpringApplication.run(Chat.class, args);
  }

  @Bean
  public TopicConnectionFactory topicConnectionFactory() throws Exception
  {
    // Properties props = new Properties();
    // props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
    // props.setProperty(Context.PROVIDER_URL, "tcp://hostname:61616");
    final Context ctx = new InitialContext();
    return (TopicConnectionFactory) ctx.lookup("TopicConnectionFactory");
  }

  @Bean
  public Topic topic() throws Exception
  {
    final Context ctx = new InitialContext();
    return (Topic) ctx.lookup("SimpleChat");
  }

  @Override
  public void run(final String... strings) throws Exception
  {
    // Get the beans ...
    final TopicConnectionFactory connectionFactory = m_context.getBean(TopicConnectionFactory.class);
    final Topic topic = m_context.getBean(Topic.class);
    // Create connection ...
    final TopicConnection connection = connectionFactory.createTopicConnection();
    // Create sessions ...
    final TopicSession pubSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
    final TopicSession subSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
    // Create publisher ...
    final TopicPublisher publisher = pubSession.createPublisher(topic);
    // Create subscriber ...
    final TopicSubscriber subscriber = subSession.createSubscriber(topic, null, true);

    // Register the message listener ...
    subscriber.setMessageListener(this);

    // Kick-off the connection ...
    connection.start();

    while (true)
    {
      String input = ConsoleHelper.readLine("> ");
      String[] inputParts = input.split("\\s+");
      String commandName = inputParts[0];
      Set<String> commandArgs = new LinkedHashSet<>();
      if (inputParts.length > 1)
      {
        commandArgs.addAll(Arrays.asList(inputParts).subList(1, inputParts.length));
      }
      ConsoleCommand command;
      switch (commandName)
      {
        case COMMAND_PUBLISH:
          command = new ConsoleCommand()
          {
            @Override
            public void execute(final Set<String> args)
            {
              try
              {
                // Create a simple message ...
                final TextMessage message = pubSession.createTextMessage(StringUtils.join(args, " "));
                // ... and publish it ... !
                publisher.publish(message);
              }
              catch (JMSException e)
              {
                s_log.error(e.getMessage(), e);
              }
            }
          };
          break;
        case COMMAND_EXIT:
          command = new ConsoleCommand()
          {
            @Override
            public void execute(Set<String> args)
            {
              try
              {
                connection.stop();
                connection.close();
              }
              catch (JMSException e)
              {
                s_log.warn(e.getMessage(), e);
              }
              SpringApplication.exit(m_context);
              System.exit(0);
            }
          };
          break;
        default:
          ConsoleHelper.printlnErr("Unknown command: %s", commandName);
          printHelp();
          continue;
      }
      command.execute(commandArgs);
    }
  }

  private void printHelp()
  {
    ConsoleHelper.printHeadline("Commands");
    ConsoleHelper.println("> " + COMMAND_EXIT + " : Exits the application");
  }

  @Override
  public void onMessage(final Message message)
  {
    s_log.debug("We got a message ...");
    try
    {
      ConsoleHelper.println("> " + ((TextMessage) message).getText());
    }
    catch (JMSException e)
    {
      s_log.error(e.getMessage(), e);
    }
  }
}
