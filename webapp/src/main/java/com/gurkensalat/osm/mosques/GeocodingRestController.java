package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.mosques.entity.OsmMosquePlace;
import com.gurkensalat.osm.mosques.jobs.CountryCodeReverseGeocoder;
import com.gurkensalat.osm.mosques.repository.OsmMosquePlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@EnableAutoConfiguration
public class GeocodingRestController
{
    private final static Logger LOGGER = LoggerFactory.getLogger(GeocodingRestController.class);

    private final static String REQUEST_ROOT = "/rest/osm/geocode";

    private final static String REQUEST_INTERNAL_ROOT = "/rest/internal/osm/geocode";

    private final static String REQUEST_GEOCODE_REVERSE_ENQUEUE = REQUEST_INTERNAL_ROOT + "/enqueue";

    private final static String REQUEST_GEOCODE_REVERSE = REQUEST_INTERNAL_ROOT + "/reverse";

    @Autowired
    private CountryCodeReverseGeocoder countryCodeReverseGeocoder;

    @Autowired
    private OsmMosquePlaceRepository osmMosquePlaceRepository;

    @RequestMapping(value = REQUEST_GEOCODE_REVERSE + "/{placeId}", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public GenericResponse reverse(@PathVariable("placeId") String placeId)
    {
        LOGGER.debug("Request to reverse geocode {} arrived", placeId);

        countryCodeReverseGeocoder.enqueue(placeId);

        return new GenericResponse("Reverse geocoding attempt kicked off.");
    }

    @RequestMapping(value = REQUEST_GEOCODE_REVERSE_ENQUEUE + "/byAge", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public GenericResponse enqueueByAge()
    {
        LOGGER.debug("Request to update addr_country_from_geocoding arrived");

        osmMosquePlaceRepository.emptyIfNullCountryCodeFromGeocoding();
        List<OsmMosquePlace> reverseCountryGeocodingCandidates = osmMosquePlaceRepository.reverseCountryGeocodingCandidatesByAge(new PageRequest(0, 20));
        for (OsmMosquePlace candidate : reverseCountryGeocodingCandidates)
        {
            countryCodeReverseGeocoder.enqueue(candidate.getKey());
        }

        return new GenericResponse("Reverse geocoding attempt kicked off.");
    }

    @RequestMapping(value = REQUEST_GEOCODE_REVERSE_ENQUEUE + "/byLongitude", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public GenericResponse enqueueByLongitude()
    {
        LOGGER.debug("Request to update addr_country_from_geocoding arrived");

        osmMosquePlaceRepository.emptyIfNullCountryCodeFromGeocoding();
        List<OsmMosquePlace> reverseCountryGeocodingCandidates = osmMosquePlaceRepository.reverseCountryGeocodingCandidatesByLongitude(new PageRequest(0, 20));
        for (OsmMosquePlace candidate : reverseCountryGeocodingCandidates)
        {
            countryCodeReverseGeocoder.enqueue(candidate.getKey());
        }

        return new GenericResponse("Reverse geocoding attempt kicked off.");
    }

    @RequestMapping(value = REQUEST_GEOCODE_REVERSE_ENQUEUE + "/cyprus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public GenericResponse enqueueByCountryCyprus()
    {
        return enqueueByBbox(BBox.CYPRUS_MIN_LONGITUDE, BBox.CYPRUS_MIN_LATITUDE, BBox.CYPRUS_MAX_LONGITUDE, BBox.CYPRUS_MAX_LATITUDE, 100);
    }

    @RequestMapping(value = REQUEST_GEOCODE_REVERSE_ENQUEUE + "/turkey", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public GenericResponse enqueueByCountryTurkey()
    {
        return enqueueByBbox(BBox.TURKEY_MIN_LONGITUDE, BBox.TURKEY_MIN_LATITUDE, BBox.TURKEY_MAX_LONGITUDE, BBox.TURKEY_MAX_LATITUDE, 1000);
    }

    private GenericResponse enqueueByBbox(double minLongitude, double minLatitude, double maxLongitude, double maxLatitude, int requestSize)
    {
        LOGGER.debug("Request to update addr_country_from_geocoding arrived");

        osmMosquePlaceRepository.emptyIfNullCountryCodeFromGeocoding();
        List<OsmMosquePlace> reverseCountryGeocodingCandidates = osmMosquePlaceRepository.reverseCountryGeocodingCandidates(
                new PageRequest(0, requestSize),
                minLongitude, minLatitude,
                maxLongitude, maxLatitude);

        for (OsmMosquePlace candidate : reverseCountryGeocodingCandidates)
        {
            countryCodeReverseGeocoder.enqueue(candidate.getKey());
        }

        return new GenericResponse("Reverse geocoding attempt kicked off.");
    }
}
