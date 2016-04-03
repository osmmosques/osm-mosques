package com.gurkensalat.osm.mosques.jobs;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Configuration
@Component
public class ImportDataHandler
{
    private final static Logger LOGGER = LoggerFactory.getLogger(ImportDataHandler.class);

    @RabbitListener(queues = "${mq.queue.import-data.name}")
    public void handleMessage(ImportDataMessage message)
    {
        LOGGER.info("  received <{}>", message);
        LOGGER.info("  done with handleMessage <{}>", message);
    }
}
