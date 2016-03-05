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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
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

    private static final String APPLICATION_JSON_UTF8 = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8";

    @Autowired
    private DitibPlaceRepository ditibPlaceRepository;

    @Autowired
    private OsmPlaceRepository osmPlaceRepository;

    @RequestMapping(value = REQUEST_DITIB_MAPDATA + "/as-javascript", produces = MEDIATYPE_JAVASCRIPT)
    public String ditibMapdataAsJavascript()
    {
        StringBuffer result = new StringBuffer();
        result.append("\n");

        result.append("var ditibAddressPoints = [");
        result.append("\n");

        for (DitibPlace place : ditibPlaceRepository.findAll())
        {
            String popupHtml = place.getAddress().getCity() + " / " + place.getName();
            popupHtml = popupHtml.replaceAll("\"", "\'");
            result.append("[" + place.getLat() + ", " + place.getLon() + ", \"" + popupHtml + "\"]");
            result.append(",\n");
        }

        result.append("\n");
        result.append("]\n");

        return result.toString();
    }

    @RequestMapping(value = REQUEST_DITIB_MAPDATA + "/as-json", produces = APPLICATION_JSON_UTF8)
    ResponseEntity<List<MapDataEntry>> ditibMapdataAsJSON()
    {
        // TODO round query parameters to the nearest minute (or 10 minutes?)

        List<MapDataEntry> result = new ArrayList<MapDataEntry>();

        for (DitibPlace place : ditibPlaceRepository.findAll())
        {
            MapDataEntry entry = new MapDataEntry(place);
            entry.setName(place.getAddress().getCountry() + " / " + place.getAddress().getCity() + " / " + place.getName());

            result.add(entry);
        }

        return new ResponseEntity<List<MapDataEntry>>(result, null, HttpStatus.OK);
    }

    @RequestMapping(value = REQUEST_OSM_MAPDATA + "/as-javascript", produces = MEDIATYPE_JAVASCRIPT)
    public String osmMapdataAsJavascript()
    {
        StringBuffer result = new StringBuffer();
        result.append("\n");

        result.append("var osmAddressPoints = [");
        result.append("\n");

        ExtendedMessageFormat mf = new ExtendedMessageFormat("[{0}, {1}, \"{2}\"]", Locale.ENGLISH);

        for (OsmPlace place : osmPlaceRepository.findAll())
        {
            String popupHtml = "OSM / " + place.getAddress().getCity() + " / " + place.getName();
            popupHtml = popupHtml.replaceAll("\"", "\'");
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

    @RequestMapping(value = REQUEST_OSM_MAPDATA + "/as-json", produces = APPLICATION_JSON_UTF8)
    ResponseEntity<List<MapDataEntry>> osmMapdataAsJSON()
    {
        // TODO round query parameters to the nearest minute (or 10 minutes?)

        List<MapDataEntry> result = new ArrayList<MapDataEntry>();

        for (OsmPlace place : osmPlaceRepository.findAll())
        {
            MapDataEntry entry = new MapDataEntry(place);
            entry.setName(place.getAddress().getCountry() + " / " + place.getAddress().getCity() + " / " + place.getName());

            result.add(entry);
        }

        return new ResponseEntity<List<MapDataEntry>>(result, null, HttpStatus.OK);
    }
}
