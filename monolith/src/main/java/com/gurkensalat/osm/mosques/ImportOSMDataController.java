package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.mosques.messaging.OsmServiceMessaging;
import com.gurkensalat.osm.mosques.service.OsmConverterResult;
import com.gurkensalat.osm.mosques.service.OsmConverterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/internal/import")
@Slf4j
/* package protected */
class ImportOSMDataController
{
    @Autowired
    @Qualifier(OsmServiceMessaging.KIND_ASYNC)
    private OsmConverterService osmConverterService;

    @GetMapping("/quadtile/nodes/{tile}")
    public OsmConverterResult nodesInQuadtile(@PathVariable String tile)
    {
        log.info("About to load some nodes from tile {}", tile);
        OsmConverterResult result = osmConverterService.importNodes("world-religion-muslim-node-by-quadtile-" + tile + ".osm");
        log.info("Loaded {}", result);
        return result;
    }

    @GetMapping("/quadtile/ways/{tile}")
    public OsmConverterResult waysInQuadtile(@PathVariable String tile)
    {
        log.info("About to load some nodes from tile {}", tile);
        OsmConverterResult result = osmConverterService.importWays("world-religion-muslim-way-by-quadtile-" + tile + ".osm");
        log.info("Loaded {}", result);
        return result;
    }

    // TODO we need a public endpoint for this too
    @GetMapping("/online/node/{id}")
    public OsmConverterResult nodeWithIdFromAPI(@PathVariable String id)
    {
        log.info("About to load a node with id {}", id);
        OsmConverterResult result = osmConverterService.fetchAndImportNode(id);
        log.info("Loaded {}", result);
        return result;
    }

    // TODO we need a public endpoint for this too
    @GetMapping("/online/way/{id}")
    public OsmConverterResult wayWithIdFromAPI(@PathVariable String id)
    {
        log.info("About to load a way with id {}", id);
        OsmConverterResult result = osmConverterService.fetchAndImportWay(id);
        log.info("Loaded {}", result);
        return result;
    }
}
