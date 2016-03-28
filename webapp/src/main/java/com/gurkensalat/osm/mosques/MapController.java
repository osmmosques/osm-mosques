package com.gurkensalat.osm.mosques;


import com.gurkensalat.osm.entity.OsmEntityType;
import com.gurkensalat.osm.entity.PlaceType;
import com.gurkensalat.osm.mosques.entity.OsmMosquePlace;
import com.gurkensalat.osm.mosques.repository.OsmMosquePlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.stripToEmpty;

@Controller
public class MapController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MapController.class);

    private final static String REQUEST_MAP = "/";

    private final static String REQUEST_MAP_UNASSIGNED_COUNTRY = "/unassigned-country";

    private final static String REQUEST_MAP_OSM_POPUP = "/osm-details-for-popup";

    private final static String REQUEST_MAP_UNASSIGNED_COUNTRY_OSM_POPUP = "/osm-details-unassigned-country-for-popup";

    private static String countryCodeToAdd = "TR";

    @Autowired
    private OsmMosquePlaceRepository osmMosquePlaceRepository;

    @RequestMapping(REQUEST_MAP)
    String map()
    {
        return "map";
    }

    @RequestMapping(REQUEST_MAP_UNASSIGNED_COUNTRY)
    String mapUnassignedCountry(@RequestParam(value = "countryCode", defaultValue = "TR", required = false) String countryCode)
    {
        countryCodeToAdd = countryCode;

        // TODO set 'flavour' property and include the main map html
        return "map-unassigned-country";
    }

    @RequestMapping(value = REQUEST_MAP_OSM_POPUP)
    String osmDetailsForPopup(Model model, @RequestParam(value = "placeKey", defaultValue = "0") String placeKey)
    {
        LOGGER.info("Requested place details for '{}'", placeKey);

        OsmMosquePlace place = findPlace(placeKey);
        populateModel(model, place);

        return "osm-details";
    }

    @RequestMapping(value = REQUEST_MAP_UNASSIGNED_COUNTRY_OSM_POPUP)
    String osmDetailsUnassignedCountryForPopup(Model model, @RequestParam(value = "placeKey", defaultValue = "0") String placeKey)
    {
        LOGGER.info("Requested place details for '{}'", placeKey);

        OsmMosquePlace place = findPlace(placeKey);
        populateModel(model, place);

        return "osm-details-unassigned-country";
    }

    private OsmMosquePlace findPlace(String placeKey)
    {
        OsmMosquePlace place = new OsmMosquePlace("dummy", PlaceType.OSM_PLACE_OF_WORSHIP);
        List<OsmMosquePlace> places = osmMosquePlaceRepository.findByKey(placeKey);
        if (!((places == null) || (places.size() == 0)))
        {
            place = places.get(0);
        }

        return place;
    }

    private void populateModel(Model model, OsmMosquePlace place)
    {
        String osmId = place.getKey();

        if (osmId.length() > 14)
        {
            osmId = osmId.substring(1);

            while (osmId.charAt(0) == '0')
            {
                osmId = osmId.substring(1);
            }
        }

        if ("".equals(stripToEmpty(place.getName())))
        {
            place.setName(place.getKey());
        }

        model.addAttribute("place", place);
        model.addAttribute("placeName", place.getName());
        model.addAttribute("placeKey", place.getKey());
        model.addAttribute("placeStreet", place.getAddress().getStreet());
        model.addAttribute("placeHouseNumber", place.getAddress().getHousenumber());
        model.addAttribute("placeCity", place.getAddress().getCity());
        model.addAttribute("placePostcode", place.getAddress().getPostcode());
        model.addAttribute("placeCountryCode", place.getAddress().getCountry());

        model.addAttribute("placeCountry", Countries.getCountries().getOrDefault(
                stripToEmpty(place.getAddress().getCountry().toLowerCase()), "Unassigned..."));

        model.addAttribute("placeType", "amenity - place_of_worship");

        model.addAttribute("_placeKey", place.getKey());
        model.addAttribute("_placeType", place.getType());

        UriComponentsBuilder editInJosmBuilder = UriComponentsBuilder.newInstance()
                // .scheme("http")
                // .port(8111)
                .scheme("https")
                .port(8112)
                .host("localhost")
                .path("load_and_zoom");

        if (place.getType() == OsmEntityType.WAY)
        {
            editInJosmBuilder.queryParam("select", "way" + osmId);
        }
        else
        {
            editInJosmBuilder.queryParam("select", "node" + osmId);
        }

        editInJosmBuilder.queryParam("left", place.getLon() - 0.001);
        editInJosmBuilder.queryParam("right", place.getLon() + 0.001);
        editInJosmBuilder.queryParam("top", place.getLat() + 0.001);
        editInJosmBuilder.queryParam("bottom", place.getLat() - 0.001);

        // http://localhost:8111/load_and_zoom?
        // addtags=wikipedia:de=Wei%C3%9Fe_Gasse%7Cmaxspeed=5&
        // select=way23071688,way23076176,way23076177,&
        // left=13.739727546842&
        // right=13.740890970188&
        // top=51.049987191025&
        // bottom=51.048466954325

        model.addAttribute("josmEditUrl", editInJosmBuilder.toUriString());

        // TODO this is still a bit hackish... The country should come from the session
        if ("".equals(stripToEmpty(place.getCountryFromOSM())))
        {
            if (!("".equals(countryCodeToAdd)))
            {
                editInJosmBuilder.queryParam("addtags", "addr:country=" + countryCodeToAdd);
            }
        }

        model.addAttribute("josmEditUrlUnassignedCountry", editInJosmBuilder.toUriString());


        // http://localhost:8888/rest/osm/reimport/3370089414
        // Actually, this needs some help from the thymleaf configuration...
        UriComponentsBuilder refreshFromServerBuilder = UriComponentsBuilder.newInstance()
                .path(OsmRestController.getRequestReimportFromServer() + "/" + place.getKey());

        model.addAttribute("refreshFromServerUrl", refreshFromServerBuilder.toUriString());

        UriComponentsBuilder detailsOnOSMBuilder = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("www.openstreetmap.org");

        if (place.getType() == OsmEntityType.WAY)
        {
            detailsOnOSMBuilder.path("way/" + osmId);
        }
        else
        {
            detailsOnOSMBuilder.path("node/" + osmId);
        }

        model.addAttribute("detailsOnOSM", detailsOnOSMBuilder.toUriString());
    }
}
