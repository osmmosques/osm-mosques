package com.gurkensalat.osm.mosques.jobs;

import com.gurkensalat.osm.mosques.entity.OsmMosquePlace;
import com.gurkensalat.osm.mosques.repository.OsmMosquePlaceRepository;
import com.gurkensalat.osm.mosques.service.GeocodingService;
import com.tandogan.geostuff.opencagedata.entity.GeocodeResponse;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.stripToEmpty;

@Configuration
@Component
public class CountryCodeReverseGeocoderHandler
{
    private final static Logger LOGGER = LoggerFactory.getLogger(CountryCodeReverseGeocoderHandler.class);

    @Value("${mq.queue.reverse-geocode-countrycode.minsleep}")
    private Integer minsleep;

    @Value("${mq.queue.reverse-geocode-countrycode.randomsleep}")
    private Integer randomsleep;

    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    private OsmMosquePlaceRepository osmMosquePlaceRepository;

    @Autowired
    private SlackNotifier slackNotifier;

    @RabbitListener(queues = "${mq.queue.reverse-geocode-countrycode.name}")
    public void handleMessage(TaskMessage message)
    {
        LOGGER.info("  received <{}>", message);

        String key = message.getMessage();
        List<OsmMosquePlace> places = osmMosquePlaceRepository.findByKey(key);
        if (places != null)
        {
            for (OsmMosquePlace place : places)
            {
                LOGGER.info("  have to handle: {}", place);

                // TODO we might use the message kind to select whether to re-geocode known places
                if ("".equals(stripToEmpty(place.getCountryFromGeocoding())))
                {
                    // Do some magic... Maybe the service should not write directly to the database...
                    GeocodeResponse response = geocodingService.reverse(key);

                    // Reload the place, it might have been changed by the geocodingService
                    place = osmMosquePlaceRepository.findOne(place.getId());

                    // did what we had to, now mark this place as "touched"
                    place.setLastGeocodeAttempt(DateTime.now());
                    place = osmMosquePlaceRepository.save(place);

                    long sleeptime = (long) (minsleep + (Math.random() * (double) randomsleep));

                    LOGGER.debug("Processing finished, sleeping a while ({} seconds)...", sleeptime);

                    try
                    {
                        Thread.sleep(sleeptime * 1000);
                    }
                    catch (InterruptedException ie)
                    {
                        // Not really important...
                    }

                    if (response.getRate() != null)
                    {
                        if (response.getRate().getRemaining() < 100)
                        {
                            slackNotifier.notify(SlackNotifier.CHANNEL_GEOCODING, "Too little attempts left (" + response.getRate().getRemaining() + " out of " + response.getRate().getLimit() + "), sleeping for half an hour");

                            try
                            {
                                Thread.sleep(30 * 60 * 1000);
                            }
                            catch (InterruptedException ie)
                            {
                                // Not really important...
                            }
                        }
                    }
                }
            }
        }

        LOGGER.info("  done with handleMessage <{}>", message);
    }
}
