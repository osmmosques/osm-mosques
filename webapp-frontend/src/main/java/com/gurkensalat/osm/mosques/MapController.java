package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.entity.DitibPlace;
import com.gurkensalat.osm.repository.DitibPlaceRepository;
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


    @RequestMapping("/map")
    String map(Model model)
    {
        StringBuffer result = new StringBuffer();
        result.append("\n");

        Iterable<DitibPlace> places = ditibPlaceRepository.findAll();
        model.addAttribute("places", places);

        ExtendedMessageFormat mf = new ExtendedMessageFormat("[{0}, {1}, \"{2}\"]", Locale.ENGLISH);

        result.append("var addressPoints = [");
        result.append("\n");

        for (DitibPlace place : places)
        {
            // String popupHtml = "'<b>" + place.getName() + "<br/>" + place.getAddress().getCity() + "</b>'";
            String popupHtml = place.getAddress().getCity() + " / " + place.getName();

            StringBuffer currentLine = new StringBuffer();
            result.append(mf.format(new Object[]{place.getLat(), place.getLon(), popupHtml}));
            result.append(",\n");
        }

        result.append(mf.format(new Object[]{48.1364, 11.3872928, "Germering / OSM"}));
        result.append("\n");
        result.append("]\n");

        model.addAttribute("placemarkers_as_js", result.toString());
        return "map/index";
    }
}
