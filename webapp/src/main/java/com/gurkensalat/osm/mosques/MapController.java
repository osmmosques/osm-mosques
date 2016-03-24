package com.gurkensalat.osm.mosques;


import com.gurkensalat.osm.entity.PlaceType;
import com.gurkensalat.osm.mosques.entity.OsmMosquePlace;
import com.gurkensalat.osm.mosques.repository.OsmMosquePlaceRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final static String REQUEST_MAP_UNASSIGNED_COUNTRY = "/unassigned-country";

    private final static String REQUEST_MAP_OSM_POPUP = "/osm-details-for-popup";

    private final static String REQUEST_MAP_UNASSIGNED_COUNTRY_OSM_POPUP = "/osm-details-unassigned-country-for-popup";

    @Autowired
    private OsmMosquePlaceRepository osmMosquePlaceRepository;

    @RequestMapping(REQUEST_MAP)
    String map()
    {
        return "map";
    }

    @RequestMapping(REQUEST_MAP_UNASSIGNED_COUNTRY)
    String mapUnassignedCountry()
    {
        // TODO set 'flavour' property and include the main map html
        return "map-unassigned-country";
    }

    @RequestMapping(value = REQUEST_MAP_OSM_POPUP)
    String osmDetailsForPopup(Model model, @RequestParam(value = "placeKey", defaultValue = "0") String placeKey)
    {
        LOGGER.info("Requested place details for '{}'", placeKey);

        OsmMosquePlace place = findPlace(placeKey);
        populateModel(model, place);

        return "osm-details";
    }

    @RequestMapping(value = REQUEST_MAP_UNASSIGNED_COUNTRY_OSM_POPUP)
    String osmDetailsUnassignedCountryForPopup(Model model, @RequestParam(value = "placeKey", defaultValue = "0") String placeKey)
    {
        LOGGER.info("Requested place details for '{}'", placeKey);

        OsmMosquePlace place = findPlace(placeKey);
        populateModel(model, place);

        return "osm-details-unassigned-country";
    }

    private OsmMosquePlace findPlace(String placeKey)
    {
        OsmMosquePlace place = new OsmMosquePlace("dummy", PlaceType.OSM_PLACE_OF_WORSHIP);
        List<OsmMosquePlace> places = osmMosquePlaceRepository.findByKey(placeKey);
        if (!((places == null) || (places.size() == 0)))
        {
            place = places.get(0);
        }

        return place;
    }

    private void populateModel(Model model, OsmMosquePlace place)
    {

        if ("".equals(StringUtils.trimToEmpty(place.getName())))
        {
            place.setName(place.getKey());
        }

        model.addAttribute("place", place);
        model.addAttribute("placeName", place.getName());
        model.addAttribute("placeKey", place.getKey());

        if (place.getKey().length() > 14)
        {
            model.addAttribute("placeType", "Way");
        }
        else
        {
            model.addAttribute("placeType", "Node");
        }
    }
}
