package org.miczilla.lcm.customer;

import com.google.common.collect.EvictingQueue;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;
import javax.jms.ConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.miczilla.lcm.ConsoleCommand;
import org.miczilla.lcm.ConsoleHelper;
import org.miczilla.lcm.Consts;
import org.miczilla.lcm.command.DefaultCommand;
import org.miczilla.lcm.customer.command.OrderRequestCommand;
import org.miczilla.lcm.customer.command.ShowOfferCommand;
import org.miczilla.lcm.domain.CategorySubscription;
import org.miczilla.lcm.domain.SpecialOffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.BeanFactoryDestinationResolver;

@EnableJms
@SpringBootApplication
@ImportResource("classpath:common-context.xml")
public class Application implements CommandLineRunner
{
  private static Logger logger = LoggerFactory.getLogger(Application.class);

  public static final String COMMAND_SHOW_OFFER = "offer";
  public static final String COMMAND_ORDER_REQUEST = "request";
  public static final String COMMAND_EXIT = "exit";

  @Value("${customer}")
  private String customer;

  @Value("${category}")
  private String[] categorySubscriptions;

  @Autowired
  ConfigurableApplicationContext context;

  @Bean
  public DefaultJmsListenerContainerFactory jmsListenerContainerFactory()
  {
    final DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    factory.setConnectionFactory(context.getBean("connectionFactory", ConnectionFactory.class));
    factory.setDestinationResolver(new BeanFactoryDestinationResolver(context.getBeanFactory()));
    factory.setConcurrency("3-10");
    return factory;
  }

  @Bean
  public JmsTemplate jmsTemplate()
  {
    final JmsTemplate jmsTemplate = new JmsTemplate();
    jmsTemplate.setConnectionFactory(context.getBean("connectionFactory", ConnectionFactory.class));
    jmsTemplate.setDestinationResolver(new BeanFactoryDestinationResolver(context.getBeanFactory()));
    return jmsTemplate;
  }

  @Bean
  public ActiveMQQueue specialOfferQueue()
  {
    CategorySubscription subscription = new CategorySubscription(customer, categorySubscriptions);
    return new ActiveMQQueue(subscription.getReplyToQueueName());
  }

  @Bean
  public ActiveMQQueue orderApprovalQueue()
  {
    return new ActiveMQQueue(Consts.ORDER_APPROVAL_QUEUE_NAME + customer);
  }

  @Bean
  public Queue<SpecialOffer> specialOffers()
  {
    return EvictingQueue.create(1);
  }

  public static void main(String[] args) throws Exception
  {
    SpringApplication.run(Application.class, args);
  }

  @Override
  public void run(final String... args) throws Exception
  {
    ConsoleHelper.printHeader("CUSTOMER " + customer);
    printHelp();

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
      ConsoleCommand command = new DefaultCommand();
      switch (commandName)
      {
        case COMMAND_SHOW_OFFER:
          command = context.getBean(ShowOfferCommand.class);
          break;
        case COMMAND_ORDER_REQUEST:
          command = context.getBean(OrderRequestCommand.class);
          break;
        case COMMAND_EXIT:
          command = new ConsoleCommand()
          {
            @Override
            public void execute(Set<String> args)
            {
              SpringApplication.exit(context);
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
    ConsoleHelper.println("> " + COMMAND_SHOW_OFFER + " : Shows the latest offer from the shop");
    ConsoleHelper.println("> " + COMMAND_ORDER_REQUEST + " <product ID>=<amount> [ <product ID>=<amount> ... ] : Creates/sends a order request to the shop");
    ConsoleHelper.println("> " + COMMAND_EXIT + " : Exits the Shop Application");
  }
}
