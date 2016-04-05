package com.gurkensalat.osm.mosques.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SlackNotifier
{
    private final static Logger LOGGER = LoggerFactory.getLogger(SlackNotifier.class);

    public static final String CHANNEL_IMPORTS = "imports";

    @Value("${mq.queue.slacknotifier.name}")
    private String queueName;

    @Autowired
    private SlackNotifierConfiguration slackNotifierConfiguration;

    public void notify(String channel, String message)
    {
        TaskMessage taskMessage = new TaskMessage();
        taskMessage.setChannel(channel);
        taskMessage.setMessage(message);

        LOGGER.info("  sending message <{}>", taskMessage);

        slackNotifierConfiguration.rabbitTemplate().convertAndSend(queueName, taskMessage);
    }
}
