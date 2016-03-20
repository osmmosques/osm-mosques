package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.entity.OsmNode;
import com.gurkensalat.osm.entity.OsmNodeTag;
import com.gurkensalat.osm.entity.OsmRoot;
import com.gurkensalat.osm.entity.OsmTag;
import com.gurkensalat.osm.entity.PlaceType;
import com.gurkensalat.osm.mosques.entity.OsmMosquePlace;
import com.gurkensalat.osm.mosques.repository.OsmMosquePlaceRepository;
import com.gurkensalat.osm.mosques.service.OsmConverterService;
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

    private final static String REQUEST_ROOT_INTERNAL = "/rest/internal/osm";

    private final static String REQUEST_IMPORT = REQUEST_ROOT_INTERNAL + "/import";

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

    @RequestMapping(value = REQUEST_IMPORT, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public GenericResponse importData()
    {
        return importData(null);
    }

    @RequestMapping(value = REQUEST_IMPORT + "/{path}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public GenericResponse importData(@PathVariable("path") String path)
    {
        osmConverterService.importData("world/world-religion-muslim-node-freshened.osm");

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
}
