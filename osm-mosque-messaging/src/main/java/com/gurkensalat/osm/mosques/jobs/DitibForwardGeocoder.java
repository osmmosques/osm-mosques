package com.gurkensalat.osm.mosques.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DitibForwardGeocoder
{
    private final static Logger LOGGER = LoggerFactory.getLogger(DitibForwardGeocoder.class);

    @Value("${mq.queue.forward-geocode-ditib.name}")
    private String queueName;

    @Autowired
    private DitibForwardGeocoderConfiguration configuration;

    public void enqueue(String ditibCode)
    {
        TaskMessage taskMessage = new TaskMessage();
        taskMessage.setChannel("nothing-at-all");
        taskMessage.setMessage(ditibCode);

        LOGGER.info("  sending message <{}>", taskMessage);

        configuration.rabbitTemplate().convertAndSend(queueName, taskMessage);
    }
}
