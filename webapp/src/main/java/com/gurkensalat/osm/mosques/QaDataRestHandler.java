package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.entity.DitibPlace;
import com.gurkensalat.osm.entity.LinkedPlace;
import com.gurkensalat.osm.entity.OsmPlace;
import com.gurkensalat.osm.repository.DitibPlaceRepository;
import com.gurkensalat.osm.repository.LinkedPlaceRepository;
import com.gurkensalat.osm.repository.OsmPlaceRepository;
import com.gurkensalat.osm.repository.QaScoreCalculator;
import com.gurkensalat.osm.utils.GreatCircle;
import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@EnableAutoConfiguration
public class QaDataRestHandler
{
    private final static Logger LOGGER = LoggerFactory.getLogger(QaDataRestHandler.class);

    private static final String APPLICATION_JSON_UTF8 = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8";

    private final static String REQUEST_ROOT = "/rest/qadata";

    private final static String REQUEST_CALCULATE_DITIB_SCORE = REQUEST_ROOT + "/calculate/ditib/{ditibCode}";

    private final static String REQUEST_CALCULATE_OSM_SCORE = REQUEST_ROOT + "/calculate/osm/{osmId}";

    private static final double DELTA_LAT_LON_10KM = 0.1;

    @Autowired
    private LinkedPlaceRepository linkedPlaceRepository;

    @Autowired
    private DitibPlaceRepository ditibPlaceRepository;

    @Autowired
    private OsmPlaceRepository osmPlaceRepository;

    @Autowired
    private QaScoreCalculator qaScoreCalculator;

    @RequestMapping(value = REQUEST_CALCULATE_DITIB_SCORE, produces = APPLICATION_JSON_UTF8)
    ResponseEntity<String> calculateDitibScore(@PathVariable("ditibCode") String ditibCode)
    {
        String result = "";
        if ("all".equals(ditibCode))
        {
            Iterable<DitibPlace> places = ditibPlaceRepository.findAll();
            for (DitibPlace place : places)
            {
                calculateDitibScore(place.getKey());
            }
        }
        else
        {
            LinkedPlace place = findByDitibCode(ditibCode);

            OsmPlace osmPlace = findClosestByBBOX(place, DELTA_LAT_LON_10KM, DELTA_LAT_LON_10KM);
            if (osmPlace != null)
            {
                place.setOsmId(Long.toString(osmPlace.getId()));
                place.setOsmPlace(osmPlace);
            }

            qaScoreCalculator.calculateDitibScore(place);
            place = linkedPlaceRepository.save(place);

            result = place.toString();
        }

        return new ResponseEntity<String>(result, null, HttpStatus.OK);
    }

    @RequestMapping(value = REQUEST_CALCULATE_OSM_SCORE, produces = APPLICATION_JSON_UTF8)
    ResponseEntity<String> calculateOsmScore(@PathVariable("osmId") String osmId)
    {
        String result = "";
        if ("all".equals(osmId))
        {
            Iterable<OsmPlace> places = osmPlaceRepository.findAll();
            for (OsmPlace place : places)
            {
                calculateOsmScore(place.getKey());
            }
        }
        else
        {
            LinkedPlace place = findByOsmId(osmId);

            qaScoreCalculator.calculateOSMScore(place);
            place = linkedPlaceRepository.save(place);

            result = place.toString();
        }

        return new ResponseEntity<String>(result, null, HttpStatus.OK);
    }

    private LinkedPlace findByDitibCode(String ditibCode)
    {
        LinkedPlace place = null;

        Iterable<LinkedPlace> places = linkedPlaceRepository.findAllByDitibCode(ditibCode);
        if (places != null)
        {
            if (places.iterator().hasNext())
            {
                place = places.iterator().next();
            }
        }

        if (place == null)
        {
            place = new LinkedPlace();
            place.setDitibCode(ditibCode);
            place = linkedPlaceRepository.save(place);
        }

        return place;
    }

    private LinkedPlace findByOsmId(String osmId)
    {
        LinkedPlace place = null;

        Iterable<LinkedPlace> places = linkedPlaceRepository.findAllByOsmId(osmId);
        if (places != null)
        {
            if (places.iterator().hasNext())
            {
                place = places.iterator().next();
            }
        }

        if (place == null)
        {
            place = new LinkedPlace();
            place.setOsmId(osmId);
            place = linkedPlaceRepository.save(place);
        }

        return place;
    }

    private OsmPlace findClosestByBBOX(LinkedPlace place, double deltaLat, double deltaLon)
    {
        OsmPlace result = null;

        List<OsmPlace> osmPlaces = osmPlaceRepository.findByBbox(
                place.getLon() - deltaLon, place.getLat() - deltaLat,
                place.getLon() + deltaLon, place.getLat() + deltaLat);

        osmPlaces = ListUtils.emptyIfNull(osmPlaces);

        double distance = 1000000000;
        for (OsmPlace osmPlace : osmPlaces)
        {
            LOGGER.info("  Calculating distance for {}", osmPlace);
            double newDistance = GreatCircle.distanceInKm(place.getLat(), place.getLon(), osmPlace.getLat(), osmPlace.getLon());

            if (newDistance < distance)
            {
                distance = newDistance;
                result = osmPlace;
            }
        }

        return result;
    }

}
