package com.gurkensalat.osm.mosques.messaging;

import com.gurkensalat.osm.entity.OsmEntityType;
import com.gurkensalat.osm.mosques.service.OsmConverterResult;
import com.gurkensalat.osm.mosques.service.OsmConverterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AsyncOsmConverterServiceListener
{
    @Autowired
    @Qualifier("sync")
    private OsmConverterService innerOsmConverterService;

    @Bean
    public Queue queue()
    {
        return new Queue(OsmServiceMessaging.QUEUE_NAME_IMPORT_OSM_DATA, false);
    }

    @RabbitListener(queues = OsmServiceMessaging.QUEUE_NAME_IMPORT_OSM_DATA)
    public void listen(ImportDataMessage in)
    {
        log.info("Message read from {} : {}", OsmServiceMessaging.QUEUE_NAME_IMPORT_OSM_DATA, in);

        OsmConverterResult result = new OsmConverterResult();
        if (OsmEntityType.NODE.equals(in.getKind()))
        {
            if (OsmServiceMessaging.SOURCE_FILE.equals(in.getSource()))
            {
                result = innerOsmConverterService.importNodes(in.getPath());
            }
            else if (OsmServiceMessaging.SOURCE_API.equals(in.getSource()))
            {
                result = innerOsmConverterService.fetchAndImportNode(in.getId());
            }
        }
        else if (OsmEntityType.WAY.equals(in.getKind()))
        {
            if (OsmServiceMessaging.SOURCE_FILE.equals(in.getSource()))
            {
                result = innerOsmConverterService.importWays(in.getPath());
            }
            else if (OsmServiceMessaging.SOURCE_API.equals(in.getSource()))
            {
                result = innerOsmConverterService.fetchAndImportWay(in.getId());
            }
        }
        else
        {
            log.error("unknown message type");
        }

        log.info("async call result {}", result);
    }
}
