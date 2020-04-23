package com.gurkensalat.osm.mosques.service;

import com.gurkensalat.osm.ConfigurationMarker;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ConfigurationMarker.class)
public class OsmConverterServiceImplTest
{
    @Autowired
    private OsmConverterService osmConverterService;

    @Before
    public void setUp()
    {
    }

    @Test
    public void wiringPossible()
    {
        System.err.println("converter service is " + osmConverterService);
        // assertNotNull(osmConverterService);
    }
}
