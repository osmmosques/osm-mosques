package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.mosques.service.GeocodingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
public class GeocodingRestController
{
    private final static Logger LOGGER = LoggerFactory.getLogger(GeocodingRestController.class);

    private final static String REQUEST_ROOT = "/rest/osm/geocode";

    private final static String REQUEST_GEOCODE_REVERSE = REQUEST_ROOT + "/reverse";

    @Autowired
    private GeocodingService geocodingService;

    @RequestMapping(value = REQUEST_GEOCODE_REVERSE + "/{placeId}", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public GenericResponse reverse(@PathVariable("placeId") String placeId)
    {
        LOGGER.debug("Request to reverse geocode {} arrived", placeId);

        geocodingService.reverse(placeId);

        return new GenericResponse("Reverse geocoding attempt kicked off.");
    }
}
