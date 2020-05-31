package com.gurkensalat.osm.mosques.messaging;

import com.gurkensalat.osm.entity.OsmEntityType;
import com.gurkensalat.osm.mosques.service.OsmConverterResult;
import com.gurkensalat.osm.mosques.service.OsmConverterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Priority;
import java.time.LocalDateTime;

@Component
@Qualifier(OsmServiceMessaging.KIND_ASYNC)
@Priority(1)
@Slf4j
public class AsyncOsmConverterServiceImpl implements OsmConverterService
{
    @Autowired(required = false)
    private RabbitTemplate rabbitTemplate;

    @Override
    public OsmConverterResult importNodes(String path)
    {
        if (rabbitTemplate != null)
        {
            ImportDataMessage message = new ImportDataMessage(OsmEntityType.NODE, OsmServiceMessaging.SOURCE_FILE, path, null);
            rabbitTemplate.convertAndSend(OsmServiceMessaging.QUEUE_NAME_IMPORT_OSM_DATA, message);
        }
        return createAsyncResult(path);
    }

    @Override
    public OsmConverterResult importWays(String path)
    {
        if (rabbitTemplate != null)
        {
            ImportDataMessage message = new ImportDataMessage(OsmEntityType.WAY, OsmServiceMessaging.SOURCE_FILE, path, null);
            rabbitTemplate.convertAndSend(OsmServiceMessaging.QUEUE_NAME_IMPORT_OSM_DATA, message);
        }
        return createAsyncResult(path);
    }

    @Override
    public OsmConverterResult fetchAndImportNode(String id)
    {
        if (rabbitTemplate != null)
        {
            ImportDataMessage message = new ImportDataMessage(OsmEntityType.NODE, OsmServiceMessaging.SOURCE_API, null, id);
            rabbitTemplate.convertAndSend(OsmServiceMessaging.QUEUE_NAME_IMPORT_OSM_DATA, message);
        }
        return createAsyncResult(id);
    }

    @Override
    public OsmConverterResult fetchAndImportWay(String id)
    {
        if (rabbitTemplate != null)
        {
            ImportDataMessage message = new ImportDataMessage(OsmEntityType.WAY, OsmServiceMessaging.SOURCE_API, null, id);
            rabbitTemplate.convertAndSend(OsmServiceMessaging.QUEUE_NAME_IMPORT_OSM_DATA, message);
        }
        return createAsyncResult(id);
    }

    private OsmConverterResult createAsyncResult(String path)
    {
        OsmConverterResult result = new OsmConverterResult();
        result.setWhat("async call, result will be available elsewhere");
        result.setStart(LocalDateTime.now());
        result.setEnd(LocalDateTime.now());
        result.setPath(path);
        return result;
    }
}
