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
        ExtendedMessageFormat mf = new ExtendedMessageFormat("L.marker([{0}, {1}]).bindPopup({2}).addTo(places);", Locale.ENGLISH);

        for (DitibPlace place : places)
        {
            String popupHtml = "'<b>" + place.getName() + "<br/>" + place.getAddress().getCity() + "</b>'";

            StringBuffer currentLine = new StringBuffer();
            result.append(mf.format(new Object[]{place.getLat(), place.getLon(), popupHtml}));
            result.append("\n");
        }

        model.addAttribute("placemarkers_as_js", result.toString());
        return "map/index";
    }
}
