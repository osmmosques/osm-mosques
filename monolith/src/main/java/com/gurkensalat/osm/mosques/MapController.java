package com.gurkensalat.osm.mosques;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
/* package protected */
class MapController
{
    private static final String REQUEST_MAP = "/";

    @Value("${mapbox_token:unset}")
    private String mapboxToken;

    @GetMapping(REQUEST_MAP)
    public String map(Model model)
    {
        model.addAttribute("MAPBOX_TOKEN", mapboxToken);
        return "map";
    }
}
