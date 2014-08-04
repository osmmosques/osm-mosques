package com.gurkensalat.osm.mosques;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@EnableAutoConfiguration
public class MapController
{
    private final static Logger LOGGER = LoggerFactory.getLogger(MapController.class);

    @RequestMapping("/map")
    String map()
    {
        return "map/index";
    }
}
