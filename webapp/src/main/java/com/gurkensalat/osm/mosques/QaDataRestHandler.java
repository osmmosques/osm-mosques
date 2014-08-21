package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.entity.DitibPlace;
import com.gurkensalat.osm.entity.LinkedPlace;
import com.gurkensalat.osm.entity.OsmPlace;
import com.gurkensalat.osm.repository.DitibPlaceRepository;
import com.gurkensalat.osm.repository.LinkedPlaceRepository;
import com.gurkensalat.osm.repository.OsmPlaceRepository;
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

@RestController
@EnableAutoConfiguration
public class QaDataRestHandler
{
    private final static Logger LOGGER = LoggerFactory.getLogger(QaDataRestHandler.class);

    private static final String APPLICATION_JSON_UTF8 = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8";

    private final static String REQUEST_ROOT = "/rest/qadata";

    private final static String REQUEST_CALCULATE_DITIB_SCORE = REQUEST_ROOT + "/calculate/ditib/{ditibCode}";

    private final static String REQUEST_CALCULATE_OSM_SCORE = REQUEST_ROOT + "/calculate/osm/{osmId}";

    @Autowired
    private LinkedPlaceRepository linkedPlaceRepository;

    @Autowired
    private DitibPlaceRepository ditibPlaceRepository;

    @Autowired
    private OsmPlaceRepository osmPlaceRepository;

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

            place.setScore(42);

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

            place.setScore(42);

            place = linkedPlaceRepository.save(place);

            result = place.toString();
        }

        return new ResponseEntity<String>(result, null, HttpStatus.OK);
    }

    private LinkedPlace findByDitibCode(String ditibCode)
    {
        LinkedPlace place = linkedPlaceRepository.findByDitibCode(ditibCode);
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
        LinkedPlace place = linkedPlaceRepository.findByOsmId(osmId);
        if (place == null)
        {
            place = new LinkedPlace();
            place.setOsmId(osmId);
            place = linkedPlaceRepository.save(place);
        }

        return place;
    }

}
