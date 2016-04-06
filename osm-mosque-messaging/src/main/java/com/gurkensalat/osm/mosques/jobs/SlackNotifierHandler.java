package com.gurkensalat.osm.mosques.jobs;

import com.gurkensalat.osm.mosques.service.StatisticsService;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Configuration
@Component
public class SlackNotifierHandler
{
    private final static Logger LOGGER = LoggerFactory.getLogger(SlackNotifierHandler.class);

    @Value("${mq.queue.slacknotifier.bot.token}")
    private String botToken;

    @RabbitListener(queues = "${mq.queue.slacknotifier.name}")
    public void handleMessage(TaskMessage message)
    {
        LOGGER.info("  received <{}>", message);

        try
        {
            SlackSession session = SlackSessionFactory.createWebSocketSlackSession(botToken);
            session.connect();

            for (SlackChannel channel : session.getChannels())
            {
                LOGGER.debug("Channel: {}", channel);
            }

            SlackChannel channel = session.findChannelByName(message.getChannel());
            if (channel == null)
            {
                LOGGER.error("Could not find channel {} for message {}", message.getChannel(), message.getMessage());
            }
            else
            {
                session.sendMessage(channel, message.getMessage());
            }

            session.disconnect();
        }
        catch (IOException ioe)
        {
            LOGGER.error("While talking to slack", ioe);
        }

        LOGGER.info("  done with handleMessage <{}>", message);
    }
}
