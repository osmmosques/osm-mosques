package com.gurkensalat.osm.mosques.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoHandler
{
    private final static Logger LOGGER = LoggerFactory.getLogger(DemoHandler.class);

    @RabbitListener(queues = "${mq.queue.demo.name}")
    public void handleMessage(TaskMessage message)
    {
        LOGGER.info("  received <{}>", message);
        LOGGER.info("  done with handleMessage <{}>", message);
    }
}
