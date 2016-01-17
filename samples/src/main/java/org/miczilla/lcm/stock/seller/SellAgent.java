package org.miczilla.lcm.stock.seller;

import javax.jms.Message;
import javax.jms.QueueConnectionFactory;
import javax.jms.TextMessage;
import org.miczilla.lcm.ConsoleHelper;
import org.miczilla.lcm.JsonHelper;
import org.miczilla.lcm.stock.StockAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.JndiDestinationResolver;
import org.springframework.jndi.JndiTemplate;

@SpringBootApplication
public class SellAgent implements CommandLineRunner
{
  @Autowired
  private JmsTemplate m_jmsTemplate;

  @Override
  public void run(final String... strings) throws Exception
  {
    ConsoleHelper.printHeader(SellAgent.class.getSimpleName());
    while (true)
    {
      final Message m = m_jmsTemplate.receive();
      if (m instanceof TextMessage)
      {
        StockAction action = JsonHelper.unmarshal(((TextMessage) m).getText(), StockAction.class);
        ConsoleHelper.println("> Sell this:");
        ConsoleHelper.println(JsonHelper.prettyPrint(action));
        ConsoleHelper.printSeparator();
      }
    }
  }

  public static void main(String[] args)
  {
    SpringApplication.run(SellAgent.class, args);
  }

  @Bean
  public JndiTemplate jndiTemplate() throws Exception
  {
    final PropertiesFactoryBean properties = new PropertiesFactoryBean();
    properties.setLocation(new ClassPathResource("jndi.properties"));
    final JndiTemplate jndiTemplate = new JndiTemplate();
    jndiTemplate.setEnvironment(properties.getObject());
    return jndiTemplate;
  }

  @Bean
  public JmsTemplate jmsTemplate(final JndiTemplate jndiTemplate) throws Exception
  {
    final JndiDestinationResolver destinationResolver = new JndiDestinationResolver();
    destinationResolver.setJndiTemplate(jndiTemplate);
    final JmsTemplate jmsTemplate = new JmsTemplate();
    jmsTemplate.setConnectionFactory(jndiTemplate.lookup("QueueConnectionFactory", QueueConnectionFactory.class));
    jmsTemplate.setDestinationResolver(destinationResolver);
    jmsTemplate.setDefaultDestinationName("SellOrders");
    return jmsTemplate;
  }
}
