package com.gurkensalat.osm.mosques.service;

import com.tandogan.geostuff.opencagedata.GeocodeRepository;
import com.tandogan.geostuff.opencagedata.entity.GeocodeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GeocodingServiceImpl implements GeocodingService
{
    private final static Logger LOGGER = LoggerFactory.getLogger(GeocodingServiceImpl.class);

    @Autowired
    private GeocodeRepository geocodeRepository;

    @Override
    public GeocodeResponse reverse(String key)
    {
        return null;
    }
}