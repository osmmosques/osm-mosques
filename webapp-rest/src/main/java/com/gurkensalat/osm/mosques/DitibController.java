package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.entity.Address;
import com.gurkensalat.osm.entity.DitibParsedPlace;
import com.gurkensalat.osm.entity.DitibParsedPlaceKey;
import com.gurkensalat.osm.entity.DitibPlace;
import com.gurkensalat.osm.entity.Place;
import com.gurkensalat.osm.repository.DitibParserRepository;
import com.gurkensalat.osm.repository.DitibPlaceRepository;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
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
            String dataFileName = "ditib-germany-page-" + i + ".html";
            File dataFile = new File(dataDirectory, dataFileName);

            File splitDirectory = new File(dataDirectory, "ditib-germany-split-" + i);
            splitDirectory.mkdirs();
            ditibParserRepository.prettify(splitDirectory, dataFile);

            parsedPlaces.addAll(ditibParserRepository.parse(dataFile));
        }

        LOGGER.info("DITIB Place Repository is: {}", ditibPlaceRepository);

        // Empty the storage first
        ditibPlaceRepository.deleteAll();

        int parsedPlaceNumber = 10000;
        for (DitibParsedPlace parsedPlace : parsedPlaces)
        {
            parsedPlaceNumber++;
            String key = (new DitibParsedPlaceKey(parsedPlace)).getKey();

            DitibPlace tempPlace = new DitibPlace(key);

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
                }

                place.setDitibCode(parsedPlace.getDitibCode());

                place.setName(parsedPlace.getName());

                place.setAddress(new Address());
                place.getAddress().setPostcode(parsedPlace.getPostcode());
                place.getAddress().setCity(parsedPlace.getCity());
                place.getAddress().setStreet(parsedPlace.getStreet());

                // Limit house number, we don't want all extensions like 'In Keller'
                place.getAddress().setHousenumber(StringUtils.substring(parsedPlace.getStreetNumber(), 0, 19));

                place.setPhone(parsedPlace.getPhone());
                place.setFax(parsedPlace.getFax());

                // Some Faxes have a comment after them
                // TODO: False positive, need better check...
                // if (place.getPhone().indexOf(" ") > -1)
                // {
                // LOGGER.info("Phone with comment: {}", place.getPhone());
                // }

                place = ditibPlaceRepository.save(place);

                LOGGER.debug("Saved Place {}", place);
            }
            catch (Exception e)
            {
                LOGGER.error("While persisting place", e);
                LOGGER.info("Place: {}", tempPlace);
            }
        }

        return new ResponseEntity<String>("Done Massa", null, HttpStatus.OK);
    }
}
