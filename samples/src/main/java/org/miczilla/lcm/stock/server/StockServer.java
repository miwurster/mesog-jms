package org.miczilla.lcm.stock.server;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import org.apache.commons.lang.math.RandomUtils;
import org.miczilla.lcm.ConsoleHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jndi.JndiTemplate;

@EnableJms
@SpringBootApplication
public class StockServer implements CommandLineRunner
{
  private static Logger s_log = LoggerFactory.getLogger(StockServer.class);

  @Autowired
  private JndiTemplate m_jndiTemplate;

  @Autowired
  private JmsTemplate m_jmsTemplate;

  private static String[] s_stock = new String[]{
    "BMW", "DTAG", "DCAG", "MAN"
  };

  @Override
  public void run(final String... strings) throws Exception
  {
    ConsoleHelper.printHeader("StockServer");
    final Topic topic = m_jndiTemplate.lookup("StockTrader", Topic.class);
    while (true)
    {
      for (final String stock : s_stock)
      {
        final int price = RandomUtils.nextInt(200) + 1;
        m_jmsTemplate.send(topic, new MessageCreator()
        {
          @Override
          public Message createMessage(final Session session) throws JMSException
          {
            final MapMessage message = session.createMapMessage();
            message.setInt(stock, price);
            return message;
          }
        });
      }
      final int messages = s_stock.length;
      final int wait = RandomUtils.nextInt(10) + 1;
      s_log.info("Sent {} stock quotes!", messages);
      s_log.info("Wait for {} seconds...", wait);
      Thread.sleep(wait * 1000);
      ConsoleHelper.printSeparator();
    }
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
    final JmsTemplate jmsTemplate = new JmsTemplate();
    jmsTemplate.setConnectionFactory(jndiTemplate.lookup("TopicConnectionFactory", TopicConnectionFactory.class));
    jmsTemplate.setPubSubDomain(true);
    jmsTemplate.setPubSubNoLocal(true);
    return jmsTemplate;
  }

  public static void main(String[] args)
  {
    SpringApplication.run(StockServer.class, args);
  }
}
