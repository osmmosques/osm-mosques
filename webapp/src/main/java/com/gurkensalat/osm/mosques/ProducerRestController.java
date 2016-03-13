package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.mosques.jobs.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
public class ProducerRestController
{
    private final static Logger LOGGER = LoggerFactory.getLogger(ProducerRestController.class);

    private final static String REQUEST_ROOT = "/rest/mq";

    private final static String REQUEST_ENQUEUE = REQUEST_ROOT + "/enqueue";

    @Autowired
    Producer producer;

    @RequestMapping(value = REQUEST_ENQUEUE, produces = "application/hal+json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> allMethods()
    {
        try
        {
            producer.enqueueMessage();
        }
        catch (Exception e)
        {
            LOGGER.error("While launching Producer", e);
        }

        return new ResponseEntity<String>("{ '_links': {} }", null, HttpStatus.OK);
    }
}
