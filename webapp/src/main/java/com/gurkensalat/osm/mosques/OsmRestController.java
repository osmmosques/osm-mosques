package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.mosques.service.OsmConverterService;
import org.apache.commons.lang3.StringUtils;
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

    private final static String REQUEST_IMPORT_FULLY_NODES = REQUEST_ROOT_INTERNAL + "/import-fully-nodes";

    private final static String REQUEST_IMPORT_FULLY_WAYS = REQUEST_ROOT_INTERNAL + "/import-fully-ways";

    private final static String REQUEST_REIMPORT_FROM_SERVER_NODE = REQUEST_ROOT + "/reimport-node";

    private final static String REQUEST_REIMPORT_FROM_SERVER_WAY = REQUEST_ROOT + "/reimport-way";

    private final static String REQUEST_REIMPORT_FROM_SERVER = REQUEST_ROOT + "/reimport";

    private final static String[] quadTiles = new String[]{
            "00-0", "01-1", "02-2", "03-3",
            "10-4", "11-5", "12-6", "13-7",
            "20-8", "21-9", "22-10", "23-11",
            "30-12", "31-13", "32-14", "33-15"
    };

    @Autowired
    private OsmConverterService osmConverterService;

    @Value("${osm.data.location}")
    private String dataLocation;

    public static String getRequestReimportFromServer()
    {
        return REQUEST_REIMPORT_FROM_SERVER;
    }

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

    @RequestMapping(value = REQUEST_IMPORT_FULLY_NODES, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public GenericResponse importFullyNodes()
    {
        for (String cell : quadTiles)
        {
            osmConverterService.importNodes("by-quadtile/world-religion-muslim-node-by-quadtile-" + cell + ".osm");
        }

        return new GenericResponse("Import kicked off.");
    }

    @RequestMapping(value = REQUEST_IMPORT_FULLY_WAYS, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public GenericResponse importFullyWays()
    {
        for (String cell : quadTiles)
        {
            osmConverterService.importWays("by-quadtile/world-religion-muslim-way-by-quadtile-" + cell + ".osm");
        }

        return new GenericResponse("Import kicked off.");
    }

    @RequestMapping(value = REQUEST_REIMPORT_FROM_SERVER_NODE + "/{osmId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public GenericResponse reimportNodeFromServer(@PathVariable("osmId") String osmId)
    {
        LOGGER.info("About to reload OSM data with ID", osmId);

        osmConverterService.fetchAndImportNode(osmId);

        return new GenericResponse("O.K., Massa!");
    }

    @RequestMapping(value = REQUEST_REIMPORT_FROM_SERVER_WAY + "/{osmId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public GenericResponse reimportWayFromServer(@PathVariable("osmId") String osmId)
    {
        LOGGER.info("About to reload OSM data with ID", osmId);

        osmConverterService.fetchAndImportWay(osmId);

        return new GenericResponse("O.K., Massa!");
    }

    @RequestMapping(value = REQUEST_REIMPORT_FROM_SERVER + "/{osmId}", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public GenericResponse reimportFromServer(@PathVariable("osmId") String osmId)
    {
        osmId = StringUtils.trimToEmpty(osmId);

        if (osmId.length() > 14)
        {
            osmId = osmId.substring(2);
            while (osmId.charAt(0) == '0')
            {
                osmId = osmId.substring(1);
            }

            return reimportWayFromServer(osmId);
        }
        else
        {
            return reimportNodeFromServer(osmId);
        }
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
