package com.gurkensalat.osm.mosques;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MapController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MapController.class);

    private final static String REQUEST_MAP = "/map";

    @RequestMapping(REQUEST_MAP)
    String map()
    {
        return "map";
    }
}
