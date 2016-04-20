package com.gurkensalat.osm.mosques.service;

import com.gurkensalat.osm.entity.Address;
import com.gurkensalat.osm.mosques.entity.OsmMosquePlace;
import com.gurkensalat.osm.mosques.repository.OsmMosquePlaceRepository;
import com.tandogan.geostuff.opencagedata.GeocodeRepository;
import com.tandogan.geostuff.opencagedata.entity.GeocodeResponse;
import com.tandogan.geostuff.opencagedata.entity.OpencageRate;
import com.tandogan.geostuff.opencagedata.entity.OpencageResult;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;

@Component
public class GeocodingServiceImpl implements GeocodingService
{
    private final static Logger LOGGER = LoggerFactory.getLogger(GeocodingServiceImpl.class);

    private int minimumRequestsToLeave = 100;

    @Autowired
    private GeocodeRepository geocodeRepository;

    @Autowired
    private OsmMosquePlaceRepository osmMosquePlaceRepository;

    @Override
    public GeocodeResponse reverse(String key)
    {
        GeocodeResponse response = null;

        if (geocodeRepository.getRemaining() < minimumRequestsToLeave)
        {
            if (DateTime.now().isAfter(geocodeRepository.getReset()))
            {
                LOGGER.info("API rate reset timeout arrived, resetting limit and trying again...");
            }
            else
            {
                LOGGER.info("Too little requests allowed left (less than {}), trying again later...", minimumRequestsToLeave);
                LOGGER.debug("  limit will be reset at {}, now it is {}", geocodeRepository.getReset(), DateTime.now());
                LOGGER.debug("  {} of {} queries remaining", geocodeRepository.getRemaining(), geocodeRepository.getLimit());

                return response;
            }
        }

        List<OsmMosquePlace> places = osmMosquePlaceRepository.findByKey(key);
        if ((places != null) && (places.size() > 0))
        {
            OsmMosquePlace place = places.get(0);

            try
            {
                // Keep track whether we already tried this place...
                place.setLastGeocodeAttempt(DateTime.now());

                response = geocodeRepository.reverse(place.getLat(), place.getLon());
                int confidence = -1;
                String countryCode = "";

                if (place.getAddress() == null)
                {
                    place.setAddress(new Address());
                }

                if ((response.getResults() == null) || (response.getResults().size() == 0))
                {
                    LOGGER.info("  NO RESULTS OBTAINED FOR PLACE {} / {} / {}", new Object[]{place.getKey(), place.getLat(), place.getLon()});
                }
                else
                {
                    for (OpencageResult result : response.getResults())
                    {
                        if (result.getConfidence() > confidence)
                        {
                            if (result.getComponents() != null)
                            {
                                countryCode = result.getComponents().getCountryCode();
                                confidence = result.getConfidence();
                            }
                        }
                    }

                    countryCode = trimToEmpty(countryCode).toUpperCase(Locale.ENGLISH);

                    place.setCountryFromGeocoding(countryCode);

                    if ("".equals(trimToEmpty(place.getCountryFromOSM())))
                    {
                        place.getAddress().setCountry(countryCode);
                        LOGGER.info("  OBTAINED COUNTRY CODE {}", place.getAddress().getCountry());
                    }
                }

                LOGGER.info("  COUNTRY CODES OSM: '{}', OCD: '{}', place: '{}'", new Object[]{
                        place.getCountryFromOSM(),
                        place.getCountryFromGeocoding(),
                        place.getAddress().getCountry()
                });

                // Save place
                place = osmMosquePlaceRepository.save(place);
            }
            catch (Exception e)
            {
                LOGGER.error("While reverse geocoding", e);
            }

            // TODO Kick off statistics run at some time?
        }

        if (response != null)
        {
            if (response.getRate() == null)
            {
                response.setRate(new OpencageRate());
            }

            response.getRate().setRemaining(geocodeRepository.getRemaining());
            response.getRate().setLimit(geocodeRepository.getLimit());
        }

        return response;
    }
}
