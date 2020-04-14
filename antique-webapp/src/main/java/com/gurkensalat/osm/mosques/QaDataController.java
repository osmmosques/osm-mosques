package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.entity.DitibPlace;
import com.gurkensalat.osm.entity.LinkedPlace;
import com.gurkensalat.osm.entity.OsmPlace;
import com.gurkensalat.osm.entity.PlaceType;
import com.gurkensalat.osm.mosques.entity.OsmMosquePlace;
import com.gurkensalat.osm.mosques.repository.OsmMosquePlaceRepository;
import com.gurkensalat.osm.repository.DitibPlaceRepository;
import com.gurkensalat.osm.repository.LinkedPlaceRepository;
import com.gurkensalat.osm.repository.OsmPlaceRepository;
import com.gurkensalat.osm.utils.DistanceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

import static com.gurkensalat.osm.utils.DistanceConstants.DELTA_LAT_LON_100_M;
import static org.apache.commons.collections4.ListUtils.emptyIfNull;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

@Controller
@EnableAutoConfiguration
public class QaDataController
{
    private final static Logger LOGGER = LoggerFactory.getLogger(QaDataController.class);

    private final static String REQUEST_ROOT = "/qadata";

    @Autowired
    private LinkedPlaceRepository linkedPlaceRepository;

    @Autowired
    private DitibPlaceRepository ditibPlaceRepository;

    @Autowired
    private OsmMosquePlaceRepository osmMosquePlaceRepository;

    @RequestMapping(value = REQUEST_ROOT + "/details/{id}", method = RequestMethod.GET)
    public String linkedPlaceDetails(Model model, @PathVariable("id") String id)
    {
        LOGGER.info("osmPlaceRepository is {}", linkedPlaceRepository);

        LinkedPlace linkedPlace = linkedPlaceRepository.findOne(Long.parseLong(id));
        if (linkedPlace != null)
        {
            linkedPlace = sanitize(linkedPlace);
            model.addAttribute("place", linkedPlace);

            OsmMosquePlace place = linkedPlace.getOsmMosquePlace();

            String josmURL = "http://localhost:8111/load_and_zoom";
            josmURL = josmURL + "?left=" + (place.getLon() - DELTA_LAT_LON_100_M);
            josmURL = josmURL + "&right=" + (place.getLon() + DELTA_LAT_LON_100_M);
            josmURL = josmURL + "&top=" + (place.getLat() + DELTA_LAT_LON_100_M);
            josmURL = josmURL + "&bottom=" + (place.getLat() - DELTA_LAT_LON_100_M);

            if (isNotEmpty(linkedPlace.getOsmId()))
            {
                model.addAttribute("osmNodeId", linkedPlace.getOsmId());

                // JOSM
                // http://localhost:8111/load_and_zoom?left=11.376857411825487&right=11.397907388173993&top=48.14261795621305&bottom=48.12967262780189&select=node494163800

                josmURL = josmURL + "&select=node" + linkedPlace.getOsmId();

                // OSM
                // http://www.openstreetmap.org/node/494163800
                String osmDetailsURL = "http://www.openstreetmap.org/node/" + linkedPlace.getOsmId();

                model.addAttribute("osmDetailsURL", osmDetailsURL);
            }

            model.addAttribute("josmURL", josmURL);

            // Id
            // http://www.openstreetmap.org/edit?editor=id&node=494163800
        }

        return "qadata-details";
    }

    @RequestMapping(value = REQUEST_ROOT + "/list", method = RequestMethod.GET)
    public String linkedPlaceList(Model model)
    {
        LOGGER.info("osmPlaceRepository is {}", linkedPlaceRepository);

        Iterable<LinkedPlace> places = linkedPlaceRepository.findAll();

        for (LinkedPlace linkedPlace : places)
        {
            sanitize(linkedPlace);

            linkedPlace.setDitibPlace(sanitize("D", linkedPlace.getDitibPlace()));
            linkedPlace.setOsmMosquePlace(sanitize("O", linkedPlace.getOsmMosquePlace()));

        }

        model.addAttribute("places", places);

        return "qadata-list";
    }

    private LinkedPlace sanitize(LinkedPlace linkedPlace)
    {
        {
            DitibPlace place = new DitibPlace("");

            String key = linkedPlace.getDitibCode();
            if (isNotEmpty(key))
            {
                List<DitibPlace> ditibPlaces = emptyIfNull(ditibPlaceRepository.findByKey(key));
                if (ditibPlaces.size() > 0)
                {
                    place = ditibPlaces.get(0);
                    linkedPlace.setLat(place.getLat());
                    linkedPlace.setLon(place.getLon());
                }
            }

            linkedPlace.setDitibPlace(sanitize("", place));
        }

        {
            OsmMosquePlace place = new OsmMosquePlace("", PlaceType.OSM_PLACE_OF_WORSHIP);

            String key = linkedPlace.getOsmId();
            if (isNotEmpty(key))
            {
                List<OsmMosquePlace> osmMosquePlaces = emptyIfNull(osmMosquePlaceRepository.findByKey(key));
                if (osmMosquePlaces.size() > 0)
                {
                    place = osmMosquePlaces.get(0);
                    linkedPlace.setLat(place.getLat());
                    linkedPlace.setLon(place.getLon());
                }
            }

            linkedPlace.setOsmMosquePlace(sanitize("", place));
        }

        return linkedPlace;
    }

    private DitibPlace sanitize(String prefix, DitibPlace place)
    {
        place.setName(trimToEmpty(place.getName()));
        place.setKey(trimToEmpty(place.getKey()));

        place.getAddress().setCity(trimToEmpty(place.getAddress().getCity()));
        place.getAddress().setStreet(trimToEmpty(place.getAddress().getStreet()));
        place.getAddress().setHousenumber(trimToEmpty(place.getAddress().getHousenumber()));

        if (isNotEmpty(prefix))
        {
            place.setName(prefix + ": " + place.getName());
            place.setName(prefix + ": " + place.getName());
            place.setKey(prefix + ": " + place.getKey());

            place.getAddress().setCity(prefix + ": " + place.getAddress().getCity());
            place.getAddress().setStreet(prefix + ": " + place.getAddress().getStreet());
            place.getAddress().setHousenumber(prefix + ": " + place.getAddress().getHousenumber());
        }

        return place;
    }

    private OsmMosquePlace sanitize(String prefix, OsmMosquePlace place)
    {
        place.setName(trimToEmpty(place.getName()));
        place.setKey(trimToEmpty(place.getKey()));

        place.getAddress().setCity(trimToEmpty(place.getAddress().getCity()));
        place.getAddress().setStreet(trimToEmpty(place.getAddress().getStreet()));
        place.getAddress().setHousenumber(trimToEmpty(place.getAddress().getHousenumber()));

        if (isNotEmpty(prefix))
        {
            place.setName(prefix + ": " + place.getName());
            place.setName(prefix + ": " + place.getName());
            place.setKey(prefix + ": " + place.getKey());

            place.getAddress().setCity(prefix + ": " + place.getAddress().getCity());
            place.getAddress().setStreet(prefix + ": " + place.getAddress().getStreet());
            place.getAddress().setHousenumber(prefix + ": " + place.getAddress().getHousenumber());
        }

        return place;
    }
}
