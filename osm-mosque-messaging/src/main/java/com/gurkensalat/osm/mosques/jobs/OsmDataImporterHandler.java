package com.gurkensalat.osm.mosques.jobs;

import com.gurkensalat.osm.mosques.service.OsmConverterResult;
import com.gurkensalat.osm.mosques.service.OsmConverterService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import static com.gurkensalat.osm.mosques.jobs.OsmDataImporter.KIND_NODES;
import static com.gurkensalat.osm.mosques.jobs.OsmDataImporter.KIND_WAYS;

@Configuration
@Component
public class OsmDataImporterHandler
{
    private final static Logger LOGGER = LoggerFactory.getLogger(OsmDataImporterHandler.class);

    @Autowired
    private OsmConverterService osmConverterService;

    @Autowired
    private SlackNotifier slackNotifier;

    @RabbitListener(queues = "${mq.queue.osm-data-importer.name}")
    public void handleMessage(ImportDataMessage message)
    {
        LOGGER.info("  received <{}>", message);

        OsmConverterResult result = null;
        if (KIND_NODES.equals(message.getKind()))
        {
            result = osmConverterService.importNodes(message.getPath());
        }
        else if (KIND_WAYS.equals(message.getKind()))
        {
            result = osmConverterService.importWays(message.getPath());
        }

        if (result != null)
        {
            slackNotifier.notify(SlackNotifier.CHANNEL_IMPORTS, prettyPrint(result));
        }

        LOGGER.info("  done with handleMessage <{}>", message);
    }


    private String prettyPrint(OsmConverterResult result)
    {
        // TODO this needs to be done in the service....
        String s = "";
        s = s + "Imported " + result.getPlaces() + " " + " places" + " from " + result.getPath() + ".";
        s = s + "\n";
        s = s + "There were " + result.getNodes() + " nodes and " + result.getWays() + " ways in the source.";
        s = s + "\n";

        if ((result.getStart() != null) && (result.getEnd() != null))
        {

            long millis = result.getEnd().getMillis() - result.getStart().getMillis();
            long seconds = millis / 1000;
            long minutes = seconds / 60;
            seconds = seconds % 60;

            // TODO generify the beautified time span
            s = s + "The import took ";
            if (minutes > 0)
            {
                s = s + minutes + " minutes";

                if (seconds > 0)
                {
                    s = s + " and";
                }
            }
            s = s + seconds + " seconds.";
        }

        return s;
    }
}
