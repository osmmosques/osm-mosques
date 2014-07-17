package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.entity.OsmNode;
import com.gurkensalat.osm.entity.OsmRoot;
import com.gurkensalat.osm.entity.OsmTag;
import com.gurkensalat.osm.entity.Place;
import com.gurkensalat.osm.entity.PlaceType;
import com.gurkensalat.osm.repository.OsmRepository;
import com.gurkensalat.osm.repository.PlaceRepository;
import org.apache.commons.digester3.Digester;
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
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

@RestController
@EnableAutoConfiguration
public class OsmController
{
    private final static Logger LOGGER = LoggerFactory.getLogger(OsmController.class);

    private final static String REQUEST_ROOT = "/osm";

    private final static String REQUEST_IMPORT = REQUEST_ROOT + "/import";

    @Autowired
    private OsmRepository osmRepository;

    @Autowired
    private PlaceRepository placeRepository;

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

        // TODO sanitize data directory first...
        File dataFile = new File(dataDirectory, "turkey-places-city.osm");

        LOGGER.info("OSM Repository was: {}", osmRepository);

        OsmRoot root = osmRepository.parse(dataFile);

        LOGGER.info("Place Repository is: {}", placeRepository);

        for (OsmNode node : root.getNodes())
        {
            LOGGER.info("Read node: {}, {}, {}", node, node.getLat(), node.getLon());

            // re-create a place from OSM data
            Place tempPlace = new Place(null, PlaceType.OSM_CITY);
            tempPlace.setLat(node.getLat());
            tempPlace.setLon(node.getLon());

            for (OsmTag tag : node.getTags())
            {
                String key = tag.getKey().toLowerCase();
                String val = tag.getValue();
                LOGGER.info("    '{}' -> '{}'", key, val);

                if ("name".equals(key))
                {
                    tempPlace.setName(val);
                }
            }

            // Now, insert-or-update the place
            try
            {
                Place place;
                List<Place> places = placeRepository.findByNameAndType(tempPlace.getName(), tempPlace.getType());
                if ((places == null) || (places.size() == 0))
                {
                    // Place could not be found, insert it...
                    place = placeRepository.save(tempPlace);
                }
                else
                {
                    // take the one from the database and update it
                    place = places.get(0);
                    place = placeRepository.findOne(place.getId());
                    place.setLat(tempPlace.getLat());
                    place.setLon(tempPlace.getLon());
                    place.setType(tempPlace.getType());
                    place = placeRepository.save(place);
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
