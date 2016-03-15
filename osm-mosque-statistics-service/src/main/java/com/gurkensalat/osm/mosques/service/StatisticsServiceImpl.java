package com.gurkensalat.osm.mosques.service;

import com.gurkensalat.osm.entity.OsmPlace;
import com.gurkensalat.osm.mosques.entity.StatisticsEntry;
import com.gurkensalat.osm.mosques.repository.StatisticsRepository;
import com.gurkensalat.osm.repository.OsmPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StatisticsServiceImpl implements StatisticsService
{
    private final static Logger LOGGER = LoggerFactory.getLogger(StatisticsServiceImpl.class);

    @Autowired
    private StatisticsRepository statisticsRepository;

    @Autowired
    private OsmPlaceRepository osmPlaceRepository;

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
        statisticsRepository.deleteAllInvalid();

        Iterable<OsmPlace> places = osmPlaceRepository.findAll();
        if (places != null)
        {
            for (OsmPlace place : places)
            {
                String countryCode = place.getAddress().getCountry();
                StatisticsEntry entry = persistStatisticsEntry(countryCode);

                entry.setOsmMosqueNodes(entry.getOsmMosqueNodes() + 1);

                entry.setMinLat(Math.min(entry.getMinLat(), place.getLat()));
                entry.setMaxLat(Math.max(entry.getMaxLat(), place.getLat()));

                entry.setMinLon(Math.min(entry.getMinLon(), place.getLon()));
                entry.setMaxLon(Math.max(entry.getMaxLon(), place.getLon()));

                entry.setCentroidLat((entry.getMinLat() + entry.getMaxLat()) / 2);
                entry.setCentroidLon((entry.getMinLon() + entry.getMaxLon()) / 2);

                entry = statisticsRepository.save(entry);
            }
        }

        // StatisticsEntry statisticsEntry = persistStatisticsEntry(countryCode, country);

        // statisticsEntry.setOsmMosqueNodes(root.getNodes().size());
        // statisticsEntry = statisticsRepository.save(statisticsEntry);
    }

    private StatisticsEntry persistStatisticsEntry(String countryCode)
    {
        StatisticsEntry entry = null;

        try
        {
            List<StatisticsEntry> entries = statisticsRepository.findByCountryCode(countryCode);
            if ((entries == null) || (entries.size() == 0))
            {
                // Place could not be found, insert it...
                StatisticsEntry tempEntry = new StatisticsEntry();
                tempEntry.setCountryCode(countryCode);
                tempEntry.setCountryName(countryCode);

                // Initialize all Integer attributes
                tempEntry.setOsmMosqueNodes(0);

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
            entry = statisticsRepository.save(entry);
        }
        catch (Exception e)
        {
            LOGGER.error("While persisting statistics entry", e);
        }

        return entry;
    }
}