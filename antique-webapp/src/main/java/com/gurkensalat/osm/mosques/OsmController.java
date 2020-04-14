package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.mosques.entity.OsmMosquePlace;
import com.gurkensalat.osm.mosques.repository.OsmMosquePlaceRepository;
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
    private OsmMosquePlaceRepository osmMosquePlaceRepository;

    @RequestMapping(value = REQUEST_ROOT + "/list", method = RequestMethod.GET)
    public String osmList(Model model)
    {
        LOGGER.info("osmMosquePlaceRepository is {}", osmMosquePlaceRepository);

        model.addAttribute("repository", osmMosquePlaceRepository);

        Iterable<OsmMosquePlace> places = osmMosquePlaceRepository.findAll();
        model.addAttribute("places", places);

        return "osm-list";
    }
}
