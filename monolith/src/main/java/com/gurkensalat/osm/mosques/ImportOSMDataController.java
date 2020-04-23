package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.mosques.service.OsmConverterResult;
import com.gurkensalat.osm.mosques.service.OsmConverterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/import")
@Slf4j
/* package protected */
class ImportOSMDataController
{
    @Autowired
    private OsmConverterService osmConverterService;

    @GetMapping("/nodes/{tile}")
    public OsmConverterResult nodes(@PathVariable String tile)
    {
        log.info("About to load some nodes from tile {}", tile);
        OsmConverterResult result = osmConverterService.importNodes("world-religion-muslim-node-by-quadtile-" + tile + ".osm");
        log.info("Loaded {}", result);
        return result;
    }
}
