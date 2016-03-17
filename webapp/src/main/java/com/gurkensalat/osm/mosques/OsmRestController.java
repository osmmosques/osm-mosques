package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.entity.OsmNode;
import com.gurkensalat.osm.entity.OsmNodeTag;
import com.gurkensalat.osm.entity.OsmRoot;
import com.gurkensalat.osm.entity.OsmTag;
import com.gurkensalat.osm.entity.PlaceType;
import com.gurkensalat.osm.mosques.entity.OsmMosquePlace;
import com.gurkensalat.osm.mosques.repository.OsmMosquePlaceRepository;
import com.gurkensalat.osm.mosques.service.StatisticsService;
import com.gurkensalat.osm.repository.OsmParserRepository;
import com.gurkensalat.osm.repository.OsmTagRepository;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@RestController
@EnableAutoConfiguration
public class OsmRestController
{
    private final static Logger LOGGER = LoggerFactory.getLogger(OsmRestController.class);

    private final static String REQUEST_ROOT = "/rest/osm";

    private final static String REQUEST_IMPORT = REQUEST_ROOT + "/import";

    private final static String REQUEST_FETCH_FROM_SERVER = REQUEST_ROOT + "/fetch";

    @Autowired
    private OsmParserRepository osmParserRepository;

    @Autowired
    private OsmMosquePlaceRepository osmMosquePlaceRepository;

    @Autowired
    private OsmTagRepository osmTagRepository;

    @Autowired
    private StatisticsService statisticsService;

    @Value("${osm.data.location}")
    private String dataLocation;

    @RequestMapping(value = REQUEST_ROOT, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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

            dataDirectory = new File(dataDirectory, path);
        }

        LOGGER.info("OSM Repository is: {}", osmParserRepository);

        LOGGER.debug("Place Repository is: {}", osmMosquePlaceRepository);

        // TODO find a way to invalidate all tags belonging to nodes in a country...
        // osmTagRepository.invalidateAll();

        LOGGER.info("Data Directory is {}", dataDirectory.getAbsolutePath());

        // First, import everything so we won't miss anything.
        for (String continent : Continents.getContinents().keySet())
        {
            importData(dataDirectory, continent, "all");
        }

        for (String country : Countries.getCountries().keySet())
        {
            String countryCode = Countries.getCountries().get(country);
            osmMosquePlaceRepository.invalidateByCountryCode(countryCode);

            if ("germany".equals(country))
            {
                for (String state : Countries.getGermanyStates())
                {
                    importData(dataDirectory, country, state);
                }
            }
            else
            {
                importData(dataDirectory, country, "all");
            }
        }

        statisticsService.calculate();

        // Lastly, remove all invalid places
        osmMosquePlaceRepository.deleteAllInvalid();

        // Now, return the amount of items in the database
        long loaded = osmMosquePlaceRepository.count();
        LOGGER.info("Loaded {} places into database", loaded);

        return new ImportDataResponse("O.K., Massa!", loaded);
    }

    private void importData(File dataDirectory, String country, String state)
    {
        String countryCode = Countries.getCountries().get(country);
        if (isEmpty(countryCode))
        {
            // Hack to identify continental data
            countryCode = Continents.getContinents().get(country);
            if (isEmpty(countryCode))
            {
                // Still empty? Then, special hack for "world" :)
                countryCode = "ZZ";
            }
        }

        File dataFile = new File(dataDirectory, country + "-" + state + "-religion-muslim" + "-node" + ".osm");

        OsmRoot root = osmParserRepository.parse(dataFile);
        for (OsmNode node : root.getNodes())
        {
            persistOsmNode(node, countryCode, state);
        }

        LOGGER.info("Read {} nodes from {}", root.getNodes().size(), dataFile.getName());
    }

    @RequestMapping(value = REQUEST_FETCH_FROM_SERVER + "/{osmId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public GenericResponse fetchFromServer(@PathVariable("osmId") String osmId)
    {
        LOGGER.info("About to reload OSM data with ID", osmId);

        long id = Long.parseLong(osmId);

        OsmRoot root = osmParserRepository.loadFromServer(id);

        for (OsmNode node : root.getNodes())
        {
            persistOsmNode(node);
        }

        return new GenericResponse("O.K., Massa!");
    }

    private void persistOsmNode(OsmNode node)
    {
        persistOsmNode(node, null, null);
    }

    private void persistOsmNode(OsmNode node, String countryCode, String state)
    {
        LOGGER.debug("Read node: {}, {}, {}", node, node.getLat(), node.getLon());

        String key = Long.toString(node.getId());

        // re-create a place from OSM data
        OsmMosquePlace tempPlace = new OsmMosquePlace(node);
        tempPlace.setKey(key);
        tempPlace.setType(PlaceType.OSM_PLACE_OF_WORSHIP);

        if (isEmpty(tempPlace.getAddress().getState()))
        {
            tempPlace.getAddress().setState(state);
        }

        if (isEmpty(tempPlace.getAddress().getCountry()))
        {
            tempPlace.getAddress().setCountry(countryCode);
        }

        tempPlace.getContact().setWebsite(StringUtils.substring(tempPlace.getContact().getWebsite(), 0, 79));

        try
        {
            OsmMosquePlace place;
            List<OsmMosquePlace> places = osmMosquePlaceRepository.findByKey(key);
            if ((places == null) || (places.size() == 0))
            {
                // Place could not be found, insert it...
                place = osmMosquePlaceRepository.save(tempPlace);
            }
            else
            {
                // take the one from the database and update it
                place = places.get(0);
                LOGGER.debug("Found pre-existing entity {} / {}", place.getId(), place.getVersion());
                place = osmMosquePlaceRepository.findOne(place.getId());
                LOGGER.debug("  reloaded: {} / {}", place.getId(), place.getVersion());
            }

            tempPlace.copyTo(place);

            place.setValid(true);
            place = osmMosquePlaceRepository.save(place);

            LOGGER.debug("Saved Place {}", place);
            persistTags(node);
        }
        catch (Exception e)
        {
            LOGGER.error("While persisting place", e);
            LOGGER.info("Place: {}", tempPlace);
            LOGGER.info("OSM node: {}", node);
        }
    }

    private void persistTags(OsmNode node)
    {
        OsmTag osmTag = null;

        try
        {
            // Now, save the tags
            // TODO allow for Strings as node ids too
            osmTagRepository.deleteByParentTableAndParentId("OSM_PLACES", node.getId());
            for (OsmNodeTag tag : node.getTags())
            {
                // TODO allow for creation of lists of OsmTag entities from OsmNode objects
                // TODO allow for creation of OsmTag entities from OsmNodeTag objects
                osmTag = new OsmTag();
                osmTag.setParentTable("OSM_PLACES");
                osmTag.setParentId(node.getId());
                osmTag.setKey(tag.getKey());
                osmTag.setValue(tag.getValue());
                osmTag.setValid(true);

                if (osmTag.getValue().length() > 79)
                {
                    LOGGER.error("Cutting down overly long tag value");
                    LOGGER.info("    tag was: {} / '{}'", osmTag.getValue().length(), osmTag.getValue());
                    osmTag.setValue(StringUtils.substring(osmTag.getValue(), 0, 79));
                    LOGGER.info("    saving: '{}'", osmTag.getValue().length(), osmTag.getValue());
                }

                osmTag = osmTagRepository.save(osmTag);
                LOGGER.debug("  saved tag {}", osmTag);
            }
        }
        catch (Exception e)
        {
            LOGGER.error("While persisting place", e);
            LOGGER.info("OSM node: {}", node);
            LOGGER.info("     tag: {}", osmTag);
        }
    }
}
