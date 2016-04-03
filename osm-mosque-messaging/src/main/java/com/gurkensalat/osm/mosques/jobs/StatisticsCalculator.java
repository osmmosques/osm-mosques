package com.gurkensalat.osm.mosques.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StatisticsCalculator
{
    private final static Logger LOGGER = LoggerFactory.getLogger(StatisticsCalculator.class);

    @Value("${mq.queue.calculate-statistics.name}")
    private String queueName;

    @Autowired
    private StatisticsCalculatorConfiguration statisticsCalculatorConfiguration;

    public void calculate()
    {
        TaskMessage message = new TaskMessage();
        message.setMessage("Kick off some statistics...");

        LOGGER.info("  sending message <{}>", message);

        statisticsCalculatorConfiguration.rabbitTemplate().convertAndSend(queueName, message);
    }
}
