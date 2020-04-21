package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.entity.Address;
import com.gurkensalat.osm.entity.OsmEntityComponentScanMarker;
import com.gurkensalat.osm.mosques.entity.OsmMosquePlace;
import com.gurkensalat.osm.mosques.repository.OsmMosquePlaceRepository;
import com.gurkensalat.osm.mosques.repository.OsmMosqueRepositoryComponentScanMarker;
import com.gurkensalat.osm.repository.OsmRepositoryComponentScanMarker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.LocalDateTime;

@SpringBootApplication
@ComponentScan(basePackageClasses = {
    Monolith.class,
    OsmEntityComponentScanMarker.class,
    OsmMosqueRepositoryComponentScanMarker.class
})
@EnableJpaRepositories(basePackageClasses = {
    OsmRepositoryComponentScanMarker.class,
    OsmMosqueRepositoryComponentScanMarker.class
})
public class Monolith implements CommandLineRunner
{
    private static final Logger log = LoggerFactory.getLogger(Monolith.class);

    @Autowired
    private OsmMosquePlaceRepository osmMosquePlaceRepository;

    public static void main(String[] args)
    {
        SpringApplication.run(Monolith.class, args);
    }

    @Override
    public void run(String... args) throws Exception
    {
        log.info("Joining Thread, Ctrl+C to terminate...");
        log.info("osmMosquePlaceRepository is {}", osmMosquePlaceRepository);

        OsmMosquePlace place = osmMosquePlaceRepository.findByName("Bermuda Triangle").orElse(new OsmMosquePlace());
        if (place.getCreationTime() == null)
        {
            place.setCreationTime(LocalDateTime.now());
        }

        place.setName("Bermuda Triangle");
        place.setLat(25);
        place.setLon(-71);

        if (place.getAddress() == null)
        {
            place.setAddress(new Address());
        }
        place.getAddress().setCountry("XXX");

        log.info("Place before saving: {}", place);

        place.setModificationTime(LocalDateTime.now());
        place = osmMosquePlaceRepository.save(place);

        log.info("Place after saving: {}", place);

        Thread.currentThread().join();
    }
}
