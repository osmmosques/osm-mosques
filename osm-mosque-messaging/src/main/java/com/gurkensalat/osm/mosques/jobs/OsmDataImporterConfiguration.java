package com.gurkensalat.osm.mosques.jobs;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OsmDataImporterConfiguration extends RabbitMqConfiguration
{
    @Value("${mq.queue.osm-data-importer.name}")
    private String queueName;

    @Autowired
    private OsmDataImporterHandler handler;

    @Bean
    public RabbitTemplate rabbitTemplate()
    {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        template.setRoutingKey(this.queueName);
        template.setQueue(this.queueName);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    public Queue scrapingResultQueue()
    {
        return new Queue(this.queueName);
    }

    @Bean
    public SimpleMessageListenerContainer listenerContainer()
    {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setQueueNames(this.queueName);
        container.setMessageListener(messageListenerAdapter());

        return container;
    }

    @Bean
    public MessageListenerAdapter messageListenerAdapter()
    {
        return new MessageListenerAdapter(handler, jsonMessageConverter());
    }
}
