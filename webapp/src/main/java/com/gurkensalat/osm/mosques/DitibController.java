package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.entity.DitibPlace;
import com.gurkensalat.osm.repository.DitibPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

@Controller
@EnableAutoConfiguration
public class DitibController
{
    private final static Logger LOGGER = LoggerFactory.getLogger(DitibController.class);

    private final static String REQUEST_ROOT = "/ditib";

    @Autowired
    private DitibPlaceRepository ditibPlaceRepository;

    @RequestMapping(value = REQUEST_ROOT + "/list", method = RequestMethod.GET)
    public String ditibList(Model model)
    {
        model.addAttribute("repository", ditibPlaceRepository);

        Iterable<DitibPlace> places = ditibPlaceRepository.findAll();
        model.addAttribute("places", places);

        return "ditib-list";
    }

    @RequestMapping(value = REQUEST_ROOT + "/list-already-geocoded", method = RequestMethod.GET)
    public String ditibListAlreadyGeocoded(Model model)
    {
        model.addAttribute("repository", ditibPlaceRepository);

        Iterable<DitibPlace> places = ditibPlaceRepository.findAll();

        // Actually, investigate FluentIterable here...
        List<DitibPlace> alreadyGeocodedPlaces = new ArrayList();
        for (DitibPlace place : places)
        {
            if (place.isGeocoded())
            {
                alreadyGeocodedPlaces.add(place);
            }
        }

        model.addAttribute("places", alreadyGeocodedPlaces);

        return "ditib-list";
    }

    @RequestMapping(value = REQUEST_ROOT + "/list-to-be-geocoded", method = RequestMethod.GET)
    public String ditibListToBeGeocoded(Model model)
    {
        model.addAttribute("repository", ditibPlaceRepository);

        Iterable<DitibPlace> places = ditibPlaceRepository.findAll();

        // Actually, investigate FluentIterable here...
        List<DitibPlace> toBeGeocodedPlaces = new ArrayList();
        for (DitibPlace place : places)
        {
            if (!(place.isGeocoded()))
            {
                toBeGeocodedPlaces.add(place);
            }
        }

        model.addAttribute("places", toBeGeocodedPlaces);

        return "ditib-list";
    }
}
