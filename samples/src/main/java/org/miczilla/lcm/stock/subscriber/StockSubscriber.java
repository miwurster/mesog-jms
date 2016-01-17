package org.miczilla.lcm.stock.subscriber;

import javax.jms.MapMessage;
import javax.jms.QueueConnectionFactory;
import javax.jms.TopicConnectionFactory;
import org.apache.commons.lang.math.RandomUtils;
import org.miczilla.lcm.stock.StockAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.JndiDestinationResolver;
import org.springframework.jndi.JndiTemplate;

@EnableJms
@SpringBootApplication
public class StockSubscriber
{
  private static Logger s_log = LoggerFactory.getLogger(StockSubscriber.class);

  @Autowired
  private Sender m_sender;

  @JmsListener(destination = "StockTrader", subscription = "StockQuotes")
  public void processStockQuotes(MapMessage message) throws Exception
  {
    final String company = (String) message.getMapNames().nextElement();
    final int price = message.getInt(company);
    final int amount = RandomUtils.nextInt(50) + 1;
    final StockAction action = new StockAction(
      StockSubscriber.class.getSimpleName(),
      company, amount, price
    );
    if (price < 10)
    {
      s_log.info("> Buy {} stocks of company {}, value is {}", new Object[]{amount, company, price});
      m_sender.buyStock(action);
    }
    else if (price < 100)
    {
      s_log.info("> Sell {} stocks of company {}, value is {}", new Object[]{amount, company, price});
      m_sender.sellStock(action);
    }
  }

  public static void main(String[] args)
  {
    SpringApplication.run(StockSubscriber.class, args);
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
  public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(final JndiTemplate jndiTemplate) throws Exception
  {
    final JndiDestinationResolver destinationResolver = new JndiDestinationResolver();
    destinationResolver.setJndiTemplate(jndiTemplate);
    final DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    factory.setConnectionFactory(jndiTemplate.lookup("TopicConnectionFactory", TopicConnectionFactory.class));
    factory.setDestinationResolver(destinationResolver);
    factory.setPubSubDomain(true);
    factory.setSubscriptionDurable(true);
    factory.setClientId(StockSubscriber.class.getSimpleName());
    return factory;
  }

  @Bean
  public JmsTemplate jmsTemplate(final JndiTemplate jndiTemplate) throws Exception
  {
    final JndiDestinationResolver destinationResolver = new JndiDestinationResolver();
    destinationResolver.setJndiTemplate(jndiTemplate);
    final JmsTemplate jmsTemplate = new JmsTemplate();
    jmsTemplate.setConnectionFactory(jndiTemplate.lookup("QueueConnectionFactory", QueueConnectionFactory.class));
    jmsTemplate.setDestinationResolver(destinationResolver);
    return jmsTemplate;
  }
}
