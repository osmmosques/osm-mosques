package com.gurkensalat.osm.mosques.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DemoProducer
{
    private final static Logger LOGGER = LoggerFactory.getLogger(DemoProducer.class);

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value("${mq.demo.queue.name}")
    private String demoQueueName;

    public void enqueueMessage() throws Exception
    {
        LOGGER.info("Sending message...");
        rabbitTemplate.convertAndSend(demoQueueName, "Hello from RabbitMQ!");
    }
}
