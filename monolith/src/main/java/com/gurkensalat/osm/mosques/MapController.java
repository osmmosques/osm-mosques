package com.gurkensalat.osm.mosques;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
/* package protected */
class MapController
{
    private final static String REQUEST_MAP = "/";

    @RequestMapping(REQUEST_MAP)
    String map()
    {
        return "map";
    }
}
