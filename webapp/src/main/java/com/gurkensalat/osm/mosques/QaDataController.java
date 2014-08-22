package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.entity.DitibPlace;
import com.gurkensalat.osm.entity.LinkedPlace;
import com.gurkensalat.osm.entity.OsmPlace;
import com.gurkensalat.osm.entity.PlaceType;
import com.gurkensalat.osm.repository.DitibPlaceRepository;
import com.gurkensalat.osm.repository.LinkedPlaceRepository;
import com.gurkensalat.osm.repository.OsmPlaceRepository;
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
    private OsmPlaceRepository osmPlaceRepository;

    @RequestMapping(value = REQUEST_ROOT + "/details/{id}", method = RequestMethod.GET)
    public String linkedPlaceDetails(Model model, @PathVariable("id") String id)
    {
        LOGGER.info("osmPlaceRepository is {}", linkedPlaceRepository);

        LinkedPlace linkedPlace = linkedPlaceRepository.findOne(Long.parseLong(id));
        if (linkedPlace != null)
        {
            linkedPlace = sanitize(linkedPlace);
            model.addAttribute("place", linkedPlace);
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
            linkedPlace.setOsmPlace(sanitize("O", linkedPlace.getOsmPlace()));

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
            OsmPlace place = new OsmPlace("", PlaceType.OSM_CITY);

            String key = linkedPlace.getOsmId();
            if (isNotEmpty(key))
            {
                List<OsmPlace> osmPlaces = emptyIfNull(osmPlaceRepository.findByKey(key));
                if (osmPlaces.size() > 0)
                {
                    place = osmPlaces.get(0);
                    linkedPlace.setLat(place.getLat());
                    linkedPlace.setLon(place.getLon());
                }
            }

            linkedPlace.setOsmPlace(sanitize("", place));
        }

        return linkedPlace;
    }

    private DitibPlace sanitize(String prefix, DitibPlace place)
    {
        place.setName(prefix + ": " + trimToEmpty(place.getName()));
        place.setKey(prefix + ": " + trimToEmpty(place.getKey()));

        place.getAddress().setCity(prefix + ": " + trimToEmpty(place.getAddress().getCity()));
        place.getAddress().setStreet(prefix + ": " + trimToEmpty(place.getAddress().getStreet()));
        place.getAddress().setHousenumber(prefix + ": " + trimToEmpty(place.getAddress().getHousenumber()));

        return place;
    }

    private OsmPlace sanitize(String prefix, OsmPlace place)
    {
        place.setName(prefix + ": " + trimToEmpty(place.getName()));
        place.setKey(prefix + ": " + trimToEmpty(place.getKey()));

        place.getAddress().setCity(prefix + ": " + trimToEmpty(place.getAddress().getCity()));
        place.getAddress().setStreet(prefix + ": " + trimToEmpty(place.getAddress().getStreet()));
        place.getAddress().setHousenumber(prefix + ": " + trimToEmpty(place.getAddress().getHousenumber()));

        return place;
    }
}
