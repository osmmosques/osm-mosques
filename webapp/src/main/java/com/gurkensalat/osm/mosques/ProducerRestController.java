package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.mosques.jobs.DemoProducer;
import com.gurkensalat.osm.mosques.jobs.ImportData;
import com.gurkensalat.osm.mosques.jobs.SlackNotifier;
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
public class ProducerRestController
{
    private final static Logger LOGGER = LoggerFactory.getLogger(ProducerRestController.class);

    private final static String REQUEST_ROOT = "/rest/mq";

    private final static String REQUEST_ENQUEUE = REQUEST_ROOT + "/enqueue";

    @Autowired
    DemoProducer demoProducer;

    @Autowired
    SlackNotifier slackNotifier;

    @Autowired
    ImportData importData;

    @RequestMapping(value = "/rest/internal/demo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public GenericResponse demo()
    {
        try
        {
            demoProducer.enqueueMessage();

            slackNotifier.notify("random", "Some random thingy from here...");
        }
        catch (Exception e)
        {
            LOGGER.error("While launching messaging demo", e);
        }

        return new GenericResponse("Demo triggered");
    }

    @RequestMapping(value = "/rest/internal/import-demo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public GenericResponse importDemo()
    {
        try
        {
            importData.importData("zumfink", "From-Over-The-Rainbow");
        }
        catch (Exception e)
        {
            LOGGER.error("While launching messaging demo", e);
        }

        return new GenericResponse("Import Demo triggered");
    }
}
