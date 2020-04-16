package com.gurkensalat.osm.mosques;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.LinkDiscoverer;
import org.springframework.hateoas.LinkDiscoverers;
import org.springframework.plugin.core.SimplePluginRegistry;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SilenceHATEOAS
{
    @Bean
    public LinkDiscoverers discoverers()
    {
        List<LinkDiscoverer> plugins = new ArrayList<>();
        // part of spring-hateoas
        // org.springframework.hateoas.mediatype.collectionjson.CollectionJsonLinkDiscoverer ...
        // plugins.add(new CollectionJsonLinkDiscoverer());
        LinkDiscoverers result = new LinkDiscoverers(SimplePluginRegistry.create(plugins));

        return result;
    }
}
