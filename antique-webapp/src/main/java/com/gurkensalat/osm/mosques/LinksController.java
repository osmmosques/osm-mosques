package com.gurkensalat.osm.mosques;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LinksController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(LinksController.class);

    private final static String REQUEST_LINKS = "/links";

    @RequestMapping(REQUEST_LINKS)
    String map()
    {
        return "links";
    }
}
