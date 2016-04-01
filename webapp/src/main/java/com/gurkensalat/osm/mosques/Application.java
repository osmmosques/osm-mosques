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
import com.gurkensalat.osm.mosques.entity.OsmMosqueEntityComponentScanMarker;
import com.gurkensalat.osm.mosques.entity.StatisticsEntityComponentScanMarker;
import com.gurkensalat.osm.mosques.repository.OsmMosqueRepositoryComponentScanMarker;
import com.gurkensalat.osm.mosques.repository.StatisticsRepositoryComponentScanMarker;
import com.gurkensalat.osm.mosques.service.GeocodingServiceComponentScanMarker;
import com.gurkensalat.osm.repository.RepositoryComponentScanMarker;
import com.tandogan.geostuff.opencagedata.GeocodeRepositoryComponentScanMarker;
import com.tandogan.geostuff.opencagedata.entity.GeocodeEntityComponentScanMarker;
import org.apache.commons.lang3.CharEncoding;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@SpringBootApplication
@Controller
@PropertySources({
        @PropertySource("classpath:application-default.properties"),
        @PropertySource(value = "file:/etc/webapps/osm-mosques/application-optional-override.properties", ignoreResourceNotFound = true),
        @PropertySource("classpath:/opencagedata-default.properties"),
        @PropertySource(value = "file:${HOME}/.config/opencagedata", ignoreResourceNotFound = true)
})
@ComponentScan(basePackageClasses = {
        ApplicationComponentScanMarker.class,
        EntityComponentScanMarker.class,
        RepositoryComponentScanMarker.class,
        StatisticsRepositoryComponentScanMarker.class,
        OsmMosqueEntityComponentScanMarker.class,
        OsmMosqueRepositoryComponentScanMarker.class,
        GeocodeEntityComponentScanMarker.class,
        GeocodeRepositoryComponentScanMarker.class,
        GeocodingServiceComponentScanMarker.class
})
@EnableJpaRepositories(basePackageClasses = {RepositoryComponentScanMarker.class, StatisticsRepositoryComponentScanMarker.class})
@EntityScan(basePackageClasses = {EntityComponentScanMarker.class, StatisticsEntityComponentScanMarker.class})
@EnableRabbit
public class Application extends WebMvcConfigurerAdapter implements RabbitListenerConfigurer
{
    public static void main(String[] args)
    {
        SpringApplication app = new SpringApplication(Application.class);

        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

    @Bean
    public LocaleResolver localeResolver()
    {
        // AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(Locale.US);
        return localeResolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor()
    {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    @Bean
    public ResourceBundleMessageSource messageSource()
    {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n/messages");
        messageSource.setFallbackToSystemLocale(false);
        messageSource.setDefaultEncoding(CharEncoding.UTF_8);
        return messageSource;
    }

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar)
    {
        registrar.setMessageHandlerMethodFactory(myHandlerMethodFactory());
    }

    @Bean
    public DefaultMessageHandlerMethodFactory myHandlerMethodFactory()
    {
        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
        factory.setMessageConverter(jackson2Converter());
        return factory;
    }

    @Bean
    public MappingJackson2MessageConverter jackson2Converter()
    {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        return converter;
    }
}
