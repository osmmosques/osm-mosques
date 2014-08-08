package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.entity.Address;
import com.gurkensalat.osm.entity.DitibParsedPlace;
import com.gurkensalat.osm.entity.DitibParsedPlaceKey;
import com.gurkensalat.osm.entity.DitibPlace;
import com.gurkensalat.osm.repository.DitibParserRepository;
import com.gurkensalat.osm.repository.DitibPlaceRepository;
import com.tandogan.geostuff.opencagedata.GeocodeRepository;
import com.tandogan.geostuff.opencagedata.GeocodeRepositoryImpl;
import com.tandogan.geostuff.opencagedata.entity.GeocodeResponse;
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
import org.springframework.web.client.RestTemplate;

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

    private final static String REQUEST_GEOCODE_FIRST = REQUEST_ROOT + "/geocode_first";

    private final static String REQUEST_IMPORT = REQUEST_ROOT + "/import";

    @Autowired
    private GeocodeRepository geocodeRepository;

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
        // ditibPlaceRepository.deleteAll();
        // TODO actually invalidate, by setting the attribute via updateAll()

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
                List<DitibPlace> places = ditibPlaceRepository.findByKey(key);
                if ((places == null) || (places.size() == 0))
                {
                    // Place could not be found, insert it...
                    place = ditibPlaceRepository.save(tempPlace);
                }
                else
                {
                    // take the one from the database and update it
                    place = places.get(0);
                    LOGGER.debug("Found pre-existing entity {} / {}", place.getId(), place.getVersion());
                    place = ditibPlaceRepository.findOne(place.getId());
                    LOGGER.debug("  reloaded: {} / {}", place.getId(), place.getVersion());
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

    @RequestMapping(value = REQUEST_GEOCODE_FIRST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GeocodeResponse> geocodeFirst()
    {
        RestTemplate restTemplate = new RestTemplate();

        LOGGER.info("Using geocoder library {}", geocodeRepository);
        LOGGER.info("    {}", ((GeocodeRepositoryImpl) geocodeRepository).getUrlBase());
        LOGGER.info("    {}", ((GeocodeRepositoryImpl) geocodeRepository).getApiKey());

        ((GeocodeRepositoryImpl) geocodeRepository).setTemplate(restTemplate);

        GeocodeResponse response = null;
        DitibPlace place = findPlaceToEncode();
        if (place != null)
        {
            String query = "";
            query = query + (place.getAddress().getStreet() == null ? "" : place.getAddress().getStreet());
            query = query + " ";
            query = query + (place.getAddress().getHousenumber() == null ? "" : place.getAddress().getHousenumber());
            query = query + " ";
            query = query + (place.getAddress().getPostcode() == null ? "" : place.getAddress().getPostcode());
            query = query + " ";
            query = query + (place.getAddress().getCity() == null ? "" : place.getAddress().getCity());

            LOGGER.info("Query string is: '{}'", query);

            response = geocodeRepository.query(query);
        }

        LOGGER.info("Response is: {}", response);

        return new ResponseEntity<GeocodeResponse>(response, null, HttpStatus.OK);
    }

    private DitibPlace findPlaceToEncode()
    {
        DitibPlace result = null;

        // TODO create repository method
        Iterable<DitibPlace> places = ditibPlaceRepository.findAll();
        for (DitibPlace place : places)
        {
            if (place.getLat() == 0 && place.getLon() == 0)
            {
                result = place;
                break;
            }
        }

        return result;
    }
}
