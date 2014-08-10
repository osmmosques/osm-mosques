package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.entity.DitibPlace;
import com.gurkensalat.osm.entity.OsmPlace;
import com.gurkensalat.osm.repository.DitibPlaceRepository;
import com.gurkensalat.osm.repository.OsmPlaceRepository;
import org.apache.commons.lang3.text.ExtendedMessageFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Locale;

@Controller
@EnableAutoConfiguration
public class MapController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MapController.class);

    private static final String MEDIATYPE_JAVASCRIPT = "application/javascript";

    @Autowired
    private DitibPlaceRepository ditibPlaceRepository;

    @Autowired
    private OsmPlaceRepository osmPlaceRepository;

    @RequestMapping("/map")
    String map(Model model)
    {
        StringBuffer result = new StringBuffer();
        result.append("\n");

        ExtendedMessageFormat mf = new ExtendedMessageFormat("[{0}, {1}, \"{2}\"]", Locale.ENGLISH);

        result.append("var ditibAddressPoints = [");
        result.append("\n");

        for (DitibPlace place : ditibPlaceRepository.findAll())
        {
            String popupHtml = place.getAddress().getCity() + " / " + place.getName();
            result.append(mf.format(new Object[]{place.getLat(), place.getLon(), popupHtml}));
            result.append(",\n");
        }

        result.append("\n");
        result.append("]\n");

        result.append("var osmAddressPoints = [");
        result.append("\n");

        for (OsmPlace place : osmPlaceRepository.findAll())
        {
            String popupHtml = "OSM / " + place.getAddress().getCity() + " / " + place.getName();
            result.append(mf.format(new Object[]{place.getLat(), place.getLon(), popupHtml}));
            result.append(",\n");
        }

        result.append("\n");
        result.append("]\n");

        model.addAttribute("placemarkers_as_js", result.toString());
        return "map/index";
    }
}
