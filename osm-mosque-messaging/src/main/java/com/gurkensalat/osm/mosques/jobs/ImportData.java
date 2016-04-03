package com.gurkensalat.osm.mosques.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ImportData
{
    private final static Logger LOGGER = LoggerFactory.getLogger(ImportData.class);

    @Value("${mq.queue.import-data.name}")
    private String queueName;

    @Autowired
    private ImportDataConfiguration importDataConfiguration;

    public void importData(String kind, String path)
    {
        ImportDataMessage message = new ImportDataMessage();
        message.setKind(kind);
        message.setPath(path);

        LOGGER.info("  sending message <{}>", message);

        importDataConfiguration.rabbitTemplate().convertAndSend(queueName, message);
    }
}
