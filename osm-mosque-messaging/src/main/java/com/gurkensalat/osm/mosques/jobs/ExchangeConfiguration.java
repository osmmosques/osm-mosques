package com.gurkensalat.osm.mosques.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ExchangeConfiguration
{
    private final static Logger LOGGER = LoggerFactory.getLogger(ExchangeConfiguration.class);

    /*
     * From Log:
     *
     * 2016-03-13 16:46:23,346  INFO ontext.support.DefaultLifecycleProcessor: 341 - Starting beans in phase 2147483647
     * 2016-03-13 16:46:23,557  INFO bbit.connection.CachingConnectionFactory: 281 - Created new connection: SimpleConnection@37d89907 [delegate=amqp://hakan@127.0.0.1:5672/]
     * 2016-03-13 16:46:23,560  INFO ngframework.amqp.rabbit.core.RabbitAdmin: 428 - Auto-declaring a non-durable, auto-delete, or exclusive Queue (spring-boot) durable:false, auto-delete:false, exclusive:false. It will be redeclared if the broker stops and is restarted while the connection factory is alive, but all messages will be lost.
     * 2016-03-13 16:46:23,665  INFO g.apache.coyote.http11.Http11NioProtocol: 180 - Initializing ProtocolHandler ["http-nio-127.0.0.1-8888"]
     *
     * Look at the Auto-declaring part....
     */

//    @Autowired
//    RabbitTemplate rabbitTemplate;

//    @Value("${mq.exchange.name}")
//    private String exchangeName;

//    @Value("${mq.demo.queue.name}")
//    private String demoQueueName;

//    @Value("${mq.queue.calculate-statistics}")
//    private String calculateStatisticsQueueName;

//    @Bean
//    DemoReceiver demoReceiver()
//    {
//        return new DemoReceiver();
//    }
//
//    @Bean
//    @Qualifier("demoBindingQueue")
//    Queue demoQueue()
//    {
//        return new Queue(demoQueueName, true);
//    }
//
//    @Bean
//    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter)
//    {
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory);
//        container.setQueueNames(demoQueueName);
//        container.setMessageListener(listenerAdapter);
//        return container;
//    }
//
//    @Bean
//    TopicExchange exchange()
//    {
//        return new TopicExchange(exchangeName, true, false);
//    }
//
//    @Bean
//    Binding demoBinding(Queue queue, TopicExchange exchange)
//    {
//        return BindingBuilder.bind(queue).to(exchange).with(demoQueueName);
//    }
//
//    @Bean
//    MessageListenerAdapter listenerAdapter(DemoReceiver receiver)
//    {
//        String defaultListenerMethod = "receiveMessage";
//        return new MessageListenerAdapter(receiver, defaultListenerMethod);
//    }
}
