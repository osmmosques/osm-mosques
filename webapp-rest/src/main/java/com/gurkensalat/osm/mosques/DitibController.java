package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.entity.DitibParsedPlace;
import com.gurkensalat.osm.entity.DitibPlace;
import com.gurkensalat.osm.entity.Place;
import com.gurkensalat.osm.repository.DitibParserRepository;
import com.gurkensalat.osm.repository.DitibPlaceRepository;
import org.apache.commons.lang3.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

@RestController
@EnableAutoConfiguration
public class DitibController
{
    private final static Logger LOGGER = LoggerFactory.getLogger(DitibController.class);

    private final static String REQUEST_ROOT = "/ditib";

    private final static String REQUEST_IMPORT = REQUEST_ROOT + "/import";

    @Autowired
    private DitibParserRepository ditibParserRepository;

    @Autowired
    private DitibPlaceRepository ditibPlaceRepository;

    @Value("${ditib.data.location}")
    private String dataLocation;

    @RequestMapping(value = REQUEST_ROOT, produces = "application/hal+json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> allMethods()
    {
        return new ResponseEntity<String>("{ '_links': {} }", null, HttpStatus.OK);
    }

    @RequestMapping(value = REQUEST_IMPORT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> importData()
    {
        return importData(null);
    }

    @RequestMapping(value = REQUEST_IMPORT + "/{path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> importData(@PathVariable("path") String path)
    {
        LOGGER.info("About to import OSM data from {} / {}", dataLocation, path);

        File dataDirectory = new File(dataLocation);
        if (path != null)
        {
            try
            {
                path = URLDecoder.decode(path, CharEncoding.UTF_8);
            }
            catch (UnsupportedEncodingException e)
            {
                LOGGER.error("While decoding optional path", e);
            }

            // TODO sanitize data directory first...
            dataDirectory = new File(dataDirectory, path);
        }

        LOGGER.info("DITIB Parser Repository is: {}", ditibParserRepository);

        List<DitibParsedPlace> parsedPlaces = new ArrayList<DitibParsedPlace>();

        for (int i = 1; i < 9; i++)
        {
            File dataFile = new File(dataDirectory, "ditib-germany-page-" + i + ".html");
            parsedPlaces.addAll(ditibParserRepository.parse(dataFile));
        }

        LOGGER.info("DITIB Place Repository is: {}", ditibPlaceRepository);

        int parsedPlaceNumber = 10000;
        for (DitibParsedPlace parsedPlace : parsedPlaces)
        {
            parsedPlaceNumber++;
            String key = "D-" + Integer.toString(parsedPlaceNumber).substring(1);

            DitibPlace tempPlace = new DitibPlace(key);
            tempPlace.setDitibCode(parsedPlace.getDitibCode());
            tempPlace.setName(parsedPlace.getName());
            // tempPlace.setName(key);
            // tempPlace.setName();

            // Now, insert-or-update the place
            try
            {
                DitibPlace place = null;
                List<Place> places = null; // ditibPlaceRepository.findByNameAndType(tempPlace.getName(), tempPlace.getType());
                if ((places == null) || (places.size() == 0))
                {
                    // Place could not be found, insert it...
                    place = ditibPlaceRepository.save(tempPlace);
                }
                else
                {
                    // take the one from the database and update it
                    // place = places.get(0);
                    // place = ditibPlaceRepository.findOne(place.getId());
                    // place.setLat(tempPlace.getLat());
                    // place.setLon(tempPlace.getLon());
                    // place.setType(tempPlace.getType());
                    // place = ditibPlaceRepository.save(place);
                }

                LOGGER.info("Saved Place {}", place);
            }
            catch (Exception e)
            {
                LOGGER.info("While persisting place", e);
                LOGGER.info("Place:");
                LOGGER.info("    name: '{}'", tempPlace.getName());
            }
        }

        return new ResponseEntity<String>("Done Massa", null, HttpStatus.OK);
    }
}
