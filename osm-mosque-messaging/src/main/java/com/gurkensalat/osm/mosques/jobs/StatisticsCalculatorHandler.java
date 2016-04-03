package com.gurkensalat.osm.mosques.jobs;

import com.gurkensalat.osm.mosques.service.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
public class StatisticsCalculatorHandler
{
    private final static Logger LOGGER = LoggerFactory.getLogger(StatisticsCalculatorHandler.class);

    @Autowired
    private StatisticsService statisticsService;

    @RabbitListener(queues = "${mq.queue.calculate-statistics.name}")
    public void handleMessage(TaskMessage message)
    {
        LOGGER.info("  received <{}>", message);
        statisticsService.calculate();
        LOGGER.info("  done with handleMessage <{}>", message);
    }
}
