package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.mosques.entity.OsmMosquePlace;
import com.gurkensalat.osm.mosques.repository.OsmMosquePlaceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.util.StringUtils.isEmpty;

@RestController
@EnableAutoConfiguration
@Slf4j
public class MapRestController
{
    @Autowired
    private OsmMosquePlaceRepository osmMosquePlaceRepository;

    @GetMapping(value = "/rest/map/placemarkers/osm", produces = MediaType.APPLICATION_JSON_VALUE)
    @SuppressWarnings("java:S2293") // We can't use diamond operators on the return value...
    public ResponseEntity<List<MapDataEntry>> osmPlaceMarkers(
        @RequestParam(value = "minlat", defaultValue = "-90") String minlat,
        @RequestParam(value = "minlon", defaultValue = "-180") String minlon,
        @RequestParam(value = "maxlat", defaultValue = "90") String maxlat,
        @RequestParam(value = "maxlon", defaultValue = "180") String maxlon,
        @RequestParam(value = "zoom", defaultValue = "16", required = false) String zoomlevel
    )
    {
        List<MapDataEntry> result = new ArrayList<>();

        try
        {
            double minLat = Double.parseDouble(minlat);
            double maxLat = Double.parseDouble(maxlat);
            double minLon = Double.parseDouble(minlon);
            double maxLon = Double.parseDouble(maxlon);

            log.debug("  querying the repo for places between {} / {} / {} / {}", minLon, minLat, maxLon, maxLat);

            List<OsmMosquePlace> mosquePlaces = osmMosquePlaceRepository.findByBbox(minLon, minLat, maxLon, maxLat);
            if (!(isEmpty(mosquePlaces)))
            {
                for (OsmMosquePlace mosquePlace : mosquePlaces)
                {
                    MapDataEntry mapDataEntry = new MapDataEntry(mosquePlace);
                    if (isEmpty(mapDataEntry.getName()))
                    {
                        mapDataEntry.setName("OSM " + mosquePlace.getType() + " " + mosquePlace.getKey());
                    }

                    result.add(mapDataEntry);
                }
            }
        }
        catch (NumberFormatException e)
        {
            log.error("while asking for map data", e);
        }

        return new ResponseEntity<List<MapDataEntry>>(result, null, HttpStatus.OK);
    }
}
