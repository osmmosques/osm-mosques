package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.entity.Address;
import com.gurkensalat.osm.entity.Contact;
import com.gurkensalat.osm.entity.DitibParsedPlace;
import com.gurkensalat.osm.entity.DitibParsedPlaceKey;
import com.gurkensalat.osm.entity.DitibPlace;
import com.gurkensalat.osm.entity.OsmNode;
import com.gurkensalat.osm.entity.OsmNodeTag;
import com.gurkensalat.osm.entity.OsmRoot;
import com.gurkensalat.osm.mosques.jobs.DitibForwardGeocoder;
import com.gurkensalat.osm.mosques.service.GeocodingService;
import com.gurkensalat.osm.repository.DitibParserRepository;
import com.gurkensalat.osm.repository.DitibPlaceRepository;
import com.gurkensalat.osm.repository.OsmParserRepository;
import com.tandogan.geostuff.opencagedata.GeocodeRepository;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

@RestController
@EnableAutoConfiguration
public class DitibRestController
{
    private final static Logger LOGGER = LoggerFactory.getLogger(DitibRestController.class);

    private final static String REQUEST_ROOT = "/rest/ditib";

    private final static String REQUEST_ROOT_INTERNAL = "/rest/internal/ditib";

    private final static String REQUEST_GEOCODE_BY_CODE = REQUEST_ROOT + "/geocode/{code}";

    private final static String REQUEST_GEOCODE_ENQUEUE = REQUEST_ROOT_INTERNAL + "/geocode/enqueue";

    private final static String REQUEST_IMPORT = REQUEST_ROOT_INTERNAL + "/import";

    private final static String REQUEST_IMPORT_DE = REQUEST_ROOT_INTERNAL + "/import-de";

    private final static String REQUEST_IMPORT_NL = REQUEST_ROOT_INTERNAL + "/import-nl";

    private final static String REQUEST_MOVE_TO_LINKED_PLACE = REQUEST_ROOT_INTERNAL + "/move-to-linked-place";

    @Autowired
    private GeocodeRepository geocodeRepository;

    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    private OsmParserRepository osmParserRepository;

    @Autowired
    private DitibParserRepository ditibParserRepository;

    @Autowired
    private DitibPlaceRepository ditibPlaceRepository;

    @Autowired
    private DitibForwardGeocoder ditibForwardGeocoder;

    @Value("${ditib.data.location}")
    private String dataLocation;

    @RequestMapping(value = REQUEST_ROOT, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> allMethods()
    {
        return new ResponseEntity<String>("{ '_links': {} }", null, HttpStatus.OK);
    }

    @RequestMapping(value = REQUEST_IMPORT, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ImportDataResponse importData()
    {
        return importData(null);
    }

    @RequestMapping(value = REQUEST_IMPORT + "/{path}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ImportDataResponse importData(@PathVariable("path") String path)
    {
        LOGGER.info("Old Endpoint, do not use anymore...", dataLocation, path);

        importDataGermany();
        importDataNetherlands();

        // Now, return the amount of items in the database
        long loaded = ditibPlaceRepository.count();
        LOGGER.info("Loaded {} places into database", loaded);

        return new ImportDataResponse("O.K., Massa!", loaded);
    }

    @RequestMapping(value = REQUEST_IMPORT_DE, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ImportDataResponse importDataGermany()
    {
        return importDataGermany(null);
    }

    @RequestMapping(value = REQUEST_IMPORT_DE + "/{path}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ImportDataResponse importDataGermany(@PathVariable("path") String path)
    {
        LOGGER.info("About to import DITIB (Germany) data from {} / {}", dataLocation, path);

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

        for (int i = 0; i < 9; i++)
        {
            String dataFileName = "germany-ditib-page-" + i + ".html";
            File dataFile = new File(dataDirectory, dataFileName);

            File splitDirectory = new File(dataDirectory, "germany-ditib-split-" + i);
            splitDirectory.mkdirs();
            ditibParserRepository.prettify(splitDirectory, dataFile);

            List<DitibParsedPlace> placesInFile = ditibParserRepository.parseGermany(dataFile);
            LOGGER.info("Loaded {} places from  {}", placesInFile.size(), dataFileName);

            parsedPlaces.addAll(placesInFile);
            LOGGER.info("  Overall places size is {}", parsedPlaces.size());
        }

        LOGGER.info("DITIB Place Repository is: {}", ditibPlaceRepository);
        persistPlaces(parsedPlaces, "DE");

        // Now, return the amount of items in the database
        long loaded = ditibPlaceRepository.count();
        LOGGER.info("Loaded {} places into database", loaded);

        return new ImportDataResponse("O.K., Massa!", loaded);
    }

    @RequestMapping(value = REQUEST_IMPORT_NL, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ImportDataResponse importDataNetherlands()
    {
        return importDataNetherlands(null);
    }

    @RequestMapping(value = REQUEST_IMPORT_NL + "/{path}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ImportDataResponse importDataNetherlands(@PathVariable("path") String path)
    {
        LOGGER.info("About to import Diyanet Isleri Vafki (Netherlands) data from {} / {}", dataLocation, path);

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

        String dataFileName = "netherlands-diyanet.html";
        File dataFile = new File(dataDirectory, dataFileName);

        List<DitibParsedPlace> parsedPlaces = ditibParserRepository.parseNetherlands(dataFile);
        LOGGER.info("Loaded {} places from  {}", parsedPlaces.size(), dataFileName);

        LOGGER.info("DITIB Place Repository is: {}", ditibPlaceRepository);
        persistPlaces(parsedPlaces, "NL");

        // Now, return the amount of items in the database
        long loaded = ditibPlaceRepository.count();
        LOGGER.info("Loaded {} places into database", loaded);

        return new ImportDataResponse("O.K., Massa!", loaded);
    }

    private void persistPlaces(List<DitibParsedPlace> parsedPlaces, String countryCode)
    {
        // Mark all places in Germany as invalid first
        ditibPlaceRepository.invalidateByCountryCode(countryCode);
        LOGGER.info("About to persist {} DITIB places", parsedPlaces.size());

        for (DitibParsedPlace parsedPlace : parsedPlaces)
        {
            String key = (new DitibParsedPlaceKey(parsedPlace)).getKey();

            DitibPlace tempPlace = new DitibPlace(key);

            tempPlace.getContact().setWebsite(StringUtils.substring(tempPlace.getContact().getWebsite(), 0, 79));

            // Now, insert-or-update the place
            try
            {
                DitibPlace place = null;
                List<DitibPlace> places = ditibPlaceRepository.findByKey(key);
                if ((places == null) || (places.size() == 0))
                {
                    // Place could not be found, insert it...
                    tempPlace.setCreationTime(DateTime.now());
                    tempPlace.setModificationTime(DateTime.now());
                    tempPlace.setLastGeocodeAttempt(DateTime.now().withDate(2000, 1, 1));
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
                place.getAddress().setCountry(countryCode);
                place.getAddress().setPostcode(parsedPlace.getPostcode());
                place.getAddress().setCity(parsedPlace.getCity());
                place.getAddress().setStreet(parsedPlace.getStreet());

                // Limit house number, we don't want all extensions like 'In Keller'
                place.getAddress().setHousenumber(StringUtils.substring(parsedPlace.getStreetNumber(), 0, 19));

                place.setContact(new Contact());
                place.getContact().setPhone(parsedPlace.getPhone());
                place.getContact().setFax(parsedPlace.getFax());
                place.getContact().setWebsite(StringUtils.substring(parsedPlace.getUrl(), 0, 79));

                place.setValid(true);
                place.setModificationTime(DateTime.now());
                place = ditibPlaceRepository.save(place);

                LOGGER.debug("Saved Place {}", place);
            }
            catch (Exception e)
            {
                LOGGER.error("While persisting place", e);
                LOGGER.info("Place: {}", tempPlace);
            }
        }

        // Lastly, remove all invalid places
        ditibPlaceRepository.deleteAllInvalid();
    }

    @RequestMapping(value = REQUEST_GEOCODE_BY_CODE, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<DitibPlace> geocode(@PathVariable String code)
    {
        geocodingService.ditibForward(code);

        DitibPlace place = null;

        // Now, reload the place from cache / database
        List<DitibPlace> places = ditibPlaceRepository.findByKey(code);
        if ((places != null) && (places.size() > 0))
        {
            place = places.get(0);
        }

        return new ResponseEntity<DitibPlace>(place, null, HttpStatus.OK);
    }

    @RequestMapping(value = REQUEST_GEOCODE_ENQUEUE, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public GenericResponse enqueueByAge()
    {
        LOGGER.debug("Request to geocode DITIB places");

        // All our DITIB places are already valid :)
        // osmMosquePlaceRepository.emptyIfNullCountryCodeFromGeocoding();

        List<DitibPlace> geocodingCandidates = ditibPlaceRepository.geocodingCandidates(new PageRequest(0, 20));
        for (DitibPlace candidate : geocodingCandidates)
        {
            ditibForwardGeocoder.enqueue(candidate.getDitibCode());
        }

        return new GenericResponse("DITIB geocoding attempt kicked off.");
    }

    @RequestMapping(value = REQUEST_MOVE_TO_LINKED_PLACE, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public GenericResponse moveToLinkedPlace()
    {
        return moveToLinkedPlace(null);
    }

    @RequestMapping(value = REQUEST_MOVE_TO_LINKED_PLACE + "/{path}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public GenericResponse moveToLinkedPlace(@PathVariable("path") String path)
    {
        // TODO Actually, this should be a service and later on a Job...

        LOGGER.info("About to set move DITIB places with data from {} / {}", dataLocation, path);

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

        LOGGER.info("OSM Parser Repository is: {}", osmParserRepository);

        File dataFile = new File(dataDirectory, "world-ditib-code-node.osm");

        OsmRoot root = osmParserRepository.parse(dataFile);
        LOGGER.info("Read {} nodes from {}", root.getNodes().size(), dataFile.getName());

        for (OsmNode node : root.getNodes())
        {
            LOGGER.info("Checking to conflate {} - {}", node.getId(), node);
            for (OsmNodeTag tag : node.getTags())
            {
                if ("ditib:code".equals(tag.getKey()))
                {
                    LOGGER.info("Checking to conflate {}", tag.getValue());

                    List<DitibPlace> possiblePlaces = ditibPlaceRepository.findByKey(tag.getValue());
                    if ((possiblePlaces != null) || (possiblePlaces.size() > 0))
                    {
                        DitibPlace place = possiblePlaces.get(0);
                        // Actually, should be precisely one
                        LOGGER.info("  Found place {} - {}", place.getName(), place);

                        place.setLat(node.getLat());
                        place.setLon(node.getLon());

                        place = ditibPlaceRepository.save(place);
                    }
                }
            }
        }

        return new GenericResponse("O.K., Massa!");
    }
}
