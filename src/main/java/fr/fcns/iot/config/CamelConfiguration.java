package fr.fcns.iot.config;

import fr.fcns.iot.listener.MQTTListener;
import fr.fcns.iot.transformer.AggregateTransformer;
import fr.fcns.iot.transformer.InputTransformer;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpMethods;
import org.apache.camel.component.jms.JmsConfiguration;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;

/**
 * Created by lmarchau on 28/06/2016.
 */
@Configuration
@PropertySource("classpath:mqtt.properties")
public class CamelConfiguration {

    @Autowired
    private CamelContext camelContext;

    @Value("${mqtt.uri}")
    private String mqttURI;
    @Value("${mqtt.topic.subscribe}")
    private String fromRoute;
    @Value("${mqtt.rest.push}")
    private String toRoute;


    @Autowired
    private InputTransformer inputTransformer;
    @Autowired
    private AggregateTransformer aggregateTransformer;

    // configuration pool ActiveMQ pour ajout au contexte

    @Bean
    public ActiveMQConnectionFactory jsmConnectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL(mqttURI);
        factory.setUseRetroactiveConsumer(true);
        return factory;
    }

    @Bean(name = "connectionFactory", initMethod = "start", destroyMethod = "stop")
    public PooledConnectionFactory pooledConnectionFactory(ConnectionFactory jmsConnectionFactory) {
        PooledConnectionFactory factory = new PooledConnectionFactory();
        factory.setMaxConnections(50);
        factory.setConnectionFactory(jmsConnectionFactory);
        return factory;
    }

    @Bean
    public JmsConfiguration jsmConfiguration(ActiveMQConnectionFactory jmsConnectionFactory) {
        JmsConfiguration config = new JmsConfiguration();
        config.setConnectionFactory(jmsConnectionFactory);
        config.setConcurrentConsumers(1);
        return config;
    }

    @Bean
    public JmsTemplate jmsTemplate(PooledConnectionFactory pooledConnectionFactory) {
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(pooledConnectionFactory);
        return template;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        return factory;
    }

    @Bean
    public ActiveMQComponent activemq(JmsConfiguration jmsConfiguration) {
        ActiveMQComponent component = new ActiveMQComponent();
        component.setConfiguration(jmsConfiguration);
        component.setTransacted(true);
        component.setCacheLevelName("CACHE_CONSUMER");
        return component;
    }

//    @Bean
    public RoutesBuilder mqttRoute(MQTTListener listener) {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(fromRoute).bean(listener);
            }
        };
    }

    @Bean
    public RoutesBuilder sensorRoute(MQTTListener listener) {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(fromRoute)
                    .bean(inputTransformer)
                    .bean(aggregateTransformer)
                    .marshal().json(JsonLibrary.Jackson)
                    .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.POST))
                    .setHeader(Exchange.CONTENT_TYPE, constant(ContentType.APPLICATION_JSON.getMimeType()))
                    .to(toRoute);
            }
        };
    }

}
