package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.entity.DitibPlace;
import com.gurkensalat.osm.entity.EntityComponentScanMarker;
import com.gurkensalat.osm.repository.RepositoryComponentScanMarker;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Configuration
@PropertySources({
        @PropertySource("classpath:application-default.properties"),
        @PropertySource(value = "file:/etc/webapps/osm-mosques/application-optional-override.properties", ignoreResourceNotFound = true)
})
@EnableJpaRepositories(basePackageClasses = {RepositoryComponentScanMarker.class})
@EntityScan(basePackageClasses = {EntityComponentScanMarker.class})
@Import(RepositoryRestMvcConfiguration.class)
@ComponentScan(basePackageClasses = {EntityComponentScanMarker.class, RepositoryComponentScanMarker.class, ApplicationComponentScanMarker.class})
@EnableAutoConfiguration
@Controller
public class Application
{
    public static void main(String[] args)
    {
        SpringApplication app = new SpringApplication(Application.class);

        app.setShowBanner(false);
        app.run(args);
    }

    @RequestMapping("/")
    String index()
    {
        return "index";
    }
}
