package com.gurkensalat.osm.mosques.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
public class CountryCodeReverseGeocoderHandler
{
    private final static Logger LOGGER = LoggerFactory.getLogger(CountryCodeReverseGeocoderHandler.class);

    @Value("${mq.queue.reverse-geocode-countrycode.name}")
    public void handleMessage(TaskMessage message)
    {
        LOGGER.info("  received <{}>", message);

        LOGGER.info("  done with handleMessage <{}>", message);
    }
}
