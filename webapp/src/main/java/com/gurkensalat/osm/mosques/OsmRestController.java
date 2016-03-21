package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.mosques.service.OsmConverterService;
import org.joda.time.DateTime;
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

@RestController
@EnableAutoConfiguration
public class OsmRestController
{
    private final static Logger LOGGER = LoggerFactory.getLogger(OsmRestController.class);

    private final static String REQUEST_ROOT = "/rest/osm";

    private final static String REQUEST_ROOT_INTERNAL = "/rest/internal/osm";

    private final static String REQUEST_IMPORT_FRESHENED_NODES = REQUEST_ROOT_INTERNAL + "/import-freshened-nodes";

    private final static String REQUEST_IMPORT_FRESHENED_WAYS = REQUEST_ROOT_INTERNAL + "/import-freshened-ways";

    private final static String REQUEST_IMPORT_QUADTILED_NODES = REQUEST_ROOT_INTERNAL + "/import-quadtiled-nodes";

    private final static String REQUEST_IMPORT_QUADTILED_WAYS = REQUEST_ROOT_INTERNAL + "/import-quadtiled-ways";

    private final static String REQUEST_FETCH_FROM_SERVER = REQUEST_ROOT + "/fetch";

    @Autowired
    private OsmConverterService osmConverterService;

    @Value("${osm.data.location}")
    private String dataLocation;

    @RequestMapping(value = REQUEST_ROOT, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> allMethods()
    {
        return new ResponseEntity<String>("{ '_links': {} }", null, HttpStatus.OK);
    }

    @RequestMapping(value = REQUEST_IMPORT_FRESHENED_NODES, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public GenericResponse importFreshenedNodes()
    {
        osmConverterService.importNodes("world/world-religion-muslim-node-freshened.osm");

        return new GenericResponse("Import kicked off.");
    }

    @RequestMapping(value = REQUEST_IMPORT_FRESHENED_WAYS, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public GenericResponse importFreshenedWays()
    {
        osmConverterService.importWays("world/world-religion-muslim-way-freshened.osm");

        return new GenericResponse("Import kicked off.");
    }

    @RequestMapping(value = REQUEST_IMPORT_QUADTILED_NODES, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public GenericResponse importQuadtiledNodes()
    {
        String cell = calculateQuadtileCell();
        osmConverterService.importNodes("by-quadtile/world-religion-muslim-node-by-quadtile-" + cell + ".osm");

        return new GenericResponse("Import kicked off.");
    }

    @RequestMapping(value = REQUEST_IMPORT_QUADTILED_WAYS, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public GenericResponse importQuadtiledWays()
    {
        String cell = calculateQuadtileCell();
        osmConverterService.importWays("by-quadtile/world-religion-muslim-way-by-quadtile-" + cell + ".osm");

        return new GenericResponse("Import kicked off.");
    }

    @RequestMapping(value = REQUEST_FETCH_FROM_SERVER + "/{osmId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public GenericResponse fetchFromServer(@PathVariable("osmId") String osmId)
    {
        /*
        LOGGER.info("About to reload OSM data with ID", osmId);

        long id = Long.parseLong(osmId);

        OsmRoot root = osmParserRepository.loadFromServer(id);

        for (OsmNode node : root.getNodes())
        {
            persistOsmNode(node);
        }
*/
        return new GenericResponse("O.K., Massa!");
    }

    /* package level protection for unit testing */
    String calculateQuadtileCell()
    {
        String result;

        DateTime today = DateTime.now().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfDay(0);

        long day = today.getMillis() / 1000 / 86400;

        // Offset to align with the shell script
        day = day + 1;

        long quadtile = day % 16;
        long row = quadtile / 4;
        long col = quadtile % 4;

        result = Long.toString(row) + Long.toString(col) + "-" + Long.toString(quadtile);

        return result;
    }
}
