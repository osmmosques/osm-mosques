package com.gurkensalat.osm.mosques;


import com.gurkensalat.osm.mosques.repository.OsmMosquePlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class MapController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MapController.class);

    private final static String REQUEST_MAP = "/";

    private final static String REQUEST_MAP_OSM_POPUP = "/osm-details-for-popup";

    @Autowired
    private OsmMosquePlaceRepository osmMosquePlaceRepository;

    @RequestMapping(REQUEST_MAP)
    String map()
    {
        return "map";
    }

    @RequestMapping(value = REQUEST_MAP_OSM_POPUP)
    String osmDetailsForPopup(Model model, @RequestParam(value = "placeKey", defaultValue = "0") String placeKey)
    {
        LOGGER.info("Requested place details for '{}'", placeKey);

        model.addAttribute("placeKey", placeKey);

        return "osm-details";
    }
}
