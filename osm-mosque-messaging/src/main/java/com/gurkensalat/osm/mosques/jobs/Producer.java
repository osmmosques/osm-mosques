package com.gurkensalat.osm.mosques.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class Producer
{
    private final static Logger LOGGER = LoggerFactory.getLogger(Producer.class);

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

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Bean
    Receiver receiver()
    {
        return new Receiver();
    }

    @Bean
    Queue queue()
    {
        return new Queue(QueueNames.queueName, false);
    }

    @Bean
    TopicExchange exchange()
    {
        return new TopicExchange(QueueNames.exchangeName);
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange)
    {
        return BindingBuilder.bind(queue).to(exchange).with(QueueNames.queueName);
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter)
    {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(QueueNames.queueName);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(Receiver receiver)
    {
        String defaultListenerMethod = "receiveMessage";
        return new MessageListenerAdapter(receiver, defaultListenerMethod);
    }

    public void enqueueMessage() throws Exception
    {
        LOGGER.info("Sending message...");
        rabbitTemplate.convertAndSend(QueueNames.queueName, "Hello from RabbitMQ!");
    }
}
