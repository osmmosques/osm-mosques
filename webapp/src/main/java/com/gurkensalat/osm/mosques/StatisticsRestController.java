package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.mosques.service.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
public class StatisticsRestController
{
    private final static Logger LOGGER = LoggerFactory.getLogger(StatisticsRestController.class);

    private final static String REQUEST_ROOT = "/rest/statistics";

    private final static String REQUEST_CALCULATE = REQUEST_ROOT + "/calculate";

    @Autowired
    private StatisticsService statisticsService;

    @RequestMapping(value = REQUEST_CALCULATE, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public GenericResponse calculate()
    {
        try
        {
            statisticsService.calculate();
        }
        catch (Exception e)
        {
            LOGGER.error("While launching statistics calculation", e);
        }

        return new GenericResponse("Calculation triggered");
    }
}
