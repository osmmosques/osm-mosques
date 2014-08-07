package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.entity.EntityComponentScanMarker;
import com.gurkensalat.osm.repository.RepositoryComponentScanMarker;
import com.tandogan.geostuff.opencagedata.GeocodeRepositoryComponentScanMarker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

@Configuration
@PropertySources({
        @PropertySource("classpath:application-default.properties"),
        @PropertySource(value = "file:/etc/webapps/osm-mosques/application-optional-override.properties", ignoreResourceNotFound = true),
        @PropertySource("classpath:/opencagedata-default.properties"),
        @PropertySource(value = "file:${HOME}/.config/opencagedata", ignoreResourceNotFound = true)
})
@EnableJpaRepositories(basePackageClasses = {RepositoryComponentScanMarker.class})
@EntityScan(basePackageClasses = {EntityComponentScanMarker.class})
@Import(RepositoryRestMvcConfiguration.class)
@ComponentScan(basePackageClasses = {
        EntityComponentScanMarker.class,
        RepositoryComponentScanMarker.class,
        GeocodeRepositoryComponentScanMarker.class,
        ApplicationComponentScanMarker.class
})
@EnableAutoConfiguration
public class Application
{
    // http://stackoverflow.com/questions/23366226/spring-boot-w-jpa-move-entity-to-different-package
    // solved the issue of EntityScan
    public static void main(String[] args)
    {
        SpringApplication app = new SpringApplication(Application.class);

        app.setShowBanner(false);
        app.run(args);
    }
}
