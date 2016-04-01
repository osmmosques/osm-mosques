package com.gurkensalat.osm.mosques.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CalculatorStatisticsProducer
{
    private final static Logger LOGGER = LoggerFactory.getLogger(CalculatorStatisticsProducer.class);

    @Value("${mq.queue.calculate-statistics.name}")
    private String queueName;

    @Autowired
    private CalculatorStatisticsProducerConfiguration calculatorStatisticsProducerConfiguration;

    public void enqueueMessage( /* TaskMessage taskMessage */)
    {
        TaskMessage message = new TaskMessage();
        message.setMessage("Hello from Demo service...");

        LOGGER.info("  sending message <{}>", message);

        calculatorStatisticsProducerConfiguration.rabbitTemplate().convertAndSend(queueName, message);
    }
}
