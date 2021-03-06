package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.entity.DitibPlace;
import com.gurkensalat.osm.entity.LinkedPlace;
import com.gurkensalat.osm.mosques.entity.OsmMosquePlace;
import com.gurkensalat.osm.mosques.repository.OsmMosquePlaceRepository;
import com.gurkensalat.osm.repository.DitibPlaceRepository;
import com.gurkensalat.osm.repository.LinkedPlaceRepository;
import com.gurkensalat.osm.repository.QaScoreCalculator;
import com.gurkensalat.osm.utils.GreatCircle;
import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

import static com.gurkensalat.osm.utils.DistanceConstants.DELTA_LAT_LON_10_KM;
import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@RestController
@EnableAutoConfiguration
public class QaDataRestHandler
{
    private final static Logger LOGGER = LoggerFactory.getLogger(QaDataRestHandler.class);

    private static final String APPLICATION_JSON_UTF8 = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8";

    private final static String REQUEST_ROOT = "/rest/qadata";

    private final static String REQUEST_CALCULATE_ALL_SCORES = REQUEST_ROOT + "/calculate/all";

    private final static String REQUEST_CALCULATE_DITIB_SCORE = REQUEST_ROOT + "/calculate/ditib/{ditibCode}";

    private final static String REQUEST_CALCULATE_OSM_SCORE = REQUEST_ROOT + "/calculate/osm/{osmId}";

    @Autowired
    private LinkedPlaceRepository linkedPlaceRepository;

    @Autowired
    private DitibPlaceRepository ditibPlaceRepository;

    @Autowired
    private OsmMosquePlaceRepository osmMosquePlaceRepository;

    @Autowired
    private QaScoreCalculator qaScoreCalculator;


    @RequestMapping(value = REQUEST_CALCULATE_ALL_SCORES, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse calculateAllScores()
    {
        String result = "";
        linkedPlaceRepository.invalidateAll();

        calculateDitibScore("all");
        calculateOsmScore("all");

        linkedPlaceRepository.deleteAllInvalid();

        return new GenericResponse(result);
    }

    @RequestMapping(value = REQUEST_CALCULATE_DITIB_SCORE, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse calculateDitibScore(@PathVariable("ditibCode") String ditibCode)
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
            DitibPlace ditibPlace = null;
            List<DitibPlace> ditibPlaces = emptyIfNull(ditibPlaceRepository.findByKey(ditibCode));
            if (ditibPlaces.size() > 0)
            {
                ditibPlace = ditibPlaces.get(0);
            }

            Iterable<LinkedPlace> places = findByDitibCode(ditibCode);
            for (LinkedPlace place : places)
            {
                if (ditibPlace != null)
                {
                    place.setLat(ditibPlace.getLat());
                    place.setLon(ditibPlace.getLon());
                    place.setDitibPlace(ditibPlace);
                    place = linkedPlaceRepository.save(place);
                }

                OsmMosquePlace osmMosquePlace = findClosestByBBOX(place, DELTA_LAT_LON_10_KM, DELTA_LAT_LON_10_KM);
                if (osmMosquePlace != null)
                {
                    place.setOsmId(osmMosquePlace.getKey());
                    place.setOsmMosquePlace(osmMosquePlace);
                    place = linkedPlaceRepository.save(place);
                }

                qaScoreCalculator.calculateDitibScore(place);
                place.setValid(true);
                place = linkedPlaceRepository.save(place);

                result = place.toString();
            }
        }

        return new GenericResponse(result);
    }

    @RequestMapping(value = REQUEST_CALCULATE_OSM_SCORE, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse calculateOsmScore(@PathVariable("osmId") String osmId)
    {
        String result = "";
        if ("all".equals(osmId))
        {
            Iterable<OsmMosquePlace> places = osmMosquePlaceRepository.findAll();
            for (OsmMosquePlace place : places)
            {
                calculateOsmScore(place.getKey());
            }
        }
        else
        {
            Iterable<LinkedPlace> places = findByOsmId(osmId);
            for (LinkedPlace place : places)
            {
                qaScoreCalculator.calculateOSMScore(place);
                place.setValid(true);
                place = linkedPlaceRepository.save(place);

                result = place.toString();
            }
        }

        return new GenericResponse(result);
    }

    private Iterable<LinkedPlace> findByDitibCode(String ditibCode)
    {
        Iterable<LinkedPlace> places = linkedPlaceRepository.findAllByDitibCode(ditibCode);
        if (places == null || (!(places.iterator().hasNext())))
        {
            LinkedPlace place = new LinkedPlace();
            place.setDitibCode(ditibCode);
            place = linkedPlaceRepository.save(place);

            places = Arrays.asList(place);
        }

        return places;
    }

    private Iterable<LinkedPlace> findByOsmId(String osmId)
    {
        Iterable<LinkedPlace> places = linkedPlaceRepository.findAllByOsmId(osmId);
        if (places == null || (!(places.iterator().hasNext())))
        {
            LinkedPlace place = new LinkedPlace();
            place.setOsmId(osmId);
            place = linkedPlaceRepository.save(place);

            places = Arrays.asList(place);
        }

        return places;
    }

    private OsmMosquePlace findClosestByBBOX(LinkedPlace place, double deltaLat, double deltaLon)
    {
        OsmMosquePlace result = null;

        LOGGER.info("  Calculating distance for {} / {} / {} / {} / {}",
                place.getId(),
                place.getOsmId(),
                place.getDitibCode(),
                place.getLat(),
                place.getLon());

        List<OsmMosquePlace> osmPlaces = osmMosquePlaceRepository.findByBbox(
                place.getLon() - deltaLon, place.getLat() - deltaLat,
                place.getLon() + deltaLon, place.getLat() + deltaLat);

        osmPlaces = ListUtils.emptyIfNull(osmPlaces);

        double distance = 1000000000;
        for (OsmMosquePlace osmPlace : osmPlaces)
        {
            LOGGER.info("    calculating {}", osmPlace);
            double newDistance = GreatCircle.distanceInKm(place.getLat(), place.getLon(), osmPlace.getLat(), osmPlace.getLon());
            LOGGER.info("      distance is {}", distance);

            if (newDistance < distance)
            {
                distance = newDistance;
                result = osmPlace;
            }
        }

        return result;
    }

}
