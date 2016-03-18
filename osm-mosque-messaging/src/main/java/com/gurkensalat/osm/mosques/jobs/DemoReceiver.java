package com.gurkensalat.osm.mosques.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

public class DemoReceiver
{
    private final static Logger LOGGER = LoggerFactory.getLogger(DemoReceiver.class);

    private CountDownLatch latch = new CountDownLatch(1);

    public void receiveMessage(String message)
    {
        LOGGER.info("Received <{}>", message);
        LOGGER.info("  Doing some long-durated work...");

        try
        {
            Thread.sleep(9 * 1000);
        }
        catch (InterruptedException ie)
        {
            LOGGER.error("  While simulating some work...", ie);
        }

        latch.countDown();
        LOGGER.info("  done with receiveMessage <{}>", message);
    }

    public CountDownLatch getLatch()
    {
        return latch;
    }

}
