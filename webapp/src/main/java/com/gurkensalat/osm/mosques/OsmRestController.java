package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.entity.Address;
import com.gurkensalat.osm.entity.OsmNode;
import com.gurkensalat.osm.entity.OsmPlace;
import com.gurkensalat.osm.entity.OsmRoot;
import com.gurkensalat.osm.entity.OsmTag;
import com.gurkensalat.osm.entity.PlaceType;
import com.gurkensalat.osm.repository.OsmPlaceRepository;
import com.gurkensalat.osm.repository.OsmRepository;
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
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@RestController
@EnableAutoConfiguration
public class OsmRestController
{
    private final static Logger LOGGER = LoggerFactory.getLogger(OsmRestController.class);

    private final static String REQUEST_ROOT = "/rest/osm";

    private final static String REQUEST_IMPORT = REQUEST_ROOT + "/import";

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
            // TODO sanitize data directory first...
            File dataFile = new File(dataDirectory, "germany-" + state + "-religion-muslim" + ".osm");

            OsmRoot root = osmRepository.parse(dataFile);

            LOGGER.info("Read {} nodes from {}", root.getNodes().size(), dataFile.getName());

            for (OsmNode node : root.getNodes())
            {
                LOGGER.debug("Read node: {}, {}, {}", node, node.getLat(), node.getLon());

                // re-create a place from OSM data
                OsmPlace tempPlace = new OsmPlace(null, PlaceType.OSM_PLACE_OF_WORSHIP);
                tempPlace.setLat(node.getLat());
                tempPlace.setLon(node.getLon());
                tempPlace.setAddress(new Address());

                for (OsmTag tag : node.getTags())
                {
                    String key = tag.getKey().toLowerCase();
                    String val = tag.getValue();

                    if ("name".equals(key))
                    {
                        tempPlace.setName(val);
                    }
                    else if ("addr:street".equals(key))
                    {
                        tempPlace.getAddress().setStreet(val);
                    }
                    else if ("addr:housenumber".equals(key))
                    {
                        tempPlace.getAddress().setHousenumber(val);
                    }
                    else if ("addr:postcode".equals(key))
                    {
                        tempPlace.getAddress().setPostcode(val);
                    }
                    else if ("addr:city".equals(key))
                    {
                        tempPlace.getAddress().setCity(val);
                    }
                    else
                    {
                        LOGGER.debug("    '{}' -> '{}'", key, val);
                    }
                }

                String key = Long.toString(node.getId());

                if (isEmpty(tempPlace.getAddress().getState()))
                {
                    tempPlace.getAddress().setState(state);
                }

                if (isEmpty(tempPlace.getAddress().getCountry()))
                {
                    tempPlace.getAddress().setCountry("germany");
                }

                // Now, insert-or-update the place
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

                    place.setKey(key);
                    place.setLat(tempPlace.getLat());
                    place.setLon(tempPlace.getLon());
                    place.setType(tempPlace.getType());
                    place.setAddress(tempPlace.getAddress());
                    place = osmPlaceRepository.save(place);

                    LOGGER.info("Saved Place {}", place);
                }
                catch (Exception e)
                {
                    LOGGER.error("While persisting place", e);
                    LOGGER.info("Place: {}", tempPlace);
                    LOGGER.info("OSM node: {}", node);
                }
            }
        }

        return new ResponseEntity<String>("Done Massa", null, HttpStatus.OK);
    }
}
