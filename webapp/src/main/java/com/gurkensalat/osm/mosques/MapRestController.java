package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.entity.DitibPlace;
import com.gurkensalat.osm.entity.OsmPlace;
import com.gurkensalat.osm.mosques.entity.OsmMosquePlace;
import com.gurkensalat.osm.mosques.entity.StatisticsEntry;
import com.gurkensalat.osm.mosques.repository.OsmMosquePlaceRepository;
import com.gurkensalat.osm.mosques.repository.StatisticsRepository;
import com.gurkensalat.osm.repository.DitibPlaceRepository;
import com.gurkensalat.osm.repository.OsmPlaceRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.ExtendedMessageFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.stripToEmpty;

@RestController
@EnableAutoConfiguration
public class MapRestController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MapRestController.class);

    private final static String REQUEST_ROOT = "/rest/map";

    private final static String REQUEST_DITIB_MAPDATA = REQUEST_ROOT + "/placemarkers/ditib";

    private final static String REQUEST_OSM_MAPDATA = REQUEST_ROOT + "/placemarkers/osm";

    private final static String REQUEST_OSM_STATISTICS_MAPDATA = REQUEST_ROOT + "/statisticmarkers/osm";

    @Autowired
    private DitibPlaceRepository ditibPlaceRepository;

    @Autowired
    private OsmMosquePlaceRepository osmMosquePlaceRepository;

    @Autowired
    private StatisticsRepository statisticsRepository;

    @RequestMapping(value = REQUEST_DITIB_MAPDATA + "/as-json", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResponseEntity<List<MapDataEntry>> ditibMapdataAsJSON(
            @RequestParam(value = "minlat", defaultValue = "-90") String minlat,
            @RequestParam(value = "minlon", defaultValue = "-180") String minlon,
            @RequestParam(value = "maxlat", defaultValue = "90") String maxlat,
            @RequestParam(value = "maxlon", defaultValue = "180") String maxlon
    )
    {
        List<MapDataEntry> result = new ArrayList<MapDataEntry>();

        double minLongitude = minLongitude(minlon);
        double maxLongitude = maxLongitude(maxlon);
        double minLatitude = minLatitude(minlat);
        double maxLatitude = maxLatitude(maxlat);

        LOGGER.info("Ditib Place Query: {}, {}, {}, {}", new Object[]{minLongitude, minLatitude, maxLongitude, maxLatitude});

        for (DitibPlace place : ditibPlaceRepository.findByBbox(minLongitude, minLatitude, maxLongitude, maxLatitude))
        {
            MapDataEntry entry = new MapDataEntry(place);
            entry.setKey(place.getKey());
            entry.setName(place.getAddress().getCountry() + " / " + place.getAddress().getCity() + " / " + place.getName());

            result.add(entry);
        }

        return new ResponseEntity<List<MapDataEntry>>(result, null, HttpStatus.OK);
    }

    @RequestMapping(value = REQUEST_OSM_MAPDATA + "/as-json", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResponseEntity<List<MapDataEntry>> osmMapdataAsJSON(
            @RequestParam(value = "minlat", defaultValue = "-90") String minlat,
            @RequestParam(value = "minlon", defaultValue = "-180") String minlon,
            @RequestParam(value = "maxlat", defaultValue = "90") String maxlat,
            @RequestParam(value = "maxlon", defaultValue = "180") String maxlon
    )
    {
        List<MapDataEntry> result = new ArrayList<MapDataEntry>();

        double minLongitude = minLongitude(minlon);
        double maxLongitude = maxLongitude(maxlon);
        double minLatitude = minLatitude(minlat);
        double maxLatitude = maxLatitude(maxlat);

        LOGGER.info("OSM Place Query: {}, {}, {}, {}", new Object[]{minLongitude, minLatitude, maxLongitude, maxLatitude});

        for (OsmMosquePlace place : osmMosquePlaceRepository.findByBbox(minLongitude, minLatitude, maxLongitude, maxLatitude))
        {
            MapDataEntry entry = new MapDataEntry(place);
            entry.setKey(place.getKey());

            if (place.getAddress() != null)
            {
                entry.setName(stripToEmpty(place.getAddress().getCountry()) + " / " + stripToEmpty(place.getAddress().getCity()) + " / " + stripToEmpty(place.getName()));
            }

            result.add(entry);
        }

        return new ResponseEntity<List<MapDataEntry>>(result, null, HttpStatus.OK);
    }

    @RequestMapping(value = REQUEST_OSM_STATISTICS_MAPDATA + "/as-json", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResponseEntity<List<MapStatisticsDataEntry>> osmStatisticsdataAsJSON(
            @RequestParam(value = "minlat", defaultValue = "-90") String minlat,
            @RequestParam(value = "minlon", defaultValue = "-180") String minlon,
            @RequestParam(value = "maxlat", defaultValue = "90") String maxlat,
            @RequestParam(value = "maxlon", defaultValue = "180") String maxlon
    )
    {
        List<MapStatisticsDataEntry> result = new ArrayList<MapStatisticsDataEntry>();

        double minLongitude = minLongitude(minlon);
        double maxLongitude = maxLongitude(maxlon);
        double minLatitude = minLatitude(minlat);
        double maxLatitude = maxLatitude(maxlat);

        LOGGER.info("OSM Statistics Query: {}, {}, {}, {}", new Object[]{minLongitude, minLatitude, maxLongitude, maxLatitude});

        // For now, always provide all statistics
        for (StatisticsEntry statisticsEntry : statisticsRepository.findAll())
        {
            MapStatisticsDataEntry entry = new MapStatisticsDataEntry(statisticsEntry);

            entry.setKey(statisticsEntry.getCountryCode());
            entry.setOsmMosqueNodes(statisticsEntry.getOsmMosqueNodes());
            entry.setName(statisticsEntry.getCountryName() + " : " + statisticsEntry.getOsmMosqueNodes() + " Places");

            // TODO this should already be properly populated in the Statistics table...
            String countryName = Countries.getCountries().get(statisticsEntry.getCountryCode());
            if (isEmpty(countryName))
            {
                countryName = "";
            }

            entry.setCountryName(countryName);

            result.add(entry);
        }

        return new ResponseEntity<List<MapStatisticsDataEntry>>(result, null, HttpStatus.OK);
    }

    protected double minLongitude(String data)
    {
        double result = 15;

        try
        {
            result = Double.parseDouble(data);
        }
        catch (NumberFormatException nfe)
        {
            // Actually, not really important...
            LOGGER.debug("Got malformed minLongitude: '{}'", data);
        }

        if (result < -180)
        {
            result = -180;
        }

        result = Math.floor(result * 10) / 10;

        return result;
    }

    protected double minLatitude(String data)
    {
        double result = 45;

        try
        {
            result = Double.parseDouble(data);
        }
        catch (NumberFormatException nfe)
        {
            // Actually, not really important...
            LOGGER.debug("Got malformed minLatitude: '{}'", data);
        }

        if (result < -90)
        {
            result = -90;
        }

        result = Math.floor(result * 10) / 10;

        return result;
    }

    protected double maxLongitude(String data)
    {
        double result = 25;

        try
        {
            result = Double.parseDouble(data);
        }
        catch (NumberFormatException nfe)
        {
            // Actually, not really important...
            LOGGER.debug("Got malformed maxLongitude: '{}'", data);
        }

        if (result > 180)
        {
            result = 180;
        }

        result = Math.ceil(result * 10) / 10;

        return result;
    }

    protected double maxLatitude(String data)
    {
        double result = 49;

        try
        {
            result = Double.parseDouble(data);
        }
        catch (NumberFormatException nfe)
        {
            // Actually, not really important...
            LOGGER.debug("Got malformed maxLatitude: '{}'", data);
        }

        if (result > 90)
        {
            result = 90;
        }

        result = Math.ceil(result * 10) / 10;

        return result;
    }
}
