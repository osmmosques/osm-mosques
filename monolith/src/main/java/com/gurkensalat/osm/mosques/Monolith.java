package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.entity.OsmEntityComponentScanMarker;
import com.gurkensalat.osm.mosques.repository.OsmMosqueRepositoryComponentScanMarker;
import com.gurkensalat.osm.repository.OsmParserRepositoryComponentScanMarker;
import com.gurkensalat.osm.repository.OsmRepositoryComponentScanMarker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackageClasses = {
    OsmParserRepositoryComponentScanMarker.class
})
@ComponentScan(basePackageClasses = {
    Monolith.class,
    OsmEntityComponentScanMarker.class,
    OsmMosqueRepositoryComponentScanMarker.class
},
basePackages = {
    "freemarker"
})
@EnableJpaRepositories(basePackageClasses = {
    OsmRepositoryComponentScanMarker.class,
    OsmMosqueRepositoryComponentScanMarker.class
})
@Slf4j
public class Monolith
{
    public static void main(String[] args)
    {
        SpringApplication.run(Monolith.class, args);
    }
}
