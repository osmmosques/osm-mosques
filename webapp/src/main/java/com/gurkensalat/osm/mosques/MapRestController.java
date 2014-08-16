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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@EnableAutoConfiguration
public class MapRestController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MapRestController.class);

    private final static String REQUEST_ROOT = "/rest/map";

    private final static String REQUEST_DITIB_MAPDATA = REQUEST_ROOT + "/placemarkers/ditib";

    private final static String REQUEST_OSM_MAPDATA = REQUEST_ROOT + "/placemarkers/osm";

    private static final String MEDIATYPE_JAVASCRIPT = "application/javascript";

    @Autowired
    private DitibPlaceRepository ditibPlaceRepository;

    @Autowired
    private OsmPlaceRepository osmPlaceRepository;

    @RequestMapping(value = REQUEST_DITIB_MAPDATA + "/as-javascript", produces = MEDIATYPE_JAVASCRIPT)
    String ditibMapdataAsJavascript()
    {
        StringBuffer result = new StringBuffer();
        result.append("\n");

        result.append("var ditibAddressPoints = [");
        result.append("\n");

        for (DitibPlace place : ditibPlaceRepository.findAll())
        {
            String popupHtml = place.getAddress().getCity() + " / " + place.getName();
            result.append("[" + place.getLat() + ", " + place.getLon() + ", \"" + popupHtml + "\"]");
            result.append(",\n");
        }

        result.append("\n");
        result.append("]\n");

        return result.toString();
    }


    @RequestMapping(value = REQUEST_OSM_MAPDATA + "/as-javascript", produces = MEDIATYPE_JAVASCRIPT)
    String osmMapdataAsJavascript()
    {
        StringBuffer result = new StringBuffer();
        result.append("\n");

        result.append("var osmAddressPoints = [");
        result.append("\n");

        ExtendedMessageFormat mf = new ExtendedMessageFormat("[{0}, {1}, \"{2}\"]", Locale.ENGLISH);

        for (OsmPlace place : osmPlaceRepository.findAll())
        {
            String popupHtml = "OSM / " + place.getAddress().getCity() + " / " + place.getName();
            result.append("[" + place.getLat() + ", " + place.getLon() + ", \"" + popupHtml + "\"]");
            result.append(",\n");

            if ("82110".equals(place.getAddress().getPostcode()))
            {
                // [48.136, 11.387, "OSM / Germering / Germering Camii"],
                // result.append(mf.format(new Object[]{place.getLat(), place.getLon(), popupHtml}));
                String foo = mf.format(new Object[]{place.getLat(), place.getLon(), popupHtml});
                int breakpoint = 42;
            }
        }

        result.append("\n");
        result.append("]\n");

        return result.toString();
    }
}
