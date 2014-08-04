package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.entity.DitibPlace;
import com.gurkensalat.osm.entity.Place;
import com.gurkensalat.osm.repository.DitibPlaceRepository;
import com.gurkensalat.osm.repository.PlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@EnableAutoConfiguration
public class OsmController
{
    private final static Logger LOGGER = LoggerFactory.getLogger(OsmController.class);

    private final static String REQUEST_ROOT = "/osm";

    @Autowired
    private PlaceRepository placeRepository;

    @RequestMapping(value = REQUEST_ROOT + "/list", method = RequestMethod.GET)
    public String osmList(Model model)
    {
        LOGGER.info("placeRepository is {}", placeRepository);

        model.addAttribute("repository", placeRepository);

        Iterable<Place> places = placeRepository.findAll();
        model.addAttribute("places", places);

        return "osm/list";
    }
}
