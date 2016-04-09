package com.gurkensalat.osm.mosques.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OsmDataImporter
{
    private final static Logger LOGGER = LoggerFactory.getLogger(OsmDataImporter.class);

    public static final String KIND_NODES = "nodes";

    public static final String KIND_WAYS = "ways";

    @Value("${mq.queue.osm-data-importer.name}")
    private String queueName;

    @Autowired
    private OsmDataImporterConfiguration osmDataImporterConfiguration;

    public void importData(String kind, String path)
    {
        ImportDataMessage message = new ImportDataMessage();
        message.setKind(kind);
        message.setPath(path);

        LOGGER.info("  sending message <{}>", message);

        osmDataImporterConfiguration.rabbitTemplate().convertAndSend(queueName, message);
    }
}
