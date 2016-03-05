package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.entity.OsmNode;
import com.gurkensalat.osm.entity.OsmNodeTag;
import com.gurkensalat.osm.entity.OsmPlace;
import com.gurkensalat.osm.entity.OsmRoot;
import com.gurkensalat.osm.entity.OsmTag;
import com.gurkensalat.osm.entity.PlaceType;
import com.gurkensalat.osm.repository.OsmPlaceRepository;
import com.gurkensalat.osm.repository.OsmRepository;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@RestController
@EnableAutoConfiguration
public class OsmRestController
{
    private final static Logger LOGGER = LoggerFactory.getLogger(OsmRestController.class);

    private final static String REQUEST_ROOT = "/rest/osm";

    private final static String REQUEST_IMPORT = REQUEST_ROOT + "/import";

    private final static String REQUEST_FETCH_FROM_SERVER = REQUEST_ROOT + "/fetch";

    private final static Map<String, String> countries = new HashMap<>();

    static
    {
        countries.put("albania", "AL");
        countries.put("andorra", "AD");
        countries.put("austria", "AT");
        countries.put("azores", "PT"); // Autonomous Region of the Azores
        countries.put("belarus", "BY");
        countries.put("belgium", "BE");
        countries.put("bosnia-herzegovina", "BA");
        countries.put("bulgaria", "BG");
        countries.put("croatia", "HR");
        countries.put("cyprus", "CY");
        countries.put("czech-republic", "CZ");
        countries.put("denmark", "DK");
        countries.put("estonia", "EE");
        countries.put("faroe-islands", "FO");
        countries.put("finland", "FI");
        countries.put("france", "FR");
        countries.put("georgia", "GE");
        countries.put("germany", "DE");
        countries.put("great-britain", "GB"); // United Kingdom
        countries.put("greece", "GR");
        countries.put("hungary", "HU");
        countries.put("iceland", "IS");
        countries.put("ireland-and-northern-ireland", "IE");
        countries.put("isle-of-man", "IM");
        countries.put("italy", "IT");
        countries.put("kosovo", "XK");
        countries.put("latvia", "LV");
        countries.put("liechtenstein", "LI");
        countries.put("lithuania", "LT");
        countries.put("luxembourg", "LU");
        countries.put("macedonia", "MK");
        countries.put("malta", "MT");
        countries.put("moldova", "MD");
        countries.put("monaco", "MC");
        countries.put("montenegro", "ME");
        countries.put("netherlands", "NL");
        countries.put("norway", "NO");
        countries.put("poland", "PL");
        countries.put("portugal", "PT");
        countries.put("romania", "RO");
        countries.put("serbia", "RS");
        countries.put("slovakia", "SK");
        countries.put("slovenia", "SI");
        countries.put("spain", "ES");
        countries.put("sweden", "SE");
        countries.put("switzerland", "CH");
        countries.put("turkey", "TR");
        countries.put("ukraine", "UA");
    }

    private final static List<String> germanCounties =
            Arrays.asList(
                    "baden-wuerttemberg",
                    "bayern",
                    "berlin",
                    "brandenburg",
                    "bremen",
                    "hamburg",
                    "hessen",
                    "mecklenburg-vorpommern",
                    "niedersachsen",
                    "nordrhein-westfalen",
                    "rheinland-pfalz",
                    "saarland",
                    "sachsen-anhalt",
                    "sachsen",
                    "schleswig-holstein",
                    "thueringen"
            );

    @Autowired
    private OsmRepository osmRepository;

    @Autowired
    private OsmPlaceRepository osmPlaceRepository;

    @Autowired
    private OsmTagRepository osmTagRepository;

    @Value("${osm.data.location}")
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

            dataDirectory = new File(dataDirectory, path);
        }

        LOGGER.info("OSM Repository is: {}", osmRepository);

        LOGGER.debug("Place Repository is: {}", osmPlaceRepository);

        // Empty the storage first
        // osmPlaceRepository.deleteAll();
        // TODO actually invalidate, by setting the attribute via updateAll()

        LOGGER.info("Data Directory is {}", dataDirectory.getAbsolutePath());

        for (String state : germanCounties)
        {
            File dataFile = new File(dataDirectory, "germany-" + state + "-religion-muslim" + ".osm");

            OsmRoot root = osmRepository.parse(dataFile);

            LOGGER.info("Read {} nodes from {}", root.getNodes().size(), dataFile.getName());

            for (OsmNode node : root.getNodes())
            {
                persistOsmNode(node, "germany", state);
            }
        }

        for (String country : countries.keySet())
        {
            File dataFile = new File(dataDirectory, country + "-all-religion-muslim" + ".osm");

            OsmRoot root = osmRepository.parse(dataFile);

            LOGGER.info("Read {} nodes from {}", root.getNodes().size(), dataFile.getName());

            for (OsmNode node : root.getNodes())
            {
                persistOsmNode(node, country, "");
            }
        }

        return new ResponseEntity<String>("Done Massa", null, HttpStatus.OK);
    }


    @RequestMapping(value = REQUEST_FETCH_FROM_SERVER + "/{osmId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> fetchFromServer(@PathVariable("osmId") String osmId)
    {
        LOGGER.info("About to reload OSM data with ID", osmId);

        long id = Long.parseLong(osmId);

        OsmRoot root = osmRepository.loadFromServer(id);

        for (OsmNode node : root.getNodes())
        {
            persistOsmNode(node);
        }

        return new ResponseEntity<String>("Done Massa", null, HttpStatus.OK);
    }

    private void persistOsmNode(OsmNode node)
    {
        persistOsmNode(node, null, null);
    }

    private void persistOsmNode(OsmNode node, String country, String state)
    {
        LOGGER.debug("Read node: {}, {}, {}", node, node.getLat(), node.getLon());

        String key = Long.toString(node.getId());

        // re-create a place from OSM data
        OsmPlace tempPlace = new OsmPlace(node);
        tempPlace.setKey(key);
        tempPlace.setType(PlaceType.OSM_PLACE_OF_WORSHIP);

        if (isEmpty(tempPlace.getAddress().getState()))
        {
            tempPlace.getAddress().setState(state);
        }

        if (isEmpty(tempPlace.getAddress().getCountry()))
        {
            tempPlace.getAddress().setCountry(countries.get(country));
        }

        tempPlace.getContact().setWebsite(StringUtils.substring(tempPlace.getContact().getWebsite(), 0, 79));

        try
        {
            OsmPlace place;
            List<OsmPlace> places = osmPlaceRepository.findByKey(key);
            if ((places == null) || (places.size() == 0))
            {
                // Place could not be found, insert it...
                place = osmPlaceRepository.save(tempPlace);
            }
            else
            {
                // take the one from the database and update it
                place = places.get(0);
                LOGGER.debug("Found pre-existing entity {} / {}", place.getId(), place.getVersion());
                place = osmPlaceRepository.findOne(place.getId());
                LOGGER.debug("  reloaded: {} / {}", place.getId(), place.getVersion());
            }

            tempPlace.copyTo(place);

            place = osmPlaceRepository.save(place);

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
