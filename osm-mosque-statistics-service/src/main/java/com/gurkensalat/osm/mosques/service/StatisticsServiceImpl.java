package com.gurkensalat.osm.mosques.service;

import com.gurkensalat.osm.entity.Address;
import com.gurkensalat.osm.mosques.entity.OsmMosquePlace;
import com.gurkensalat.osm.mosques.entity.StatisticsEntry;
import com.gurkensalat.osm.mosques.repository.OsmMosquePlaceRepository;
import com.gurkensalat.osm.mosques.repository.StatisticsRepository;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Component
public class StatisticsServiceImpl implements StatisticsService
{
    private final static Logger LOGGER = LoggerFactory.getLogger(StatisticsServiceImpl.class);

    @Autowired
    private StatisticsRepository statisticsRepository;

    @Autowired
    private OsmMosquePlaceRepository osmMosquePlaceRepository;

    public void calculate()
    {

    /*
     * select
     *   ADDR_COUNTRY, COUNT(*) as COUNT, AVG(LAT) as CENTERPOINT_LAT, AVG(LON) as CENTERPOINT_LON, MIN(LON) as MIN_LON, MAX(LON) as MAX_LON, MIN(LAT) as MIN_LAT, MAX(LAT) AS MAX_LAT
     * from OSM_PLACES
     * group by ADDR_COUNTRY;
     */
        // Inefficient as hell, but will be improved later on
        statisticsRepository.invalidateAll();
        // statisticsRepository.deleteAllInvalid();

        HashMap<String, StatisticsEntry> entries = new HashMap<>();

        Iterable<OsmMosquePlace> places = osmMosquePlaceRepository.findAll();
        if (places != null)
        {
            for (OsmMosquePlace place : places)
            {
                if (place.getAddress() == null)
                {
                    // Actually should never happen but you never knwo...
                    place.setAddress(new Address());
                }

                String countryCode = place.getAddress().getCountry();
                if (isEmpty(countryCode))
                {
                    place.getAddress().setCountry("??");
                }

                if ("NULL".equals(countryCode))
                {
                    place.getAddress().setCountry("??");
                }

                countryCode = place.getAddress().getCountry();

                StatisticsEntry entry = entries.get(countryCode);
                if (entry == null)
                {
                    // Try to load entry from database
                    entry = findOrCreate(countryCode);

                    // Initialize all Integer attributes
                    entry.setOsmMosqueNodes(0);
                    entry.setOsmMosqueWays(0);

                    entry.setMinLat(1000);
                    entry.setMaxLat(-1000);

                    entry.setMinLon(1000);
                    entry.setMaxLon(-1000);

                    entry.setCentroidLat(0);
                    entry.setCentroidLon(0);

                    entry = statisticsRepository.save(entry);

                    entries.put(countryCode, entry);
                }

                // We already have a valid entry in the Map
                if (place.getKey().length() > 12)
                {
                    LOGGER.info("Key Length: {} , {}", place.getKey().length(), place.getKey());
                    entry.setOsmMosqueWays(entry.getOsmMosqueWays() + 1);
                }
                else
                {
                    entry.setOsmMosqueNodes(entry.getOsmMosqueNodes() + 1);
                }

                entry.setMinLat(Math.min(entry.getMinLat(), place.getLat()));
                entry.setMaxLat(Math.max(entry.getMaxLat(), place.getLat()));

                entry.setMinLon(Math.min(entry.getMinLon(), place.getLon()));
                entry.setMaxLon(Math.max(entry.getMaxLon(), place.getLon()));
            }
        }

        // Done, now save all entries...
        for (StatisticsEntry entry : entries.values())
        {
            try
            {
                entry.setCentroidLat((entry.getMinLat() + entry.getMaxLat()) / 2);
                entry.setCentroidLon((entry.getMinLon() + entry.getMaxLon()) / 2);

                entry.setValid(true);
                entry.setModificationTime(DateTime.now());
                entry = statisticsRepository.save(entry);
            }
            catch (Exception e)
            {
                LOGGER.error("While saving {}", entry, e);
            }
        }

        statisticsRepository.deleteAllInvalid();
    }

    private StatisticsEntry findOrCreate(String countryCode)
    {
        StatisticsEntry entry = null;

        try
        {
            List<StatisticsEntry> entries = statisticsRepository.findByCountryCode(countryCode);
            if ((entries == null) || (entries.size() == 0))
            {
                // Place could not be found, insert it...
                StatisticsEntry tempEntry = new StatisticsEntry();
                tempEntry.setCreationTime(DateTime.now());
                tempEntry.setModificationTime(DateTime.now());

                tempEntry.setCountryCode(countryCode);
                tempEntry.setCountryName(countryCode);

                // Initialize all Integer attributes
                tempEntry.setOsmMosqueNodes(0);
                tempEntry.setOsmMosqueWays(0);

                tempEntry.setMinLat(1000);
                tempEntry.setMaxLat(-1000);

                tempEntry.setMinLon(1000);
                tempEntry.setMaxLon(-1000);

                tempEntry.setCentroidLat(0);
                tempEntry.setCentroidLon(0);

                entry = statisticsRepository.save(tempEntry);
            }
            else
            {
                // take the one from the database and update it
                entry = entries.get(0);
                LOGGER.debug("Found pre-existing entity {} / {}", entry.getId(), entry.getVersion());
                entry = statisticsRepository.findOne(entry.getId());
                LOGGER.debug("  reloaded: {} / {}", entry.getId(), entry.getVersion());
            }

            entry.setValid(true);
            entry.setModificationTime(DateTime.now());
            entry = statisticsRepository.save(entry);
        }
        catch (Exception e)
        {
            LOGGER.error("While persisting statistics entry", e);
        }

        return entry;
    }
}