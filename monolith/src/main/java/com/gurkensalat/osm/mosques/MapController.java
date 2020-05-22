package com.gurkensalat.osm.mosques;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
/* package protected */
class MapController
{
    private static final String REQUEST_MAP = "/";

    @GetMapping(REQUEST_MAP)
    public String map()
    {
        return "map";
    }
}
