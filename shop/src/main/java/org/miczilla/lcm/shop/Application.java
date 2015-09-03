package org.miczilla.lcm.shop;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import org.miczilla.lcm.ConsoleCommand;
import org.miczilla.lcm.ConsoleHelper;
import org.miczilla.lcm.command.DefaultCommand;
import org.miczilla.lcm.domain.SpecialOffer;
import org.miczilla.lcm.shop.command.ListInventoryCommand;
import org.miczilla.lcm.shop.command.ListSubscriptionsCommand;
import org.miczilla.lcm.shop.command.NewOfferCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

  public static final String COMMAND_NEW_OFFER = "offer";
  public static final String COMMAND_LIST_INVENTORY = "inventory";
  public static final String COMMAND_LIST_SUBSCRIPTIONS = "subscriptions";
  public static final String COMMAND_LIST_ORDERS = "orders";
  public static final String COMMAND_APPROVE_ORDER = "approve";
  public static final String COMMAND_REJECT_ORDER = "reject";
  public static final String COMMAND_EXIT = "exit";

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
    jmsTemplate.setExplicitQosEnabled(true);
    jmsTemplate.setTimeToLive(3600 * 1000); // TTL = 1 hour
    jmsTemplate.setDeliveryMode(Message.DEFAULT_DELIVERY_MODE);
    jmsTemplate.setPriority(Message.DEFAULT_PRIORITY);
    return jmsTemplate;
  }

  @Bean
  public Cache<String, SpecialOffer> specialOffers()
  {
    return CacheBuilder.newBuilder()
      .maximumSize(200)
        // .expireAfterWrite(1, TimeUnit.HOURS)
      .expireAfterWrite(1, TimeUnit.SECONDS)
      .build();
  }

  public static void main(String[] args) throws Exception
  {
    SpringApplication.run(Application.class, args);
  }

  @Override
  public void run(final String... args) throws Exception
  {
    ConsoleHelper.printHeader("SHOP APPLICATION");
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
        case COMMAND_NEW_OFFER:
          command = context.getBean(NewOfferCommand.class);
          break;
        case COMMAND_LIST_INVENTORY:
          command = context.getBean(ListInventoryCommand.class);
          break;
        case COMMAND_LIST_SUBSCRIPTIONS:
          command = context.getBean(ListSubscriptionsCommand.class);
          break;
        case COMMAND_LIST_ORDERS:
          break;
        case COMMAND_APPROVE_ORDER:
          break;
        case COMMAND_REJECT_ORDER:
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
    ConsoleHelper.println("> " + COMMAND_NEW_OFFER + " <product ID> [ <product ID> ... ] : Creates/sends a new offer for the given products");
    ConsoleHelper.println("> " + COMMAND_LIST_INVENTORY + " : Shows the current product inventory");
    ConsoleHelper.println("> " + COMMAND_LIST_SUBSCRIPTIONS + " : Displays the category subscriptions of our customers");
    ConsoleHelper.println("> " + COMMAND_LIST_ORDERS + " : Shows order requests from our customers");
    ConsoleHelper.println("> " + COMMAND_APPROVE_ORDER + " <order ID> : Approves a given order request");
    ConsoleHelper.println("> " + COMMAND_REJECT_ORDER + " <order ID> : Rejects a given order request");
    ConsoleHelper.println("> " + COMMAND_EXIT + " : Exits the Shop Application");
  }
}
