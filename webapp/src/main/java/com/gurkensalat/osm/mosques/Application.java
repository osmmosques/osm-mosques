package com.gurkensalat.osm.mosques;

// import org.apache.commons.lang3.CharEncoding;
// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
// import org.springframework.boot.orm.jpa.EntityScan;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.ComponentScan;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.context.annotation.Import;
// import org.springframework.context.annotation.PropertySource;
// import org.springframework.context.annotation.PropertySources;
// import org.springframework.context.support.ResourceBundleMessageSource;
// import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
// import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.servlet.LocaleResolver;
// import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
// import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
// import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import com.gurkensalat.osm.entity.EntityComponentScanMarker;
import com.gurkensalat.osm.repository.RepositoryComponentScanMarker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
@Controller
@PropertySources({
        @PropertySource("classpath:application-default.properties"),
        @PropertySource(value = "file:/etc/webapps/osm-mosques/application-optional-override.properties", ignoreResourceNotFound = true),
        // @PropertySource("classpath:/opencagedata-default.properties"),
        @PropertySource(value = "file:${HOME}/.config/opencagedata", ignoreResourceNotFound = true)
})
@ComponentScan(basePackageClasses = {
        EntityComponentScanMarker.class,
        RepositoryComponentScanMarker.class
})
@EnableJpaRepositories(basePackageClasses = {RepositoryComponentScanMarker.class})
@EntityScan(basePackageClasses = {EntityComponentScanMarker.class})
public class Application
{
    public static void main(String[] args)
    {
        SpringApplication app = new SpringApplication(Application.class);

        app.setShowBanner(false);
        app.run(args);
    }

    @RequestMapping("/")
    @ResponseBody
    String home()
    {
        return "Hello World!";
    }
}
