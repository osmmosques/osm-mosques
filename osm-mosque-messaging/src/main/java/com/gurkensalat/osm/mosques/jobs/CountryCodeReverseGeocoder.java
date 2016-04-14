package com.gurkensalat.osm.mosques.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CountryCodeReverseGeocoder
{
    private final static Logger LOGGER = LoggerFactory.getLogger(CountryCodeReverseGeocoder.class);

    @Value("${mq.queue.reverse-geocode-countrycode.name}")
    private String queueName;

    @Autowired
    private SlackNotifierConfiguration slackNotifierConfiguration;

    public void enqueue(String key)
    {
        TaskMessage taskMessage = new TaskMessage();
        taskMessage.setChannel("nothing-at-all");
        taskMessage.setMessage(key);

        LOGGER.info("  sending message <{}>", taskMessage);

        slackNotifierConfiguration.rabbitTemplate().convertAndSend(queueName, taskMessage);
    }
}
